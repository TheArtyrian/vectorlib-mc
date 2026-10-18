package net.vertisoft.vectorlib.exclusive.datagen;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VectorDatagen
{
    public static final String EN_US = "en_us";

    public static final List<Caption> CAPTIONS = new ArrayList<>();

    public static class Caption
    {
        private final String id;
        @Nullable private final Map<String, String> text;

        public static Caption of(String id, Map<String, String> text) { return new Caption(id, text); }
        /** This is best used for subtitles that already exist, i.e Vanilla ones or ones shared with another event in your registry.
         *  Just adds the subtitle data without setting it up for lang generation.
         */
        public static Caption ofExisting(String id) { return new Caption(id, null); }
        public static Map.Entry<String, String> englishUS(String text) {return Map.entry(EN_US, text); }

        private Caption(String id, Map<String, String> text)
        {
            this.id = id;
            this.text = text;
        }

        public String id() { return id; }
        public Map<String, String> text() { return text; }
    }

    public static class CommonSFX
    {
        public static final VectorDatagen.Caption BLOCK_BREAK = VectorDatagen.Caption.ofExisting("subtitles.block.generic.break");
        public static final VectorDatagen.Caption BLOCK_HIT = VectorDatagen.Caption.ofExisting("subtitles.block.generic.hit");
        public static final VectorDatagen.Caption BLOCK_PLACE = VectorDatagen.Caption.ofExisting("subtitles.block.generic.place");
        public static final VectorDatagen.Caption BLOCK_STEP = VectorDatagen.Caption.ofExisting("subtitles.block.generic.footsteps");

        public static final VectorDatagen.Caption ARROW_FIRED = VectorDatagen.Caption.ofExisting("subtitles.entity.arrow.shoot");

        public static final VectorDatagen.Caption CHEST_OPEN = VectorDatagen.Caption.ofExisting("subtitles.block.chest.open");
        public static final VectorDatagen.Caption CHEST_CLOSE = VectorDatagen.Caption.ofExisting("subtitles.block.chest.close");
        public static final VectorDatagen.Caption FENCE_GATE_OPEN = VectorDatagen.Caption.ofExisting("subtitles.block.fence_gate.toggle");

        public static final VectorDatagen.Caption NOTE_BLOCK = VectorDatagen.Caption.ofExisting("subtitles.block.note_block.note");
    }
}
