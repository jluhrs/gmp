package edu.gemini.aspen.gmp.commands.messaging;

import edu.gemini.aspen.gmp.commands.model.Action;
import edu.gemini.aspen.gmp.commands.model.ActionMessage;
import edu.gemini.aspen.gmp.commands.model.ActionMessageBuilder;
import edu.gemini.aspen.gmp.commands.api.Configuration;
import edu.gemini.aspen.gmp.commands.api.ConfigPath;
import edu.gemini.aspen.gmp.commands.api.SequenceCommand;
import edu.gemini.aspen.gmp.util.jms.GmpKeys;
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
                    GmpKeys.GMP_SEQUENCE_COMMAND_PREFIX + sc.getName());
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

            props.put(GmpKeys.GMP_ACTIVITY_PROP,
                    action.getActivity().getName());
            props.put(GmpKeys.GMP_ACTIONID_PROP, action.getId());

            data = new HashMap<String, Object>();

            //Store the configuration elements that
            //matches this config path.
            Configuration c = action.getConfiguration();
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
            StringBuilder sb = new StringBuilder(TOPIC_MAP.get(action.getSequenceCommand()));
            if (path != null) {
                sb.append(GmpKeys.GMP_SEPARATOR);
                sb.append(path.getName());
            }
            return sb.toString();
        }


        public DestinationData getDestinationData() {
            return dd;
        }

        public Map<String, Object> getProperties() {
            return props;
        }

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
    public ActionMessage buildActionMessage(Action action, ConfigPath path) {
        return new ActionMessageImpl(action, path);
    }
}
