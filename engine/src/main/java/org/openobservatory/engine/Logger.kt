package org.openobservatory.engine

import android.util.Log
import oonimkall.Logger
import java.util.ArrayList

/** OONILogger is the logger used by a OONISession.  */
interface OONILogger {
    /** debug emits a debug message  */
    fun debug(message: String)

    /** info emits an informational message  */
    fun info(message: String)

    /** warn emits a warning message  */
    fun warn(message: String)
}

/** LoggerArray is a logger that writes logs into an array.  */
class LoggerArray : OONILogger {
    var logs = ArrayList<String>()

    override fun debug(message: String) {
        logs.add(message)
    }

    override fun info(message: String) {
        logs.add(message)
    }

    override fun warn(message: String) {
        logs.add(message)
    }
}

internal class LoggerAndroid : OONILogger {
    private val loggerTag = "engine"

    override fun debug(message: String) {
        Log.d(loggerTag, message)
    }

    override fun info(message: String) {
        Log.i(loggerTag, message)
    }

    override fun warn(message: String) {
        Log.w(loggerTag, message)
    }
}

internal class LoggerComposed(private val left: OONILogger, private val right: OONILogger) : OONILogger {
    override fun debug(message: String) {
        left.debug(message)
        right.debug(message)
    }

    override fun info(message: String) {
        left.info(message)
        right.info(message)
    }

    override fun warn(message: String) {
        left.warn(message)
        right.warn(message)
    }
}

internal class LoggerNull : OONILogger {
    override fun debug(message: String) {}
    override fun info(message: String) {}
    override fun warn(message: String) {}
}

internal class PELoggerAdapter(private val logger: OONILogger) : Logger {
    override fun debug(message: String) {
        logger.debug(message)
    }

    override fun info(message: String) {
        logger.info(message)
    }

    override fun warn(message: String) {
        logger.warn(message)
    }
}
