package edu.gemini.giapi.tool;

import edu.gemini.giapi.tool.fileevents.MonitorFileEventsOperation;
import edu.gemini.giapi.tool.parser.*;
import edu.gemini.giapi.tool.arguments.*;
import edu.gemini.giapi.tool.commands.CommandOperation;
import edu.gemini.giapi.tool.help.HelpOperation;
import edu.gemini.giapi.tool.status.GetStatusNamesOperation;
import edu.gemini.giapi.tool.status.MonitorStatusOperation;
import edu.gemini.giapi.tool.status.GetStatusOperation;
import edu.gemini.giapi.tool.obsevents.MonitorObsEventOperation;
import edu.gemini.giapi.tool.status.SetStatusOperation;


/**
 * The GIAPI Tester main class. 
 */
public class GiapiTester {

    public static void main(String[] args) throws Exception {

        //register the default message to show in case of problems
        Util.registerDefaultMessage("Let me help you. Try: java -jar giapi-tester.jar -?");

        //create the parser
        ArgumentParser parser = new ArgumentParser(args);

        //possible arguments
        parser.registerArgument(new SequenceCommandArgument());
        parser.registerArgument(new ActivityArgument());
        parser.registerArgument(new ConfigArgument());
        parser.registerArgument(new TimeoutArgument());
        parser.registerArgument(new HostArgument());
        parser.registerArgument(new HelpArgument());
        parser.registerArgument(new RepetitionArgument());
        parser.registerArgument(new MonitorStatusArgument());
        parser.registerArgument(new GetStatusArgument());
        parser.registerArgument(new SetStatusArgument());
        parser.registerArgument(new GetStatusNamesArgument());
        parser.registerArgument(new MonitorObsEventArgument());
        parser.registerArgument(new MonitorFileEventsArgument());
        parser.registerArgument(new ExpectedValueArgument());

        //possible operations
        parser.registerOperation(new HelpOperation());
        parser.registerOperation(new CommandOperation());
        parser.registerOperation(new MonitorStatusOperation());
        parser.registerOperation(new GetStatusOperation());
        parser.registerOperation(new SetStatusOperation());
        parser.registerOperation(new GetStatusNamesOperation());
        parser.registerOperation(new MonitorObsEventOperation());
        parser.registerOperation(new MonitorFileEventsOperation());

        //get the Operation the parser found
        Operation op = parser.parse();

        if (op != null) {
            op.execute();
        } else {
            Util.die("I'm sorry, what operation do you mean?");
        }
    }

}
