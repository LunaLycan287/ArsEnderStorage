package io.github.moonwolf287.ars_enderstorage.glyphs;

import codechicken.lib.colour.EnumColour;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import io.github.moonwolf287.ars_enderstorage.EnderSpellSchool;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class ColorAugment extends AbstractAugment {
    public static final ColorAugment WHITE      = new ColorAugment(EnumColour.WHITE);
    public static final ColorAugment ORANGE     = new ColorAugment(EnumColour.ORANGE);
    public static final ColorAugment MAGENTA    = new ColorAugment(EnumColour.MAGENTA);
    public static final ColorAugment LIGHT_BLUE = new ColorAugment(EnumColour.LIGHT_BLUE);
    public static final ColorAugment YELLOW     = new ColorAugment(EnumColour.YELLOW);
    public static final ColorAugment LIME       = new ColorAugment(EnumColour.LIME);
    public static final ColorAugment PINK       = new ColorAugment(EnumColour.PINK);
    public static final ColorAugment GRAY       = new ColorAugment(EnumColour.GRAY);
    public static final ColorAugment LIGHT_GRAY = new ColorAugment(EnumColour.LIGHT_GRAY);
    public static final ColorAugment CYAN       = new ColorAugment(EnumColour.CYAN);
    public static final ColorAugment PURPLE     = new ColorAugment(EnumColour.PURPLE);
    public static final ColorAugment BLUE       = new ColorAugment(EnumColour.BLUE);
    public static final ColorAugment BROWN      = new ColorAugment(EnumColour.BROWN);
    public static final ColorAugment GREEN      = new ColorAugment(EnumColour.GREEN);
    public static final ColorAugment RED        = new ColorAugment(EnumColour.RED);
    public static final ColorAugment BLACK      = new ColorAugment(EnumColour.BLACK);

    protected EnumColour colour;

    protected ColorAugment(EnumColour colour) {
        super("color_" + colour.getSerializedName(), getDescription(colour));
        this.colour = colour;
    }

    private static String getDescription(EnumColour colour) {
        String color = colour.getSerializedName();
        String capitalizedColor = color.substring(0, 1).toUpperCase() + color.substring(1);
        return capitalizedColor + " Frequency Spell";
    }

    public EnumColour getColour() {return this.colour;}

    @Override
    public Tier getTier() {
        return Tier.ONE;
    }


    @Override
    public int getManaCost() {return 0;}

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return RegistryObject.of(colour.getDyeTagName(), ForgeRegistries.ITEMS).get();
    }

    @Override
    public String getBookDescription() {
        return "Adds a color modification to the previous spell. This is used for EnderStorage EnderChest frequencies" +
               " for example.";
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(EnderSpellSchool.INSTANCE);
    }

    public static AbstractAugment[] getAllColorGlyphs() {
        // @formatter:off
        return new AbstractAugment[] {
                    ColorAugment.WHITE,
                    ColorAugment.ORANGE,
                    ColorAugment.MAGENTA,
                    ColorAugment.LIGHT_BLUE,
                    ColorAugment.YELLOW,
                    ColorAugment.LIME,
                    ColorAugment.PINK,
                    ColorAugment.GRAY,
                    ColorAugment.LIGHT_GRAY,
                    ColorAugment.CYAN,
                    ColorAugment.PURPLE,
                    ColorAugment.BLUE,
                    ColorAugment.BROWN,
                    ColorAugment.GREEN,
                    ColorAugment.RED,
                    ColorAugment.BLACK
                };
        // @formatter:on
    }
}
