package com.github.westonpace.jayvee.workflow;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A marker used to indicate that a particular field or property is
 * an output Sink to a worker or system.  This field should be controlled and settable 
 * by the system and the system builder.
 * 
 * @see InputBuffer
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface OutputBuffer {
	
}
