package com.pace.jayvee.workflow;

/**
 * This annotation should be placed on workers that maintain some kind of internal state.
 * Ideally workers should be stateless as this allows us to parallelize them without
 * concern.  However, some workers (e.g. a tracker) must maintain state and those workers
 * should mark themselves Stateful with this annotation so that the system knows this
 * and will not parallelize them.
 */
public @interface Stateful {

}
