package edu.gemini.aspen.gmp.commands.records;

import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import gov.aps.jca.CAException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
* Class CADCompletionListener
*
* @author Nicolas A. Barriga
*         Date: 3/24/11
*/
class CADCompletionListener implements CompletionListener {
    private static final Logger LOG = Logger.getLogger(CADCompletionListener.class.getName());

    final private Integer clientId;
    final private CARRecord car;

    CADCompletionListener(Integer clientId, CARRecord car) {
        this.clientId = clientId;
        this.car = car;
    }

    @Override
    public void onHandlerResponse(HandlerResponse response, Command command) {
        try {
            if (response.getResponse().equals(HandlerResponse.Response.COMPLETED)) {
                car.changeState(CARRecord.Val.IDLE, response.hasErrorMessage() ? "" : response.getMessage(), 0, clientId);
            } else {
                car.changeState(CARRecord.Val.ERR, response.hasErrorMessage() ? "" : response.getMessage(), -1, clientId);
            }
        } catch (CAException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
