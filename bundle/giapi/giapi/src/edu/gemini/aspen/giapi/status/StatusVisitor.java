package edu.gemini.aspen.giapi.status;

/**
 * Visitor interface for status items
 */
public interface StatusVisitor {

    /**
     * Operation over simple status items
     * @param status the status item where the operation will be applied.
     * @throws Exception if a problem occurs
     */
    void visitStatusItem(StatusItem status) throws Exception;

    /**
     * Operation over alarm status items
     * @param status the alarm status item where the operation will be applied.
     * @throws Exception if a problem occurs
     */
    void visitAlarmItem(AlarmStatusItem status) throws Exception;

    /**
     * Operation over health status items
     * @param status the health status item where the operation will be applied.
     * @throws Exception if a problem occurs
     */
    void visitHealthItem(HealthStatusItem status) throws Exception;

    
}
