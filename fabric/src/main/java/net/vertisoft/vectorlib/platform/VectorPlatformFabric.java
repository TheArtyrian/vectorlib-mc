package net.vertisoft.vectorlib.platform;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.vertisoft.vectorlib.VectorLib;

import java.nio.file.Path;
import java.util.Optional;

public class VectorPlatformFabric implements VectorPlatformIntf
{
    @Override
    public String getPlatformName() {
        return FABRIC;
    }

    @Override
    public boolean isModLoaded(String modId) { return FabricLoader.getInstance().isModLoaded(modId); }

    @Override
    public Path getConfigDirectory() { return FabricLoader.getInstance().getConfigDir(); }

    @Override
    public String getModVersion(String mod)
    {
        String ret = "";
        try
        {
            Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(mod);
            if (container.isEmpty()) throw new IllegalArgumentException("The provided mod ID doesn't have an existing ModContainer");
            ret = container.get().getMetadata().getVersion().getFriendlyString();
        }
        catch (IllegalArgumentException xyz)
        {
            VectorLib.LOGGER.error("getModVersion() failed, see below", xyz);
        }

        return ret;
    }

    @Override
    public boolean isDevelopmentEnvironment()
    {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public boolean isClient() { return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT; }

    @Override
    public boolean isDatagen()
    {
        try
        {
            Class.forName("net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint");
            return System.getProperty("fabric-api.datagen") != null;
        }
        catch (ClassNotFoundException unused)
        {
            return false;
        }
    }
}
