package edu.gemini.aspen.giapi.statusservice;

import com.google.common.base.Optional;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import edu.gemini.aspen.giapi.status.Health;
import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.impl.BasicStatus;
import edu.gemini.aspen.giapi.status.impl.HealthStatus;
import edu.gemini.aspen.giapi.statusservice.generated.DataType;
import edu.gemini.aspen.giapi.statusservice.generated.MapType;
import edu.gemini.aspen.giapi.statusservice.generated.StatusType;
import edu.gemini.aspen.giapi.util.jms.status.StatusGetter;
import edu.gemini.gmp.top.Top;
import net.jmatrix.eproperties.EProperties;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base class for building status translators
 */
abstract public class AbstractStatusItemTranslator implements StatusItemTranslator {
    private static final Logger LOG = Logger.getLogger(AbstractStatusItemTranslator.class.getName());
    private static final String CONF_DIR_PROPERTY = "statusTranslatorFile";


    /**
     * This class is needed to allow for translations of one StatusItem mapping to multiple ones,
     * as well as several different items mapping to a single one.
     */
    private static class Key {
        private String original;
        private String translated;

        static Key create(String original, String translated) {
            return new Key(original, translated);
        }

        private Key(String original, String translated) {
            this.original = original;
            this.translated = translated;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            if (!original.equals(key.original)) return false;
            if (!translated.equals(key.translated)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = original.hashCode();
            result = 31 * result + translated.hashCode();
            return result;
        }
    }

    /**
     * Maps original StatusItem name to 1 or more translated names
     */
    private final Multimap<String, String> names = HashMultimap.create();
    /**
     * Maps the name of the translation to its type
     */
    private final Map<Key, DataType> types = new HashMap<Key, DataType>();
    /**
     * Maps the name of a translation to the actual translations
     */
    private final Map<Key, Map<String, String>> translations = new HashMap<Key, Map<String, String>>();
    /**
     * Maps the name of a translation to its default values
     */
    private final Map<Key, String> defaults = new HashMap<Key, String>();

    private final String xmlFileName;
    private final String name = "StatusItemTranslator: " + this;
    protected final Top top;
    protected StatusItemTranslatorConfiguration config;
    protected final StatusGetter getter = new StatusGetter("Status Translator initial item loader");
    protected final AtomicBoolean jmsStarted = new AtomicBoolean(false);

    public AbstractStatusItemTranslator(Top top, String xmlFileName) {
        this.top = top;
        this.xmlFileName = xmlFileName;
    }

    static protected void waitFor(AtomicBoolean bool) {
        long sleepTime = 100;
        do {
            if (!bool.get()) {
                try {
                    Thread.sleep(sleepTime *= 2);
                } catch (InterruptedException e) {
                    LOG.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        } while (!bool.get());
    }

    /**
     * Reads in the configuration and stores it for using it in  translations
     *
     * @throws IOException
     * @throws JAXBException
     */
    public void start() throws IOException, JAXBException, SAXException {
        File f = new File(substituteProperties(xmlFileName));
        if (!f.exists()) {
            LOG.severe("Configuration file " + f + " does not exist");
            return;
        }
        LOG.info("Start StatusItemTranslator with configuration file " + f);

        //read mappings
        config = new StatusItemTranslatorConfiguration(new FileInputStream(f));

        // initialize mappings
        for (StatusType status : config.getStatuses()) {
            Map<String, String> tr = new HashMap<String, String>();
            for (MapType map : status.getMaps().getMap()) {
                tr.put(map.getFrom(), map.getTo());
            }
            translations.put(Key.create(top.buildStatusItemName(status.getOriginalName()), top.buildStatusItemName(status.getTranslatedName())), tr);
            if (status.getDefault() != null) {
                defaults.put(Key.create(top.buildStatusItemName(status.getOriginalName()), top.buildStatusItemName(status.getTranslatedName())), status.getDefault());
            }
        }

        //store types and names
        for (StatusType status : config.getStatuses()) {
            types.put(Key.create(top.buildStatusItemName(status.getOriginalName()), top.buildStatusItemName(status.getTranslatedName())), status.getTranslatedType());
            names.put(top.buildStatusItemName(status.getOriginalName()), top.buildStatusItemName(status.getTranslatedName()));
        }
    }

    /**
     * Try to fetch items from the StatusDB at startup. Translate those found.
     */
    protected void initItems() {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                LOG.fine("Start initItems");
                try {
                    waitFor(jmsStarted);//wait until JMS is started

                    long sleepTime = 100;
                    Collection<StatusItem> items = null;
                    do {
                        items = getter.getAllStatusItems();
                        if (items == null) {
                            LOG.warning("Couldn't get StatusItems from StatusDB, sleeping...");
                            Thread.sleep(sleepTime *= 2);
                        }
                    } while (items == null);//wait until JMS on receiver end is started

                    for (StatusItem<?> item : items) {
                        update(item);
                    }
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, e.getMessage(), e);
                }
                LOG.fine("End initItems");
            }
        });
    }

    private String substituteProperties(String url) {
        EProperties props = new EProperties();
        props.addAll(System.getProperties());
        props.put(CONF_DIR_PROPERTY, url);
        return props.get(CONF_DIR_PROPERTY, "/").toString();
    }

    public void stop() {

    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Create a StatusItem
     *
     * @param type    DataType of the item to create
     * @param newName name of the item
     * @param newVal  value of the item
     * @return a Some<StatusItem<?>> if everything is correct, otherwise a None.
     */
    private Optional<StatusItem<?>> createStatus(DataType type, String newName, String newVal) {
        StatusItem<?> newItem = null;
        try {
            switch (type) {
                case INT:
                    newItem = new BasicStatus<Integer>(newName, Integer.valueOf(newVal));
                    break;
                case FLOAT:
                    newItem = new BasicStatus<Float>(newName, Float.valueOf(newVal));
                    break;
                case DOUBLE:
                    newItem = new BasicStatus<Double>(newName, Double.valueOf(newVal));
                    break;
                case STRING:
                    newItem = new BasicStatus<String>(newName, newVal);
                    break;
                case HEALTH:
                    newItem = new HealthStatus(newName, Health.valueOf(newVal));
                    break;
            }
        } catch (NumberFormatException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        } catch (NullPointerException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
        if (newItem == null) {
            return Optional.absent();
        } else {
            return Optional.<StatusItem<?>>of(newItem);
        }
    }

    /**
     * Translate a StatusItem according to translations specified in the config file.
     *
     * @param item the item to translate
     * @param <T>
     * @return a Some<StatusItem<?>> with the translated item, or a None if a problem occured
     */
    protected <T> List<StatusItem<?>> translate(StatusItem<T> item) {
        List<StatusItem<?>> list = new ArrayList<StatusItem<?>>();
        for (String newName : names.get(item.getName())) {
            LOG.fine("Translating " + item);

            //translate
            String newVal = translations.get(Key.create(item.getName(),newName)).get(item.getValue().toString());

            //no translation, return default
            if (newVal == null) {
                if (defaults.get(Key.create(item.getName(),newName)) != null) {
                    Optional<StatusItem<?>> newItem = createStatus(types.get(Key.create(item.getName(),newName)), newName, defaults.get(Key.create(item.getName(),newName)));
                    if (newItem.isPresent()) {
                        list.add(newItem.get());
                    }
                }
            } else {
                //return proper translation
                Optional<StatusItem<?>> newItem = createStatus(types.get(Key.create(item.getName(),newName)), newName, newVal);
                if (newItem.isPresent()) {
                    list.add(newItem.get());
                }
            }
        }
        return list;
    }
}
