package net.vertisoft.vectorlib.exclusive.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.vertisoft.vectorlib.VectorLib;
import net.vertisoft.vectorlib.agnostic.VectorSystems;
import net.vertisoft.vectorlib.exclusive.datagen.soundfile.VectorSoundsheetGen;
import org.jetbrains.annotations.Nullable;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public abstract class VectorLangGen extends FabricLanguageProvider
{
    protected final String langcode;
    private final String ID;
    private final CompletableFuture<HolderLookup.Provider> WRAPPER;
    Set<String> queuedKeys = new HashSet<>();

    protected VectorLangGen(String id, FabricDataOutput outgo, String langcode, CompletableFuture<HolderLookup.Provider> wrapper)
    {
        super(outgo, langcode, wrapper);
        this.ID = id;
        this.WRAPPER = wrapper;
        this.langcode = langcode;
    }

    /**
     * Adds a translation for a string.
     */
    public void addRaw(TranslationBuilder builder, String key, String translation)
    {
        if (!this.queuedKeys.contains(key))
        {
            builder.add(key, translation);
            this.queuedKeys.add(key);
        } else
        {
            VectorLib.LOGGER.warn("Attempted to add a translation for {}, but it was already queued prior", key);
        }
    }

    /**
     * Adds a translation for a block.
     */
    public void addBlock(TranslationBuilder builder, Block block, String translation)
    {
        this.addRaw(builder, block.getDescriptionId(), translation);
    }

    /**
     * Adds a translation for an item.
     */
    public void addItem(TranslationBuilder builder, Item item, String translation)
    {
        this.addRaw(builder, item.getDescriptionId(), translation);
    }

    /**
     * Adds a translation for an effect.
     */
    public void addEffect(TranslationBuilder builder, Holder<MobEffect> effect, String translation)
    {
        this.addRaw(builder, effect.value().getDescriptionId(), translation);
    }

    /**
     * Adds a translation for an entity.
     */
    public <T extends Entity> void addEntity(TranslationBuilder builder, EntityType<T> type, String translation)
    {
        this.addRaw(builder, type.getDescriptionId(), translation);
    }

    /**
     * Adds translations for an advancement.
     */
    public void addAdv(TranslationBuilder builder, String advClass, String id, String name, String desc)
    {
        this.addRaw(builder, "advancements." + advClass + "." + id + ".title", name);
        this.addRaw(builder, "advancements." + advClass + "." + id + ".description", desc);
    }

    /**
     * Adds translations for a painting.
     */
    public void addPainting(TranslationBuilder builder, String modId, String name, String title, String author)
    {
        this.addRaw(builder, "painting." + modId + "." + name + ".author", author);
        this.addRaw(builder, "painting." + modId + "." + name + ".title", title);
    }

    /**
     * Adds translations for a damage type.
     */
    public void addDmgType(TranslationBuilder build, String path, String deathMsg, String killMsg)
    {
        build.add("death.attack." + path, deathMsg);
        build.add("death.attack." + path + ".player", killMsg);
    }

    /**
     * Adds translations for a stat.
     */
    public void addStat(TranslationBuilder builder, String modId, String id, String desc)
    {
        this.addRaw(builder, "stat." + modId + "." + id, desc);
    }

    /** Takes a list of VectorDatagen.Captions and builds all of their translations. Uses the set langcode. */
    public void addSubtitles(TranslationBuilder builder, List<VectorDatagen.Caption> definitions)
    {
        this.addSubtitles(builder, this.langcode, definitions);
    }

    public void addSubtitles(TranslationBuilder builder, String locale, List<VectorDatagen.Caption> definitions)
    {
        for (VectorDatagen.Caption caption : definitions)
        {
            if (caption.text() != null)
            {
                Optional<String> stringLang = Optional.ofNullable(caption.text().get(locale));
                stringLang.ifPresentOrElse(
                        (capt) -> this.addRaw(builder, caption.id(), capt),
                        () -> VectorLib.LOGGER.error("No translation available for {}!", caption.id())
                );
            }
        }
    }

    private void addYapping(TranslationBuilder builder, String string, String desc)
    {
        this.addRaw(builder, "yapping_tooltips." + string + ".desc", desc);
    }

    private void addL4J(TranslationBuilder builder, String string, String desc)
    {
        this.addRaw(builder, string + ".tip", desc);
    }

    /**
     * Adds a block translation - with optional Yapping Tooltips + Legacy4J support.
     */
    public void addBlockWithDesc(TranslationBuilder builder, Block block, String name, @Nullable String legacy4j, @Nullable String yapping)
    {
        this.addBlockWithDesc(builder, block, name, legacy4j, yapping, null);

    }

    /**
     * Adds a block translation - with optional Yapping Tooltips, JEI and Legacy4J support.
     */
    public void addBlockWithDesc(TranslationBuilder builder, Block block, String name, @Nullable String legacy4j, @Nullable String yapping, @Nullable String jei)
    {
        this.addBlock(builder, block, name);

        if (legacy4j != null) this.addL4J(builder, block.getDescriptionId(), legacy4j);
        if (yapping != null) this.addYapping(builder, block.getDescriptionId(), yapping);
        if (jei != null) this.addJEIInfo(builder, block, jei);
    }

    /**
     * Adds additional item translations, such as names and disc descs.
     */
    public void addItemExtra(TranslationBuilder builder, Item item, String appendix, String desc)
    {
        this.addRaw(builder, item.getDescriptionId() + "." + appendix, desc);
    }

    /**
     * Adds an item translation - with optional Yapping Tooltips + Legacy4J support.
     */
    public void addItemWithDesc(TranslationBuilder builder, Item item, String name, @Nullable String legacy4j, @Nullable String yapping)
    {
        this.addItemWithDesc(builder, item, name, legacy4j, yapping, null);
    }

    /**
     * Adds an item translation - with optional Yapping Tooltips, JEI and Legacy4J support.
     */
    public void addItemWithDesc(TranslationBuilder builder, Item item, String name, @Nullable String legacy4j, @Nullable String yapping, @Nullable String jei)
    {
        this.addItem(builder, item, name);

        if (legacy4j != null) this.addL4J(builder, item.getDescriptionId(), legacy4j);
        if (yapping != null) this.addYapping(builder, item.getDescriptionId(), yapping);
        if (jei != null) this.addJEIInfo(builder, item, jei);
    }

    /**
     * A quick wrap-around method for items that don't have an immediate reference available.
     */
    public void addItemDescRaw(TranslationBuilder builder, String str, String name, @Nullable String legacy4j, @Nullable String yapping)
    {
        this.addRaw(builder, str, name);

        if (legacy4j != null) this.addL4J(builder, str, legacy4j);
        if (yapping != null) this.addYapping(builder, str, yapping);
    }

    /**
     * Registers container translations - can include a map.
     */
    public void addContainer(TranslationBuilder builder, String modId, String container, @Nullable String containerName, @Nullable Map<String, String> subMap)
    {
        if (containerName != null)
        {
            this.addRaw(builder, "container." + modId + "." + container, containerName);
        }

        if (subMap != null)
        {
            for (String key : subMap.keySet())
            {
                this.addRaw(builder, "container." + modId + "." + container + "." + key, subMap.get(key));
            }
        }
    }

    /** Registers Smithing Template data. */
    public void addTemplateUpgrade(TranslationBuilder builder, String modId, String upgrade, String upgradeName, String additions, String applies, String base, String ingredients)
    {
        this.addRaw(builder, "upgrade." + modId + "." + upgrade, upgradeName);

        this.addRaw(builder, "item." + modId + ".smithing_template." + upgrade + ".additions_slot_description", additions);
        this.addRaw(builder, "item." + modId + ".smithing_template." + upgrade + ".applies_to", applies);
        this.addRaw(builder, "item." + modId + ".smithing_template." + upgrade + ".base_slot_description", base);
        this.addRaw(builder, "item." + modId + ".smithing_template." + upgrade + ".ingredients", ingredients);
    }

    /** Registers all potion items for a prefix. */
    public void addPotionItems(TranslationBuilder builder, String modId, String effect, String potion, String splash, String linger, String arrow)
    {
        this.addRaw(builder, "item.minecraft.potion.effect." + modId + "." + effect, potion);
        this.addRaw(builder, "item.minecraft.splash_potion.effect." + modId + "." + effect, splash);
        this.addRaw(builder, "item.minecraft.lingering_potion.effect." + modId + "." + effect, linger);
        this.addRaw(builder, "item.minecraft.tipped_arrow.effect." + modId + "." + effect, arrow);
    }

    public void addJEIInfo(TranslationBuilder builder, Block block, String string)
    {
        this.addRaw(builder, VectorSystems.JEI_PREFIX + block.getDescriptionId(), string);
    }

    public void addJEIInfo(TranslationBuilder builder, Item item, String string)
    {
        this.addRaw(builder, VectorSystems.JEI_PREFIX + item.getDescriptionId(), string);
    }
}
