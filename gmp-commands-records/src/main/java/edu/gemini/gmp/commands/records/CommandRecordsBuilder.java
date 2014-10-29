package edu.gemini.gmp.commands.records;

import edu.gemini.aspen.giapi.commands.CommandSender;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.gmp.commands.records.generated.ApplyType;
import edu.gemini.gmp.commands.records.generated.ConfigRecordType;
import edu.gemini.gmp.commands.records.generated.Records;
import edu.gemini.gmp.commands.records.generated.SequenceCommandType;
import edu.gemini.gmp.top.Top;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.epics.api.Channel;
import edu.gemini.epics.api.ChannelListener;
import gov.aps.jca.CAException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class RecordFactory
 *
 * @author Nicolas A. Barriga
 *         Date: 4/20/12
 */
public class CommandRecordsBuilder {
    private static final Logger LOG = Logger.getLogger(CommandRecordsBuilder.class.getName());
    private List<ApplyRecord> applys = new ArrayList<ApplyRecord>();
    private final String resetRecordsName = "gmp:resetRecords";
    private Channel<Reset> reset;
    private final Top epicsTop;
    private final ChannelAccessServer cas;

    public CommandRecordsBuilder(ChannelAccessServer cas,
                                 CommandSender cs,
                                 Top epicsTop,
                                 String xmlFileName) {

        LOG.info("Constructing RecordFactory");
        this.cas = cas;
        this.epicsTop = epicsTop;
        Records records;
        try {
            JAXBContext jc = JAXBContext.newInstance(Records.class);
            Unmarshaller um = jc.createUnmarshaller();
            SchemaFactory factory =
                    SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            Schema schema = factory.newSchema(this.getClass().getResource("giapi-apply-config.xsd"));
            um.setSchema(schema); //to enable validation
            records = (Records) um.unmarshal(new File(xmlFileName));
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error parsing xml file " + xmlFileName, ex);
            throw new IllegalArgumentException("Problem parsing XML", ex);
        }

        for (ApplyType apply : records.getApply()) {
            List<SequenceCommand> seqComs = new ArrayList<SequenceCommand>();
            List<ConfigRecordType> configs = new ArrayList<ConfigRecordType>();
            for (Object cad : apply.getSequenceCommandOrConfigRecord()) {
                try {
                    if (cad instanceof SequenceCommandType) {
                        seqComs.add(SequenceCommand.valueOf(((SequenceCommandType) cad).value()));
                    } else if (cad instanceof ConfigRecordType) {
                        configs.add((ConfigRecordType) cad);
                    }
                } catch (ClassCastException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                } catch (IllegalArgumentException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
            applys.add(new ApplyRecord(cas, cs, epicsTop, seqComs, configs, apply.getName(), apply.getTimeout()));
        }

    }

    public void start() {
        startApplys();
        try {
            reset = cas.createChannel(epicsTop.buildEpicsChannelName(resetRecordsName), Reset.NO_RESET);
            reset.registerListener(new ResetListener());
        } catch (CAException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }

    }

    public void stop() {
        stopApplys();
        cas.destroyChannel(reset);

    }

    private void startApplys() {
        for (ApplyRecord apply : applys) {
            apply.start();
        }
    }

    private void stopApplys() {
        for (ApplyRecord apply : applys) {
            apply.stop();
        }
    }

    /**
     * This listener will be called when a directive is written to the DIR field
     */
    private class ResetListener implements ChannelListener<Reset> {
        @Override
        public void valueChanged(String channelName, List<Reset> values) {
            LOG.warning("Received reset write: " + values.get(0));
            if (values.get(0).equals(Reset.RESET)) {
                stopApplys();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    LOG.log(Level.SEVERE, e.getMessage(), e);
                }
                startApplys();
            }
        }
    }
}
