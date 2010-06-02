package edu.gemini.aspen.giapi.status;

/**
 * Interface for status items representing system's health
 */
public interface HealthStatusItem extends StatusItem<Health> {

    /**
     * Returns the current health information from this
     * status item. The health is the value contained in
     * this status item.
     * @return The health value of this status item 
     */
    Health getHealth();

}
