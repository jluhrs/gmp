package edu.gemini.aspen.gmp.commands.jms.instrumentbridge;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import edu.gemini.aspen.giapi.commands.ConfigPath;
import edu.gemini.aspen.giapi.commands.Configuration;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.gmp.commands.model.Action;
import edu.gemini.aspen.gmp.commands.model.ActionMessage;
import edu.gemini.aspen.gmp.commands.model.ActionMessageBuilder;

import java.util.EnumMap;
import java.util.Map;

/**
 * Helper class to construct {@link edu.gemini.aspen.gmp.commands.model.ActionMessage}
 * objects based on a given Action.
 * This service is required by SequenceCommandExecutors
 */
public class JmsActionMessageBuilder implements ActionMessageBuilder {
    /**
     * Map to store topics associated to each sequence command
     */
    private static final EnumMap<SequenceCommand, String> TOPIC_MAP = Maps.newEnumMap(SequenceCommand.class);

    /**
     * Topic map static initialization
     */
    static {
        for (SequenceCommand sc : SequenceCommand.values()) {
            TOPIC_MAP.put(sc,
                    JmsKeys.GMP_SEQUENCE_COMMAND_PREFIX + sc.toString());
        }
    }

    /**
     * A simple implementation of the Action Message. Not to be exposed to
     * other classes.
     */
    private static final class ActionMessageImpl implements ActionMessage {

        private final String dd;
        private final ImmutableMap<String, Object> props;
        private final Map<String, Object> configurationElements;

        /**
         * Constructs an Action Message that contains only the
         * sub set configuration that matches the given
         * ConfigPath
         *
         * @param action the action to be converted in a message
         * @param path   used as a filter, only those configurations
         *               that match the given path will be considered.
         */
        public ActionMessageImpl(Action action, ConfigPath path) {
            Preconditions.checkArgument(action != null, "Cannot create a message for a null action");
            Preconditions.checkArgument(path != null, "Cannot create a message for a null path");

            dd = getTopicName(action.getCommand().getSequenceCommand(), path);

            props = ImmutableMap.<String, Object>of(
                    JmsKeys.GMP_ACTIVITY_PROP, action.getCommand().getActivity().getName(),
                    JmsKeys.GMP_ACTIONID_PROP, action.getId());

            configurationElements = Maps.newHashMap();

            //Store the configuration elements that
            //matches this config path.
            Configuration c = action.getCommand().getConfiguration();
            c = c.getSubConfiguration(path);

            for (ConfigPath cp : c.getKeys()) {
                configurationElements.put(cp.getName(), c.getValue(cp));
            }
        }

        /**
         * Constructor.
         *
         * @param action The action to be used to build this action message
         */
        public ActionMessageImpl(Action action) {
            this(action, ConfigPath.EMPTY_PATH);
        }

        private String getTopicName(SequenceCommand sequenceCommand, ConfigPath path) {
            //the destination changes if a config path is specified...
            StringBuilder sb = new StringBuilder(TOPIC_MAP.get(sequenceCommand));
            if (!path.equals(ConfigPath.EMPTY_PATH)) {
                sb.append(JmsKeys.GMP_SEPARATOR);
                sb.append(path.getName());
            }
            return sb.toString();
        }

        @Override
        public String getDestinationName() {
            return dd;
        }

        @Override
        public Map<String, Object> getProperties() {
            return props;
        }

        @Override
        public Map<String, Object> getDataElements() {
            return configurationElements;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            ActionMessageImpl that = (ActionMessageImpl) o;

            if (!configurationElements.equals(that.configurationElements)) {
                return false;
            }
            if (!dd.equals(that.dd)) {
                return false;
            }
            if (!props.equals(that.props)) {
                return false;
            }
            //same thing
            return true;
        }

        @Override
        public int hashCode() {
            int result = dd.hashCode();
            result = 31 * result + props.hashCode();
            result = 31 * result + configurationElements.hashCode();
            return result;
        }

    }

    /**
     * Builds an ActionMessage for the specified action
     *
     * @param action the action to be used to construct the message
     * @return a new ActionMessage
     */
    @Override
    public ActionMessage buildActionMessage(Action action) {
        return new ActionMessageImpl(action);
    }


    /**
     * Builds an action message for the specified action, but
     * only containing the configurarion information that
     * matches the provided ConfigPath
     *
     * @param action the action to be used to construct the message
     * @param path   used to filter the configuration. Only the configuration
     *               that matches the given path will be encoded in the message
     * @return a new ActionMessage containing the sub-configuration defined
     *         by the ConfigPath
     */
    @Override
    public ActionMessage buildActionMessage(Action action, ConfigPath path) {
        return new ActionMessageImpl(action, path);
    }
}
