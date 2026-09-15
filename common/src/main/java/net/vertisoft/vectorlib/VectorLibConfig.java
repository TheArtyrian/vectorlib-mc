package net.vertisoft.vectorlib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;

public class VectorLibConfig
{
    private static final File CONFIG = new File(VectorLib.PLATFORM.getConfigDirectory().toFile(), "vectorlib.json");

    // All configs for the lib
    private boolean suppressExperimentalWarn = true;        // Suppresses the experimental warnings screen.
    private boolean specialCapeEnabled = true;              // Determines if users with special capes use it.
    private boolean vectorLibCommandsVisible = true;        // When false, will disable VectorLib commands like "vl_event".

    public static VectorLibConfig load_config()
    {
        VectorLibConfig config = new VectorLibConfig();

        // Do save in case it doesnt exist
        if (!CONFIG.exists()) save_config(config);

        Reader reader;
        try
        {
            reader = Files.newBufferedReader(CONFIG.toPath());
            Gson xnl = new GsonBuilder().setPrettyPrinting().create();
            config = xnl.fromJson(reader, VectorLibConfig.class);
            reader.close();

            VectorLib.LOGGER.info("VectorLib config file loaded.");
        }
        catch (IOException error)
        {
            VectorLib.LOGGER.error("Could not load/read VectorLib config file! Resorting to default configs - please check for config file's existence and try again.", error);
        }

        return config;
    }

    public static void save_config(VectorLibConfig file)
    {
        try
        {
            Writer writer = Files.newBufferedWriter(CONFIG.toPath());
            Gson xni = new GsonBuilder().setPrettyPrinting().create();
            xni.toJson(file, writer);
            writer.close();

            VectorLib.LOGGER.info("VectorLib config file saved.");
        }
        catch (IOException error)
        {
            VectorLib.LOGGER.error("A critical error occured while saving the VectorLib config file. Please make sure the file isn't damaged and try again.", error);
        }
    }

    public boolean doSuppressExperimentalWarn() { return suppressExperimentalWarn; }
    public boolean doSpecialCapeEnabled() { return specialCapeEnabled; }
    public boolean doVectorCommands() { return vectorLibCommandsVisible; }
}
