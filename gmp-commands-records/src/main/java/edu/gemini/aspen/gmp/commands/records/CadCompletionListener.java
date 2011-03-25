package edu.gemini.aspen.gmp.commands.records;

import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import gov.aps.jca.CAException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
* This Listener updates the CAR record when completion information for a command is received.
*
* @author Nicolas A. Barriga
*         Date: 3/24/11
*/
class CadCompletionListener implements CompletionListener {
    private static final Logger LOG = Logger.getLogger(CadCompletionListener.class.getName());

    final private Integer clientId;
    final private CarRecord car;

    CadCompletionListener(Integer clientId, CarRecord car) {
        this.clientId = clientId;
        this.car = car;
    }

    @Override
    public void onHandlerResponse(HandlerResponse response, Command command) {
        try {
            if (response.getResponse().equals(HandlerResponse.Response.COMPLETED)) {
                car.changeState(CarRecord.Val.IDLE, response.hasErrorMessage() ? "" : response.getMessage(), 0, clientId);
            } else {
                car.changeState(CarRecord.Val.ERR, response.hasErrorMessage() ? "" : response.getMessage(), -1, clientId);
            }
        } catch (CAException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
