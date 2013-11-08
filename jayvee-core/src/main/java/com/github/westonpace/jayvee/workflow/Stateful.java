package com.github.westonpace.jayvee.workflow;

/**
 * Marks a worker as containing some kind of state (e.g. the opposite of stateless).
 * Ideally workers should be stateless as this allows us to parallelize them without
 * concern.  However, some workers (e.g. a tracker) must maintain state and those workers
 * should mark themselves Stateful with this annotation so that the system knows this
 * and will not parallelize them.
 */
public @interface Stateful {

}
