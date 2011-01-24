package edu.gemini.aspen.giapi.status.beans;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A Status Annotation. Used in Beans to mark properties that have to
 * be associated to a particular status item.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Status {

    String statusName();

    Class  mapper();
}
