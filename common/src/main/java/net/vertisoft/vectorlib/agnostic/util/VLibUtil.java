package net.vertisoft.vectorlib.agnostic.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.ItemLike;

import java.util.*;
import java.util.function.Supplier;

/** A small utility class for various things. */
public class VLibUtil
{
    /** Capitalizes the first letter of the string. */
    public static String capital(String string)
    {
        String pre = string.substring(0, 1);
        String suf = string.substring(1);
        pre = pre.toUpperCase();
        return pre + suf;
    }

    /** Organizes a list of ItemLikes in alphabetical order.*/
    public static List<Supplier<? extends ItemLike>> sortByNameOrder(List<Supplier<? extends ItemLike>> list)
    {
        List<Supplier<? extends ItemLike>> returnable = new ArrayList<>();
        List<String> names = new ArrayList<>();
        Map<String, Supplier<? extends ItemLike>> tabbers = new HashMap<>();

        for (Supplier<? extends ItemLike> sup : list)
        {
            String name = BuiltInRegistries.ITEM.getKey(sup.get().asItem()).getPath().toLowerCase();
            names.add(name);
            tabbers.put(name, sup);
        }

        Collections.sort(names);
        for (String key : names) if (tabbers.containsKey(key)) returnable.add(tabbers.get(key));

        if (returnable.isEmpty()) throw new IllegalArgumentException("The returnable list in sortByNameOrder is empty - THIS SHOULD NEVER HAPPEN!");
        else return returnable;
    }
}
