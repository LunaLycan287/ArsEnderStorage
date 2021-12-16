package io.github.moonwolf287.ars_enderstorage;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import io.github.moonwolf287.ars_enderstorage.glyphs.AugmentColor;
import io.github.moonwolf287.ars_enderstorage.glyphs.EnderStorageChest;
import io.github.moonwolf287.ars_enderstorage.glyphs.TestEffect;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;

import java.util.ArrayList;
import java.util.List;

public class ArsNouveauRegistry {
    public static List<AbstractSpellPart> registeredSpells = new ArrayList<>();

    public static void registerGlyphs(){
        register(TestEffect.INSTANCE);
        register(EnderStorageChest.INSTANCE);

        AbstractAugment[] colorGlyphs = AugmentColor.getAllAugmentColor();
        for (AbstractAugment glyph : colorGlyphs){
            register(glyph);
        }
    }

    public static void register(AbstractSpellPart spellPart){
        ArsNouveauAPI.getInstance().registerSpell(spellPart.tag,spellPart);
        registeredSpells.add(spellPart);
    }
}
