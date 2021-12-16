package io.github.moonwolf287.ars_enderstorage.glyphs;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

public class AugmentColor extends AbstractAugment {

    public static final AugmentColor INSTANCE_BLACK =
            new AugmentColor("color_black", "Color Black", Items.BLACK_DYE);
    public static final AugmentColor INSTANCE_BLUE =
            new AugmentColor("color_blue", "Color Blue", Items.BLUE_DYE);
    public static final AugmentColor INSTANCE_BROWN =
            new AugmentColor("color_brown", "Color Brown", Items.BROWN_DYE);
    public static final AugmentColor INSTANCE_CYAN =
            new AugmentColor("color_cyan", "Color Cyan", Items.CYAN_DYE);
    public static final AugmentColor INSTANCE_GRAY =
            new AugmentColor("color_gray", "Color Gray", Items.GRAY_DYE);
    public static final AugmentColor INSTANCE_GREEN =
            new AugmentColor("color_green", "Color Green", Items.GREEN_DYE);
    public static final AugmentColor INSTANCE_LIGHT_BLUE =
            new AugmentColor("color_light_blue", "Color Light Blue", Items.LIGHT_BLUE_DYE);
    public static final AugmentColor INSTANCE_LIGHT_GRAY =
            new AugmentColor("color_light_gray", "Color Light Gray", Items.LIGHT_GRAY_DYE);
    public static final AugmentColor INSTANCE_LIME =
            new AugmentColor("color_lime", "Color Lime", Items.LIME_DYE);
    public static final AugmentColor INSTANCE_MAGENTA =
            new AugmentColor("color_magenta", "Color Magenta", Items.MAGENTA_DYE);
    public static final AugmentColor INSTANCE_ORANGE =
            new AugmentColor("color_orange", "Color Orange", Items.ORANGE_DYE);
    public static final AugmentColor INSTANCE_PINK =
            new AugmentColor("color_pink", "Color Pink", Items.PINK_DYE);
    public static final AugmentColor INSTANCE_PURPLE =
            new AugmentColor("color_purple", "Color Purple", Items.PURPLE_DYE);
    public static final AugmentColor INSTANCE_RED =
            new AugmentColor("color_red", "Color Red", Items.RED_DYE);
    public static final AugmentColor INSTANCE_WHITE =
            new AugmentColor("color_white", "Color White", Items.WHITE_DYE);
    public static final AugmentColor INSTANCE_YELLOW =
            new AugmentColor("color_yellow", "Color Yellow", Items.YELLOW_DYE);

    protected Item reagent;

    protected AugmentColor(String tag, String description, Item reagent) {
        super(tag, description);
        this.reagent = reagent;
    }

    @Override
    public Tier getTier() {
        return Tier.ONE;
    }

    @Override
    public int getManaCost() { return 0; }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return reagent;
    }

    @Override
    public String getBookDescription() {
        return "Adds a color modification to the previous spell. This is used for EnderStorage EnderChest frequencies" +
               " for example.";
    }

    @Override
    public TranslationTextComponent getBookDescLang() {
        return new TranslationTextComponent("ars_enderstorage.glyph_desc." + this.getTag());
    }
    /*
    @Override
    public SpellStats.Builder applyModifiers(SpellStats.Builder builder, AbstractSpellPart spellPart) {
        builder.addItem(new ItemStack(this.reagent));
        return super.applyModifiers(builder, spellPart);
    }
    */

    public static AbstractAugment[] getAllAugmentColor() {
        return new AbstractAugment[]{INSTANCE_BLACK, INSTANCE_BLUE,INSTANCE_BROWN, INSTANCE_CYAN, INSTANCE_GRAY, INSTANCE_GREEN,
                                     INSTANCE_LIGHT_BLUE, INSTANCE_LIGHT_GRAY, INSTANCE_LIME, INSTANCE_MAGENTA,
                                     INSTANCE_ORANGE, INSTANCE_PINK, INSTANCE_PURPLE, INSTANCE_RED, INSTANCE_WHITE,
                                     INSTANCE_YELLOW};
    }
}
