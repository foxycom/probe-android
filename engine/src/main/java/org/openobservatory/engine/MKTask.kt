package org.openobservatory.engine

import oonimkall.Task

/**
 * OONIMKTaskConfig is the interface that any settings for MK-like tasks
 * must implement. It allows the engine to discover the name of the task
 * that we want to run and to obtain its serialization.
 */
interface OONIMKTaskConfig {
    /**
     * serialization returns the JSON serialization of the task config, which
     * must be compatible with Measurement Kit v0.9.0 specification.
     */
    fun serialization(): String
}

/**
 * OONIMKTask is an MK-like task run by OONI Probe's engine. The expected usage
 * of this interface like in the following code snippet:
 *
 * <pre>
 * while (!task.isDone()) {
 *   processEvent(task.waitForNextEvent());
 * }
 * </pre>
 *
 * We do not necessarily run all tasks using the same backend. Some
 * backends support interrupting a task, others do not. Use the
 * canInterrupt() method to find out whether this is possible. If
 * a task can be interrupted, interrupt() will interrupt it. The task
 * will stop as soon as possible but not necessarily immediately.
 */
interface OONIMKTask {
    /** isDone tells you whether this task has completed.  */
    fun isDone(): Boolean

    /**
     * waitForNextEvent blocks until the next event is available
     * and return returns such event to the caller. The returned
     * event is a JSON serialized string that uses the data format
     * specified by Measurement Kit v0.9.0.
     */
    fun waitForNextEvent(): String

    /** canInterrupt returns true if this task can be interrupted.  */
    fun canInterrupt(): Boolean

    /**
     * interrupt will interrupt this task. If the backend does
     * not support interrupting a task, this method does noething.
     */
    fun interrupt()
}

internal class PEMKTask(private val task: Task) : OONIMKTask {
    override fun canInterrupt(): Boolean {
        return true
    }

    override fun interrupt() {
        task.interrupt()
    }

    override fun isDone(): Boolean {
        return task.isDone
    }

    override fun waitForNextEvent(): String {
        return task.waitForNextEvent()
    }
}
