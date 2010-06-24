package edu.gemini.aspen.giapi.status.beans;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * A base bean for status items. It only adds the Property change support
 * so it really should be part of a more generic bean package. 
 */
public class BaseStatusBean {

    protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String prop, PropertyChangeListener listener)  {
        pcs.addPropertyChangeListener(prop, listener);
    }



}
