package edu.gemini.aspen.giapi.commands;

import java.util.EnumSet;

/**
 *  Enumerated type defining the Gemini Sequence Commands
 */
public enum SequenceCommand {
    TEST("TEST"),
    REBOOT("REBOOT"),
    INIT("INIT"),
    DATUM("DATUM"),
    PARK("PARK"),
    VERIFY("VERIFY"),
    END_VERIFY("END_VERIFY"),
    GUIDE("GUIDE"),
    END_GUIDE("END_GUIDE"),
    APPLY("APPLY"),
    OBSERVE("OBSERVE"),
    END_OBSERVE("END_OBSERVE"),
    PAUSE("PAUSE"),
    CONTINUE("CONTINUE"),
    STOP("STOP"),
    ABORT("ABORT"),
    ENGINEERING("ENGINEERING");

    private final String _name;

    private SequenceCommand(String name) {
        _name = name;
    }

    public String getName() {
        return _name;
    }

    /**
     * Returns a set of SequenceCommands that don't require configuration
     */
    public static EnumSet<SequenceCommand> commandWithNoConfig() {
        return EnumSet.complementOf(EnumSet.of(APPLY, OBSERVE, REBOOT, ENGINEERING));
    }

}