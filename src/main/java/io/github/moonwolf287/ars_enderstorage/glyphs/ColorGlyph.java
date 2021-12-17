package io.github.moonwolf287.ars_enderstorage.glyphs;

import codechicken.lib.colour.EnumColour;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import java.util.Collections;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class ColorGlyph extends AbstractEffect {
    public static final ColorGlyph WHITE        = new ColorGlyph(EnumColour.WHITE     );
    public static final ColorGlyph ORANGE       = new ColorGlyph(EnumColour.ORANGE    );
    public static final ColorGlyph MAGENTA      = new ColorGlyph(EnumColour.MAGENTA   );
    public static final ColorGlyph LIGHT_BLUE   = new ColorGlyph(EnumColour.LIGHT_BLUE);
    public static final ColorGlyph YELLOW       = new ColorGlyph(EnumColour.YELLOW    );
    public static final ColorGlyph LIME         = new ColorGlyph(EnumColour.LIME      );
    public static final ColorGlyph PINK         = new ColorGlyph(EnumColour.PINK      );
    public static final ColorGlyph GRAY         = new ColorGlyph(EnumColour.GRAY      );
    public static final ColorGlyph LIGHT_GRAY   = new ColorGlyph(EnumColour.LIGHT_GRAY);
    public static final ColorGlyph CYAN         = new ColorGlyph(EnumColour.CYAN      );
    public static final ColorGlyph PURPLE       = new ColorGlyph(EnumColour.PURPLE    );
    public static final ColorGlyph BLUE         = new ColorGlyph(EnumColour.BLUE      );
    public static final ColorGlyph BROWN        = new ColorGlyph(EnumColour.BROWN     );
    public static final ColorGlyph GREEN        = new ColorGlyph(EnumColour.GREEN     );
    public static final ColorGlyph RED          = new ColorGlyph(EnumColour.RED       );
    public static final ColorGlyph BLACK        = new ColorGlyph(EnumColour.BLACK     );

    protected EnumColour colour;

    protected ColorGlyph(EnumColour colour) {
        super("color_" + colour.getSerializedName(), getDescription(colour));
        this.colour = colour;
    }

    private static String getDescription(EnumColour colour) {
        String color = colour.getSerializedName();
        String capitalizedColor = color.substring(0,1).toUpperCase() + color.substring(1);
        return capitalizedColor + " Frequency Spell";
    }

    @Override
    public Tier getTier() {
        return Tier.ONE;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return Collections.emptySet();
    }

    @Override
    public int getManaCost() { return 0; }

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
}
