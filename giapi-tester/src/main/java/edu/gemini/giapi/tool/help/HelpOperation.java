package edu.gemini.giapi.tool.help;

import edu.gemini.giapi.tool.arguments.HelpArgument;
import edu.gemini.giapi.tool.parser.Operation;
import edu.gemini.giapi.tool.parser.Argument;

import java.io.InputStreamReader;
import java.io.BufferedReader;

/**
 * Present help information about the tester application to the user
 */
public class HelpOperation implements Operation {

    private boolean showHelp = false;

    @Override
    public void setArgument(Argument arg) {
        if (arg instanceof HelpArgument) {
            showHelp = true;
        }
    }

    @Override
    public boolean isReady() {
        return showHelp;
    }

    @Override
    public int execute() throws Exception {
        InputStreamReader isr = new InputStreamReader(
                HelpOperation.class.getResourceAsStream("usage.txt"));
        BufferedReader br = new BufferedReader(isr);
        for (String line = br.readLine(); line != null; line = br.readLine())
            System.out.println(line);
        return 0;
    }
}
