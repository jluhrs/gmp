package edu.gemini.aspen.gmp.commands.model.executors;

import edu.gemini.aspen.gmp.commands.model.*;
import edu.gemini.aspen.gmp.commands.api.*;
import edu.gemini.aspen.gmp.commands.messaging.ActionMessageBuilder;
import edu.gemini.aspen.gmp.util.commands.HandlerResponseImpl;

import java.util.logging.Logger;

/**
 * An executor for the REBOOT sequence command.
 */
public class RebootSenderExecutor implements SequenceCommandExecutor {

    private static final Logger LOG = Logger.getLogger(RebootSenderExecutor.class.getName());

    private RebootManager _rebootManager;
    private DefaultSenderExecutor _defaultExecutor;

    public RebootSenderExecutor(ActionMessageBuilder builder, RebootManager rebootManager) {
        _defaultExecutor = new DefaultSenderExecutor(builder);
        _rebootManager = rebootManager;
    }



    public HandlerResponse execute(Action action, ActionSender sender) {

        final RebootArgument arg = RebootArgument.parse(action.getConfiguration());
        if (arg == null) {
            return HandlerResponseImpl.createError("Invalid argument for the REBOOT sequence command: " + action.getConfiguration());
        }

        if (action.getActivity() == Activity.PRESET)
            return HandlerResponseImpl.create(HandlerResponse.Response.ACCEPTED);
        if (action.getActivity() == Activity.CANCEL) {
            return HandlerResponseImpl.createError("Can't cancel a REBOOT sequence command");
        }

        //So we have an start. Let's perform a PARK on the instrument, catching
        //the return response. The actual reboot will be executed once we
        //get the completion information.
        Action parkAction = action.mutate(SequenceCommand.PARK,
                Activity.START,
                null,
                new RebootCompletionListener(action.getCompletionListener(), arg));


        //use the default executor to send the command.
        HandlerResponse response = _defaultExecutor.execute(parkAction, sender);

        if (response != null && response.getResponse() == HandlerResponse.Response.COMPLETED) {
            //a fast PARK. The instrument was parked already, for instance.
            //let's initiate the reboot in a different thread
            new Thread() {
                @Override
                public void run() {
                    _rebootManager.reboot(arg);
                }
            }.start();
        }

        return response;
    }

    /**
     * A decorator listener that will receive the completion information
     * associated to the park sequence command and then it will initiate
     * the corresponding reboot of the system.
     */
    class RebootCompletionListener implements CompletionListener {


        private CompletionListener listener;
        private RebootArgument rebootArg;

        public RebootCompletionListener(CompletionListener l, RebootArgument arg) {
            listener = l;
            rebootArg = arg;
        }


        public void onHandlerResponse(HandlerResponse response,
                                      SequenceCommand command,
                                      Activity activity,
                                      Configuration config) {
            if (listener != null) {
                listener.onHandlerResponse(response, command, activity, config);
            }
            //now, let's initiate the reboot...
            if (response.getResponse() == HandlerResponse.Response.COMPLETED) {
                _rebootManager.reboot(rebootArg);
            } else {
                LOG.warning("Can't reboot, the PARK command terminated with " + response);
            }
        }
    }
}
