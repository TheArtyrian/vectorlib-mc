package net.vertisoft.vectorlib.agnostic.cosmetic.capes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.FileUtil;
import net.minecraft.client.Minecraft;
import net.vertisoft.vectorlib.VectorLib;
import net.vertisoft.vectorlib.agnostic.VectorSystems;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * The client-side manager for capes. Communicates with the server and attempts to tell it what cape to display on this user.
 *
 * @author Yirmiri (original code from RunicLib), Artyrian
 */
public class VecClientCapeMan
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static boolean CAPE_WORTHY = true;
    private static boolean INITED = false;
    private static Path FILE;

    private static String CAPE_ID = "";

    public static void initialize()
    {
        if (INITED) throw new IllegalArgumentException("VecClientCapeMan was already initialized");
        INITED = true;

        UUID uid = Minecraft.getInstance().getUser().getProfileId();
        String uidStr = Minecraft.getInstance().getUser().getProfileId().toString();
        FILE = VectorLib.PLATFORM.getGameDirectory().resolve("vectorlib").resolve("capes").resolve(uidStr + ".veccape");

        VectorLib.LOGGER.info("Initializing VectorLib client cape manager...");

        String vecCape = VectorCapeMan.getFirstApplicableCape(uid);
        if (vecCape.isEmpty()) {
            CAPE_WORTHY = false;
            VectorLib.LOGGER.info("Active user isn't allowed to have a cape - ignoring cape manager");
            return;
        }

        boolean loaded = load();
        if (!loaded) {
            CAPE_ID = vecCape;
            save();
        }
    }

    /////////////////////////////////////////////////////////////

    public static String capeID() { return CAPE_ID; }
    public static boolean isCapeWorthy() { return CAPE_WORTHY; }

    public static void setCape(String capeID) {
        CAPE_ID = capeID;
        save();
    }

    /////////////////////////////////////////////////////////////

    public static boolean load() {
        try {
            if (FILE.getParent() != null) FileUtil.createDirectoriesSafe(FILE.getParent());
            Reader reader = Files.newBufferedReader(FILE, StandardCharsets.UTF_8);

            char[] xmi = new char[64];
            reader.read(xmi);
            String str = String.valueOf(xmi).trim();
            reader.close();

            if (str != null) CAPE_ID = str;
            else return false;
        }
        catch (IOException ignored) {
            return false;
        }

        return true;
    }

    public static void save() {
        Minecraft minecraft = Minecraft.getInstance();

        try {
            if (FILE.getParent() != null) FileUtil.createDirectoriesSafe(FILE.getParent());
            Writer writer = Files.newBufferedWriter(FILE, StandardCharsets.UTF_8);
            writer.write(CAPE_ID);
            writer.close();
        }
        catch (IOException ignored) {
        }
    }
}
