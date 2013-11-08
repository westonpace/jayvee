package com.github.westonpace.jayvee.workflow;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A marker used to indicate that a particular field or property is
 * an input Source to a worker or system.  This field should be controlled by the
 * system and the system builder.
 * 
 * This annotation should be placed on a public or private field for field based access
 * or a getter method for getter/setter access.  Which access method you prefer is 
 * completely up to you.  The underlying variable must be a Source.
 * 
 * In order to use a worker in a Builder and to call connect() on a source that source
 * must be marked with this annotation.  Otherwise, the builder will completely ignore
 * it.  This allows you to use sources which are not controlled by the system.
 * 
 * @see OutputBuffer
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface InputBuffer {
	
}
