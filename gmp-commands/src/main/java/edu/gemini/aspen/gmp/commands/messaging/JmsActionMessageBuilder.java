package edu.gemini.aspen.gmp.commands.messaging;

import edu.gemini.aspen.giapi.commands.ConfigPath;
import edu.gemini.aspen.giapi.commands.Configuration;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.gmp.commands.model.Action;
import edu.gemini.aspen.gmp.commands.model.ActionMessage;
import edu.gemini.aspen.gmp.commands.model.ActionMessageBuilder;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;

import java.util.Map;
import java.util.HashMap;

/**
 * Helper class to construct {@link edu.gemini.aspen.gmp.commands.model.ActionMessage}
 * objects based on a given Action, using JMS objects
 */
public class JmsActionMessageBuilder implements ActionMessageBuilder {
    /**
     * Map to store topics associtated to each sequence command
     */
    private static final Map<SequenceCommand, String> TOPIC_MAP = new HashMap<SequenceCommand, String>();
    /**
     * Topic map static initialization
     */
    static {
        for (SequenceCommand sc : SequenceCommand.values()) {
            TOPIC_MAP.put(sc,
                    JmsKeys.GMP_SEQUENCE_COMMAND_PREFIX + sc.getName());
        }
    }

    /**
     * A simple implementation of the Action Message. Not to be exposed to
     * other classes.
     */
    private final class ActionMessageImpl implements ActionMessage {

        private DestinationData dd;
        private Map<String, Object> props;
        private Map<String, Object> data;

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

            dd = new DestinationData(getTopicName(action, path), DestinationType.TOPIC);

            props = new HashMap<String, Object>();

            props.put(JmsKeys.GMP_ACTIVITY_PROP,
                    action.getCommand().getActivity().getName());
            props.put(JmsKeys.GMP_ACTIONID_PROP, action.getId());

            data = new HashMap<String, Object>();

            //Store the configuration elements that
            //matches this config path.
            Configuration c = action.getCommand().getConfiguration();
            if (c != null) {

                if (path != null) {
                    c = c.getSubConfiguration(path);
                }

                for (ConfigPath cp : c.getKeys()) {
                    data.put(cp.getName(), c.getValue(cp));
                }
            }

        }

        /**
         * Constructor.
         *
         * @param action The action to be used to build this action message
         */
        public ActionMessageImpl(Action action) {
            this(action, null);
        }

        private String getTopicName(Action action, ConfigPath path) {
            //the destination changes if a config path is specified...
            StringBuilder sb = new StringBuilder(TOPIC_MAP.get(action.getCommand().getSequenceCommand()));
            if (path != null) {
                sb.append(JmsKeys.GMP_SEPARATOR);
                sb.append(path.getName());
            }
            return sb.toString();
        }

        @Override
        public DestinationData getDestinationData() {
            return dd;
        }

        @Override
        public Map<String, Object> getProperties() {
            return props;
        }

        @Override
        public Map<String, Object> getDataElements() {
            return data;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ActionMessageImpl that = (ActionMessageImpl) o;

            if (data != null ? !data.equals(that.data) : that.data != null)
                return false;
            if (dd != null ? !dd.equals(that.dd) : that.dd != null)
                return false;
            if (props != null ? !props.equals(that.props) : that.props != null)
                return false;
            //same thing
            return true;
        }

        @Override
        public int hashCode() {
            int result = dd != null ? dd.hashCode() : 0;
            result = 31 * result + (props != null ? props.hashCode() : 0);
            result = 31 * result + (data != null ? data.hashCode() : 0);
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
