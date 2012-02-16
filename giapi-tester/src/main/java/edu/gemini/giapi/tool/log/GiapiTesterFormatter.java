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
        if (logRecord.getThrown() != null) {
            StringBuffer buf = new StringBuffer(logRecord.getMessage() + ": " + logRecord.getThrown().getMessage() + System.getProperty("line.separator"));
            for (StackTraceElement el : logRecord.getThrown().getStackTrace()) {
                buf.append("\t" + el + System.getProperty("line.separator"));
            }
            return buf.toString();
        } else {
            return logRecord.getMessage() + System.getProperty("line.separator");

        }
    }
}
