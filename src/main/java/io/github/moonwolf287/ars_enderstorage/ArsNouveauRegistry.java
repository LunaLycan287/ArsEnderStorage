package io.github.moonwolf287.ars_enderstorage;

import io.github.moonwolf287.ars_enderstorage.glyphs.ColorGlyph;
import io.github.moonwolf287.ars_enderstorage.glyphs.EnderStorageChest;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;

import java.util.ArrayList;
import java.util.List;

public class ArsNouveauRegistry {
    protected static List<AbstractSpellPart> registeredSpells = new ArrayList<>();

    private ArsNouveauRegistry() {}

    protected static void registerGlyphs() {
        register(EnderStorageChest.INSTANCE);

        // Color Glyphs for all MC colors
        register(ColorGlyph.WHITE);
        register(ColorGlyph.ORANGE);
        register(ColorGlyph.MAGENTA);
        register(ColorGlyph.LIGHT_BLUE);
        register(ColorGlyph.YELLOW);
        register(ColorGlyph.LIME);
        register(ColorGlyph.PINK);
        register(ColorGlyph.GRAY);
        register(ColorGlyph.LIGHT_GRAY);
        register(ColorGlyph.CYAN);
        register(ColorGlyph.PURPLE);
        register(ColorGlyph.BLUE);
        register(ColorGlyph.BROWN);
        register(ColorGlyph.GREEN);
        register(ColorGlyph.RED);
        register(ColorGlyph.BLACK);
    }

    private static void register(AbstractSpellPart spellPart) {
        ArsNouveauAPI.getInstance().registerSpell(spellPart.tag,spellPart);
        registeredSpells.add(spellPart);
    }
}
