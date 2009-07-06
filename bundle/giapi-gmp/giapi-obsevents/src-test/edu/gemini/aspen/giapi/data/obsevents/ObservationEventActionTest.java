package edu.gemini.aspen.giapi.data.obsevents;

/**
 * Test code for the ObservationEventAction
 */
public class ObservationEventActionTest extends ObservationEventHandlerCompositeTestBase {

    ObservationEventHandlerComposite _compositeHandler = new ObservationEventAction();

    public ObservationEventHandlerComposite getHandlerComposite() {
        return _compositeHandler;
    }


}
