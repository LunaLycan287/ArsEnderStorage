package io.github.moonwolf287.ars_enderstorage.glyphs;

import codechicken.enderstorage.storage.EnderItemStorage;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class EnderStorageBreak extends AbstractEnderStorageEffect {

    public static final EnderStorageBreak INSTANCE = new EnderStorageBreak("enderstorage_break", "EnderStorage Break");

    protected EnderStorageBreak(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter,
                          SpellStats spellStats, SpellContext spellContext) {
        if (shooter == null) {
            return; // This can only happen from other add-ons and is required to check for calcAOEBlocks
        }

        if (rayTraceResult.getType() == RayTraceResult.Type.BLOCK) {
            BlockRayTraceResult blockTraceResult = (BlockRayTraceResult) rayTraceResult;
            EnderItemStorage storage = getItemStorage(world, loadFrequency(spellStats));
            ItemStack toolStack = getToolStack(spellStats);

            List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, blockTraceResult.getBlockPos(), blockTraceResult,
                                                             spellStats);

            for (BlockPos pos : posList) {
                if (!isSafeHarvestable(world, shooter, spellStats, pos)) {
                    continue;
                }

                BlockState state = world.getBlockState(pos);
                Block block = state.getBlock();
                List<ItemStack> dropStack;
                if (spellStats.hasBuff(AugmentExtract.INSTANCE)) {
                    toolStack.enchant(Enchantments.SILK_TOUCH, 1);
                    Item item = block.asItem();
                    dropStack = Collections.singletonList(new ItemStack(item));
                } else {
                    int bonus = 0;
                    if (spellStats.hasBuff(AugmentFortune.INSTANCE)) {
                        bonus = spellStats.getBuffCount(AugmentFortune.INSTANCE);
                        toolStack.enchant(Enchantments.BLOCK_FORTUNE, bonus);
                    }
                    block.popExperience((ServerWorld) world, pos, state.getExpDrop(world, pos, bonus, 0));
                    dropStack = Block.getDrops(state, (ServerWorld) world, pos, null, null, toolStack);
                }

                if (!placeItemInStorage(storage, dropStack)) {
                    state.getBlock()
                         .playerDestroy(world, getPlayer(shooter, (ServerWorld) world), pos, world.getBlockState(pos),
                                        world.getBlockEntity(pos), toolStack);
                }
                BlockUtil.destroyBlockSafely(world, pos, false, shooter);
            }
        }
    }

    private boolean isSafeHarvestable(World world, @Nullable LivingEntity shooter, SpellStats spellStats,
                                      BlockPos pos) {
        return canBlockBeHarvested(spellStats, world, pos) &&
               BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerWorld) world), world, pos);
    }

    private ItemStack getToolStack(SpellStats spellStats) {
        return spellStats.hasBuff(AugmentSensitive.INSTANCE) ? new ItemStack(Items.SHEARS)
                                                             : new ItemStack(Items.DIAMOND_PICKAXE);
    }

    private boolean placeItemInStorage(EnderItemStorage storage, List<ItemStack> dropStacks) {
        boolean result = false;
        for (ItemStack stack : dropStacks) {
            for (int iSlot = 0; iSlot < storage.getContainerSize(); iSlot++) {
                ItemStack stackInSlot = storage.getItem(iSlot);
                if (stackInSlot.isEmpty()) {
                    storage.setItem(iSlot, stack.copy());
                    stack.setCount(0);
                } else if (stackInSlot.getItem() == stack.getItem()) {
                    int spaceLeft = storage.getMaxStackSize() - stackInSlot.getCount();
                    if (spaceLeft > 0) {
                        int stored = Math.min(spaceLeft, stack.getCount());
                        stackInSlot.grow(stored);
                        stack.shrink(stored);
                    }
                }

                if (stack.isEmpty()) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public String getBookDescription() {
        return "Breaks blocks and places them in an EnderStorage ender chest inventory. The Frequency depends on the " +
               "Color Augment modifiers.";
    }

    @Override
    public int getManaCost() {
        return 110;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        AbstractAugment[] augments = ArrayUtils.addAll(ColorAugment.getAllColorGlyphs(), AugmentAmplify.INSTANCE,
                                                       AugmentDampen.INSTANCE, AugmentPierce.INSTANCE,
                                                       AugmentAOE.INSTANCE, AugmentExtract.INSTANCE,
                                                       AugmentFortune.INSTANCE, AugmentSensitive.INSTANCE);
        return augmentSetOf(augments);
    }
}
