package edu.gemini.cas.epics;

import com.cosylab.epics.caj.cas.util.MemoryProcessVariable;
import gov.aps.jca.cas.ProcessVariableEventCallback;
import gov.aps.jca.dbr.*;

/**
 * Class AlarmMemoryProcessVariable
 *
 * @author Nicolas A. Barriga
 *         Date: 12/28/10
 */
public class AlarmMemoryProcessVariable extends MemoryProcessVariable{

    protected Status _status;

    protected Severity _severity;

    /**
	 * Constructor of memory process variable.
	 * @param name	name of the PV.
	 * @param eventCallback	event callback, where to report value changes if <code>interest</code> is <code>true</code>.
	 * @param type	type of the PV (of initial value).
	 * @param initialValue	initial value, array is expected.
	 */
	public AlarmMemoryProcessVariable(String name, ProcessVariableEventCallback eventCallback,
								 DBRType type, Object initialValue)
	{
        super(name,eventCallback,type,initialValue);
        setStatus(0);
        setSeverity(0);
    }

    /**
	 * Constructor of memory process variable.
	 * @param name	name of the PV.
	 * @param eventCallback	event callback, where to report value changes if <code>interest</code> is <code>true</code>.
	 * @param type	type of the PV (of initial value).
	 * @param initialValue	initial value, array is expected.
     * @param status alarm initial status(cause)
     * @param severity alarm initial severity
	 */
	public AlarmMemoryProcessVariable(String name, ProcessVariableEventCallback eventCallback,
								 DBRType type, Object initialValue, Status status, Severity severity)
	{
        super(name,eventCallback,type,initialValue);
        setStatus(status);
        setSeverity(severity);
    }

    /**
	 * Constructor of memory process variable.
	 * @param name	name of the PV.
	 * @param eventCallback	event callback, where to report value changes if <code>interest</code> is <code>true</code>.
	 * @param type	type of the PV (of initial value).
	 * @param initialValue	initial value, array is expected.
     * @param status alarm initial status(cause)
     * @param severity alarm initial severity
	 */
	public AlarmMemoryProcessVariable(String name, ProcessVariableEventCallback eventCallback,
								 DBRType type, Object initialValue, int status, int severity)
	{
        super(name,eventCallback,type,initialValue);
        setStatus(status);
        setSeverity(severity);
    }

    public Status getStatus() {
        return _status;
    }

    public void setStatus(int status) {
        setStatus(Status.forValue(status));
    }

    public void setStatus(Status status) {
        if (status == null) throw new IllegalArgumentException("Illegal status: null");
        _status = status;
    }


    public Severity getSeverity() {
        return _severity;
    }

    public void setSeverity(int severity) {
        setSeverity(Severity.forValue(severity));
    }

    public void setSeverity(Severity severity) {
        if (severity == null) throw new IllegalArgumentException("Illegal severity: null");
        _severity = severity;
    }

    @Override
    public void fillInDBR(DBR value)
	{
        super.fillInDBR(value);

        if(value.isSTS()){
            //System.out.println(name+": fillInDBR: "+getStatus()+" "+getSeverity());
            STS sts=(STS)value;
            sts.setStatus(getStatus());
            sts.setSeverity(getSeverity());
        }
    }
}
