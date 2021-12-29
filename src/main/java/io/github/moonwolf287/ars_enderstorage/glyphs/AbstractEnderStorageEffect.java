package io.github.moonwolf287.ars_enderstorage.glyphs;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.enderstorage.storage.EnderLiquidStorage;
import codechicken.lib.colour.EnumColour;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import io.github.moonwolf287.ars_enderstorage.EnderSpellSchool;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Set;

public abstract class AbstractEnderStorageEffect extends AbstractEffect {

    protected AbstractEnderStorageEffect(String tag, String description) {
        super(tag, description);
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    protected static EnderItemStorage getItemStorage(World world, Frequency frequency) {
        return EnderStorageManager.instance(world.isClientSide).getStorage(frequency, EnderItemStorage.TYPE);
    }

    protected static EnderLiquidStorage getLiquidStorage(World world, Frequency frequency) {
        return EnderStorageManager.instance(world.isClientSide).getStorage(frequency, EnderLiquidStorage.TYPE);
    }

    protected static Frequency loadFrequency(SpellStats spellStats) {
        Frequency frequency = new Frequency();
        int colourIndex = 1;
        for (AbstractAugment augment : spellStats.getAugments()) {
            if (augment instanceof ColorAugment) {
                EnumColour colour = ((ColorAugment) augment).getColour();
                frequencySetColor(colourIndex, colour, frequency);

                colourIndex++;
                if (colourIndex > 3) {
                    break;
                }
            }
        }
        return frequency;
    }

    private static void frequencySetColor(int index, EnumColour colour, Frequency frequency) {
        // @formatter:off
        switch (index) {
            case 1: frequency.setLeft(colour); break;
            case 2: frequency.setMiddle(colour); break;
            case 3: frequency.setRight(colour); break;
            default: break;
        }
        // @formatter:on
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(EnderSpellSchool.INSTANCE);
    }
}
