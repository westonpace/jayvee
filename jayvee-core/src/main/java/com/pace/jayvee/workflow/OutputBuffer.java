package com.pace.jayvee.workflow;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This is a marker used to indicate that a particular field or property is
 * an output Sink to a worker or system and should be controlled and settable by the
 * system and the system builder.
 * 
 * @see InputBuffer
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface OutputBuffer {
	
}
