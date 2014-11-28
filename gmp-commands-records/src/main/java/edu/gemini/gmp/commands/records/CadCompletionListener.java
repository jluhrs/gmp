package edu.gemini.gmp.commands.records;

import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.giapi.commands.HandlerResponse;

import java.util.logging.Logger;

/**
 * This Listener updates the CAR record when completion information for a command is received.
 *
 * @author Nicolas A. Barriga
 *         Date: 3/24/11
 */
class CadCompletionListener implements CompletionListener {
    private static final Logger LOG = Logger.getLogger(CadCompletionListener.class.getName());

    private final Integer clientId;
    private final CarRecord car;

    CadCompletionListener(Integer clientId, CarRecord car) {
        this.clientId = clientId;
        this.car = car;
    }

    @Override
    public void onHandlerResponse(HandlerResponse response, Command command) {
        if (response.getResponse().equals(HandlerResponse.Response.COMPLETED)) {
            car.setIdle(clientId);
        } else {
            car.setError(clientId, response.hasErrorMessage() ? response.getMessage() : "", -1);
        }

    }

    @Override
    public String toString() {
        return "CadCompletionListener{" +
                "clientId=" + clientId +
                ", car=" + car +
                '}';
    }
}
