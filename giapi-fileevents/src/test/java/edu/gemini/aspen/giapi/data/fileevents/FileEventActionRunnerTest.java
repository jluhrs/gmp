package edu.gemini.aspen.giapi.data.fileevents;

import edu.gemini.aspen.giapi.data.AncillaryFileEventHandler;
import edu.gemini.aspen.giapi.data.DataLabel;
import edu.gemini.aspen.giapi.data.IntermediateFileEventHandler;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.*;

/**
 * Unit Test code for the FileEventActionRunner class
 */
public class FileEventActionRunnerTest {


    private Integer _intermediateCount = 0;
    private Integer _ancillaryCount = 0;
    private final Object lockIntermediate=new Object();
    private final Object lockAncillary=new Object();


    private FileEventActionRunner _action;

    /**
     * A test Ancillary File Handler
     */
    private class AncillaryAction implements AncillaryFileEventHandler {
                          public boolean done=false;
        public void onAncillaryFileEvent(String filename, DataLabel dataLabel) {
            synchronized (this) {
                done=true;
                synchronized(lockAncillary){
                    _ancillaryCount++;
                }
                this.notifyAll();
            }
        }
    }

    /**
     * A test Intermediate file handler
     */
    private class IntermediateAction implements IntermediateFileEventHandler {
        public boolean done=false;
        public void onIntermediateFileEvent(String filename, DataLabel dataLabel, String hint) {
            synchronized (this) {
                synchronized(lockIntermediate){
                    _intermediateCount++;
                }
                done=true;
                this.notifyAll();

            }
        }
    }

    @Before
    public void setUp() {
        _action = new FileEventActionRunner();
        _intermediateCount = 0;
        _ancillaryCount = 0;
    }

    @After
    public void tearDown(){
        _action.shutdown();
    }

    @Test
    public void testConcurrentFileEventsProcessing() {

        final AncillaryAction[] _ancillaryActions = new AncillaryAction[5];
        final IntermediateAction[] _intermediateActions = new IntermediateAction[5];

        for (int i = 0; i < _ancillaryActions.length; i++) {
            _ancillaryActions[i] = new AncillaryAction();
            _action.addAncillaryFileEventHandler(_ancillaryActions[i]);
        }

        for (int i = 0; i < _intermediateActions.length; i++) {
            _intermediateActions[i] = new IntermediateAction();
            _action.addIntermediateFileEventHandler(_intermediateActions[i]);
        }

        ExecutorService e = Executors.newCachedThreadPool();

        //Submit a thread to wait for the notifications of ancillary file events
        Future<Integer> f1 = e.submit(new Callable<Integer>() {
            public Integer call() {
                //synchronize all the handlers , so they have time to finish
                int i = 0;
                while (i < _ancillaryActions.length) {
                    synchronized (_ancillaryActions[i]) {
                        try {
                            while(!_ancillaryActions[i].done){
                                _ancillaryActions[i].wait(1000);
                            }
                        } catch (InterruptedException e) {
                            fail("Interrupted while waiting for thread to be called");
                        }
                    }
                    i++;
                }
                return 1;

            }
        });

        //Submit a thread to wait for the notifications of intermediate file events
        Future<Integer> f2 = e.submit(new Callable<Integer>() {
            public Integer call() {
                int i = 0;
                while (i < _intermediateActions.length) {
                    synchronized (_intermediateActions[i]) {
                        try {
                            while(!_intermediateActions[i].done){
                                _intermediateActions[i].wait(1000);
                            }
                        } catch (InterruptedException e) {
                            fail("Interrupted while waiting for thread to be called");
                        }
                    }
                    i++;
                }
                return 2;
            }
        });
        //Submit the file events.
        _action.onAncillaryFileEvent("filename1", new DataLabel("dataset"));
        _action.onIntermediateFileEvent("filename1", new DataLabel("dataset"), "hint");

        //wait for the threads waiting for events to finish.
        try {
            int v = f1.get(10, TimeUnit.SECONDS);
            //confirm the thread finished with the expected value
            assertEquals(1, v);
            //confirm the thread finished with the expected value
            v = f2.get(10, TimeUnit.SECONDS);
            assertEquals(2, v);
        } catch (InterruptedException e1) {
            fail("Unexpected InterruptedException exception");
        } catch (ExecutionException e1) {
            fail("Unexpected Execution exception");
        } catch (TimeoutException e1) {
            fail("Unexpected Timeout exception");
        }
        //check all the handlers got called
        assertEquals(_intermediateActions.length, _intermediateCount.longValue());
        assertEquals(_ancillaryActions.length, _ancillaryCount.longValue());


        for (AncillaryAction _ancillaryAction : _ancillaryActions) {
            _action.removeAncillaryFileEventHandler(_ancillaryAction);
        }

        for (IntermediateAction _intermediateAction : _intermediateActions) {
            _action.removeIntermediateFileEventHandler(_intermediateAction);
        }


    }
}
