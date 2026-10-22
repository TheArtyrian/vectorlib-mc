package net.vertisoft.vectorlib.agnostic.cosmetic.capes;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.vertisoft.vectorlib.agnostic.VectorSystems;

import java.util.*;

/**
 * The dual-sided cape manager.
 *
 * @author Yirmiri (original code from RunicLib), Artyrian
 */
public class VectorCapeMan
{
    public static final Component OFF_CAPE = Component.translatable("cape.vectorlib.disabled");
    private static final List<VecCape> CAPES = new ArrayList<>();

    private static final Map<UUID, Integer> SELECTED = new HashMap<>();
    private static boolean INITED = false;

    public static void init()
    {
        if (INITED) throw new IllegalArgumentException("VectorCapeMan was already initialized");
        INITED = true;

        // Artyrian cape
        CAPES.add(new VecCape(
                "artyrian_cape",
                false,
                List.of(
                        VectorSystems.CONTRIBUTORS.get("Dev"),
                        VectorSystems.CONTRIBUTORS.get("Artyrian")
                )
        ));
        // Yur cape
        CAPES.add(new VecCape(
                "yurjezich_cape",
                false,
                List.of(
                        VectorSystems.CONTRIBUTORS.get("Dev"),
                        VectorSystems.CONTRIBUTORS.get("Yurjezich")
                )
        ));
        // LD Cape
        CAPES.add(new VecCape(
                "ld_cape",
                false,
                List.of(
                        VectorSystems.CONTRIBUTORS.get("Dev"),
                        VectorSystems.CONTRIBUTORS.get("LucarioDeath")
                )
        ));
        // Eiscue Cape
        CAPES.add(new VecCape(
                "eiscue_cape",
                false,
                List.of(
                        VectorSystems.CONTRIBUTORS.get("Dev"),
                        VectorSystems.CONTRIBUTORS.get("EmeraldEiscue")
                )
        ));
        // Courtjjester Cape
        CAPES.add(new VecCape(
                "courtjjester_cape",
                true,
                List.of(
                        VectorSystems.CONTRIBUTORS.get("Dev"),
                        VectorSystems.CONTRIBUTORS.get("Courtjjester")
                )
        ));
        // Rednalokin Cape
        CAPES.add(new VecCape(
                "rednalokin_cape",
                true,
                List.of(
                        VectorSystems.CONTRIBUTORS.get("Dev"),
                        VectorSystems.CONTRIBUTORS.get("Rednalokin")
                )
        ));
    }

    /////////////////////////////////////////////////////////////

    /**
     * Syncs the player's data into the UUID system.
     */
    public static void sync(UUID uuid, String string)
    {
        if (string.isEmpty()) {
            SELECTED.put(uuid, -1);
            return;
        }

        VecCape cape = getAssociatedCape(string);
        int setTo = (cape.userIsWorthy(uuid)) ? CAPES.indexOf(cape) : -1;
        directIDSet(uuid, setTo);
    }

    /**
     * Frees the UUID from the UUID system.
     */
    public static void free(UUID uuid) {
        SELECTED.remove(uuid);
    }

    public static void directIDSet(UUID uuid, int id) {
        SELECTED.put(uuid, id);
    }

    /////////////////////////////////////////////////////////////

    public static VecCape getAssociatedCape(int id)
    {
        if (id >= CAPES.size() || id < 0) return null;
        else return CAPES.get(id);
    }

    public static VecCape getAssociatedCape(String id)
    {
        int maxSize = CAPES.size();
        VecCape pointer;
        int setTo = -1;
        for (int i = 0; i < maxSize; i++) {
            pointer = getAssociatedCape(i);
            if (pointer.getCapeName().equals(id)) return pointer;
        }
        return null;
    }

    public static Pair<String, Component> getNextCapeNotInGame(String id, UUID uuid)
    {
        int maxSize = CAPES.size();

        String idname = "";
        Component cmpnt = OFF_CAPE;

        VecCape pointer;
        int pntrID = -1;
        for (int i = 0; i < maxSize; i++)
        {
            pointer = getAssociatedCape(i);

            if (pntrID > -1 || id.isEmpty()) {
                if (i > pntrID && pointer.userIsWorthy(uuid)) {
                    idname = pointer.getCapeName();
                    cmpnt = Component.translatable(pointer.getComponentID());
                    break;
                }
            }
            else {
                if (pointer.getCapeName().equals(id)) pntrID = i;
            }
        }

        return Pair.of(idname, cmpnt);
    }

    /////////////////////////////////////////////////////////////

    public static int getAssociatedOrdinal(UUID uuid)
    {
        if (!SELECTED.containsKey(uuid)) throw new NoSuchElementException("The player UUID is not in the list");
        return SELECTED.getOrDefault(uuid, -1);
    }

    public static boolean hasTransparentCapeOn(UUID uuid)
    {
        if (!SELECTED.containsKey(uuid)) return false;

        int id = getAssociatedOrdinal(uuid);
        if (id < 0 || id >= CAPES.size()) return false;

        VecCape cape = CAPES.get(id);
        return cape.isTransparent();
    }

    public static ResourceLocation fetchCape(UUID uuid)
    {
        if (!SELECTED.containsKey(uuid)) return null;

        int id = getAssociatedOrdinal(uuid);
        if (id < 0 || id >= CAPES.size()) return null;

        VecCape cape = CAPES.get(id);
        return cape.getTexture();
    }

    public static String next(UUID uuid)
    {
        int id = getAssociatedOrdinal(uuid);

        int maxSize = CAPES.size();
        VecCape pointer;
        int setTo = -1;
        String ret = "";
        for (int i = 0; i < maxSize; i++) {
            if (i <= id) continue;

            pointer = getAssociatedCape(i);
            if (pointer.userIsWorthy(uuid)) {
                setTo = i;
                ret = pointer.getCapeName();
                break;
            }
        }

        SELECTED.put(uuid, setTo);
        return ret;
    }

    public static Component getSelectedName(UUID uuid)
    {
        int id = getAssociatedOrdinal(uuid);
        if (id < 0 || id >= CAPES.size()) return OFF_CAPE;

        VecCape texture = getAssociatedCape(id);
        return Component.translatable(texture.getComponentID());
    }

    /////////////////////////////////////////////////////////////

    public static String getFirstApplicableCape(UUID uuid)
    {
        if (!VectorSystems.CONTRIBUTORS.containsValue(uuid)) return "";

        int maxSize = CAPES.size();
        VecCape pointer;
        String ret = "";
        for (int i = 0; i < maxSize; i++) {
            pointer = getAssociatedCape(i);
            if (pointer.userIsWorthy(uuid)) {
                ret = pointer.getCapeName();
                break;
            }
        }

        return ret;
    }

    public static String toClientID(UUID uuid)
    {
        int id = getAssociatedOrdinal(uuid);
        if (id < 0 || id >= CAPES.size()) return "";

        VecCape texture = getAssociatedCape(id);
        return texture.getCapeName();
    }
}
