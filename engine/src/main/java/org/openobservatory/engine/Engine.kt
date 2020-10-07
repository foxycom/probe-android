package org.openobservatory.engine

import android.content.Context
import oonimkall.Oonimkall
import java.io.File
import java.io.IOException

/** OONIException is the exception thrown by the this package.  */
class OONIException(message: String, exc: Throwable) : Exception(message, exc)

/**
 * Engine is a factory class for creating several kinds of tasks. We will use different
 * engines depending on the task that you wish to create.
 */
object OONIEngine {
    /** newUUID4 returns the a new UUID4 for this client  */
    @JvmStatic
    fun newUUID4(): String {
        return Oonimkall.newUUID4()
    }

    /** startExperimentTask starts the experiment described by the provided settings.  */
    @JvmStatic
    @Throws(OONIException::class)
    fun startExperimentTask(settings: OONIMKTaskConfig): OONIMKTask {
        return maybeRewriteException("Oonimkall.startTask failed") {
            PEMKTask(Oonimkall.startTask(settings.serialization()))
        }
    }

    /** resolveProbeCC returns the probeCC.  */
    @JvmStatic
    @Throws(OONIException::class)
    fun resolveProbeCC(ctx: Context, softwareName: String,
                       softwareVersion: String, timeout: Long): String {
        val session = newSession(getDefaultSessionConfig(
                ctx, softwareName, softwareVersion, LoggerNull()
        ))
        // Updating resources with no timeout because we don't know for sure how much
        // it will take to download them and choosing a timeout may prevent the operation
        // to ever complete. (Ideally the user should be able to interrupt the process
        // and there should be no timeout here.)
        session.maybeUpdateResources(session.newContext())
        return session.geolocate(session.newContextWithTimeout(timeout)).country
    }

    /** newSession returns a new OONISession instance.  */
    @JvmStatic
    @Throws(OONIException::class)
    fun newSession(config: OONISessionConfig): OONISession {
        return PESession(config)
    }

    /** getDefaultSessionConfig returns a new SessionConfig with default parameters.  */
    @JvmStatic
    @Throws(OONIException::class)
    fun getDefaultSessionConfig(ctx: Context, softwareName: String,
                                softwareVersion: String, logger: OONILogger): OONISessionConfig {
        return maybeRewriteException("getDefaultSessionConfig failed") {
            val config = OONISessionConfig()
            config.logger = LoggerComposed(logger, LoggerAndroid())
            config.softwareName = softwareName
            config.softwareVersion = softwareVersion
            config.verbose = false
            config.assetsDir = getAssetsDir(ctx)
            config.stateDir = getStateDir(ctx)
            config.tempDir = getTempDir(ctx)
            config
        }
    }

    /**
     * getAssetsDir returns the assets directory for the current context. The
     * assets directory is the directory where the OONI Probe Engine should store
     * the assets it requires, e.g., the MaxMind DB files.
     */
    @JvmStatic
    @Throws(IOException::class)
    fun getAssetsDir(ctx: Context): String {
        return File(ctx.filesDir, "assets").canonicalPath
    }

    /**
     * getStateDir returns the state directory for the current context. The
     * state directory is the directory where the OONI Probe Engine should store
     * internal state variables (e.g. the orchestra credentials).
     */
    @JvmStatic
    @Throws(IOException::class)
    fun getStateDir(ctx: Context): String {
        return File(ctx.filesDir, "state").canonicalPath
    }

    /**
     * getTempDir returns the temporary directory for the current context. The
     * temporary directory is the directory where the OONI Probe Engine should store
     * the temporary files that are managed by a Session.
     */
    @JvmStatic
    @Throws(IOException::class)
    fun getTempDir(ctx: Context): String {
        return File(ctx.cacheDir, "").canonicalPath
    }
}

@Throws(OONIException::class)
internal fun <T: Any> maybeRewriteException(msg: String, f: ()->T): T {
    return try {
        f()
    } catch (exc: Exception) {
        throw OONIException(msg, exc)
    }
}
