package edu.gemini.giapi.tool.log;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Custom logger so that messages sent to the local log are presented
 * as if written to the System.out
 */
public class GiapiTesterFormatter extends Formatter {
    @Override
    public String format(LogRecord logRecord) {
        return logRecord.getMessage() + System.getProperty("line.separator");
    }
}
