package net.vertisoft.vectorlib.platform;

import java.nio.file.Path;

public interface VectorPlatformIntf
{
    static String NF = "NeoForge";
    static String FABRIC = "Fabric";

    /** Gets the platform name. */
    String getPlatformName();

    /** Checks if a provided mod is loaded in runtime. */
    boolean isModLoaded(String modId);

    /** Checks if currently in the dev environment. */
    boolean isDevelopmentEnvironment();

    /** Checks if currently running the data generator. */
    boolean isDatagen();

    /** Checks if currently running on the client. */
    boolean isClient();

    /** Gets the config directory for the platform. */
    Path getConfigDirectory();

    /** Gets the mod version for the provided mod - highly recommended that you ONLY use for your own. */
    String getModVersion(String mod);

    /** Gets string representative of current environment type. */
    default String getEnvironmentName()
    {
        return isDevelopmentEnvironment() ? "development" : "production";
    }
}