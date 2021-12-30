package io.github.moonwolf287.ars_enderstorage.patchouli;

import com.hollingsworth.arsnouveau.api.recipe.GlyphPressRecipe;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import net.minecraft.client.Minecraft;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class NewGlyphPressProcessor implements IComponentProcessor {

    GlyphPressRecipe recipe;

    @Override
    public void setup(IVariableProvider variables) {
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
        String recipeID = variables.get("recipe").asString();
        recipe = (GlyphPressRecipe) manager.byKey(new ResourceLocation(recipeID)).orElse(null);
    }

    @Override
    public IVariable process(String key) {
        if (recipe == null) {
            return null;
        }
        if (key.equals("clay_type")) {
            return IVariable.from(recipe.getClay());
        }
        if (key.equals("reagent")) {
            return IVariable.from(recipe.reagent);
        }
        if (key.equals("output")) {
            return IVariable.from(recipe.output);
        }
        if (key.equals("tier")) {
            return IVariable.wrap(new TranslationTextComponent(
                    "ars_nouveau.spell_tier." + recipe.tier.toString().toLowerCase()).getString());
        }
        if (key.equals("mana_cost")) {
            return IVariable.wrap(((Glyph) recipe.output.getItem()).spellPart.getManaCost());
        }
        if (key.equals("schools")) {
            AbstractSpellPart part = ((Glyph) recipe.output.getItem()).spellPart;
            StringBuilder str = new StringBuilder("");
            for (SpellSchool spellSchool : part.getSchools()) {
                str.append(spellSchool.getTextComponent().getString()).append(",");
            }
            if (!part.getSchools().isEmpty()) {
                str = new StringBuilder(str.substring(0, str.length() - 1));
            }
            return IVariable.wrap(str.toString());
        }
        return null;
    }
}
