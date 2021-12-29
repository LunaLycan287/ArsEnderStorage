package io.github.moonwolf287.ars_enderstorage.glyphs;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class EnderStorageChest extends AbstractEnderStorageEffect {

    public static final EnderStorageChest INSTANCE = new EnderStorageChest("enderstorage_chest", "EnderStorage Chest");

    protected EnderStorageChest(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter,
                          SpellStats spellStats, SpellContext spellContext) {
        if (!world.isClientSide && this.isRealPlayer(shooter)) {
            getItemStorage(world, loadFrequency(spellStats)).openContainer((ServerPlayerEntity) shooter,
                                                                           new TranslationTextComponent(
                                                                                   getLocaleName()));
        }
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return RegistryObject.of(new ResourceLocation("enderstorage", "ender_pouch"), ForgeRegistries.ITEMS).get();
    }

    @Override
    public String getBookDescription() {
        return "Opens an EnderStorage ender chest inventory from anywhere. The Frequency depends on the Color Augment" +
               " modifiers.";
    }

    @Override
    public int getManaCost() {
        return 100;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(ColorAugment.getAllColorGlyphs());
    }
}
