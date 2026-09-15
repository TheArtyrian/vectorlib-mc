package net.vertisoft.vectorlib.agnostic.registrars;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class VectorMobAttributes
{
    private static final Map<Supplier<?>, AttributeSupplier> ATTR = new HashMap<>();

    public static void add(Supplier<?> entityType, AttributeSupplier attributes) { ATTR.put(entityType, attributes); }

    public static Map<Supplier<?>, AttributeSupplier> get() {
        return ATTR;
    }
}
