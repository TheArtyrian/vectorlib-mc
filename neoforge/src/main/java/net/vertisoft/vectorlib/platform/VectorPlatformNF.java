package net.vertisoft.vectorlib.platform;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import net.vertisoft.vectorlib.VectorLib;

import java.nio.file.Path;
import java.util.Optional;

public class VectorPlatformNF implements VectorPlatformIntf
{
    @Override
    public String getPlatformName() { return NF;}

    @Override
    public boolean isModLoaded(String modId)
    {
        return (ModList.get() != null) ? ModList.get().isLoaded(modId) : false;
    }

    @Override
    public boolean isDevelopmentEnvironment()
    {
        return !FMLLoader.isProduction();
    }

    @Override
    public boolean isClient() { return FMLLoader.getDist().isClient(); }

    @Override
    public boolean isDatagen() { return DatagenModLoader.isRunningDataGen(); }

    @Override
    public Path getConfigDirectory() { return FMLPaths.CONFIGDIR.get(); }

    @Override
    public String getModVersion(String mod)
    {
        String ret = "";

        try
        {
            if (ModList.get() == null) throw new ExceptionInInitializerError("FML's ModList is not yet initialized!");

            Optional<? extends ModContainer> opt = ModList.get().getModContainerById(mod);
            if (opt.isEmpty()) throw new ExceptionInInitializerError("Mod ID doesn't have a container");

            ret = opt.get().getModInfo().getVersion().toString();
        }
        catch (ExceptionInInitializerError | IllegalArgumentException e)
        {
            VectorLib.LOGGER.error("Couldn't get mod version, see below", e);
        }

        return ret;
    }
}