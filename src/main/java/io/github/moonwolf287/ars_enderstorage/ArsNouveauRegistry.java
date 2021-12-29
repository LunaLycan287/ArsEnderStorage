package io.github.moonwolf287.ars_enderstorage;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import io.github.moonwolf287.ars_enderstorage.glyphs.*;

import java.util.ArrayList;
import java.util.List;

public class ArsNouveauRegistry {
    protected static List<AbstractSpellPart> registeredSpells = new ArrayList<>();

    private ArsNouveauRegistry() {}

    protected static void registerGlyphs() {
        register(EnderStorageChest.INSTANCE);
        register(EnderStorageBreak.INSTANCE);
        register(EnderStoragePlace.INSTANCE);
        register(EnderStorageTank.INSTANCE);

        // Color Glyphs for all MC colors
        for (AbstractAugment augment : ColorAugment.getAllColorGlyphs()) {
            register(augment);
        }
    }

    private static void register(AbstractSpellPart spellPart) {
        ArsNouveauAPI.getInstance().registerSpell(spellPart.tag, spellPart);
        registeredSpells.add(spellPart);
    }
}
