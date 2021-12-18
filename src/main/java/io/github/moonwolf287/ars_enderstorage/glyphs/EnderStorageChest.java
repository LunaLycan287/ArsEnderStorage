package io.github.moonwolf287.ars_enderstorage.glyphs;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.item.ItemEnderPouch;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.lib.colour.EnumColour;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtract;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentFortune;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectBreak;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectPlaceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class EnderStorageChest extends AbstractEffect {

    public static final EnderStorageChest INSTANCE = new EnderStorageChest("enderstorage_chest", "EnderStorage Chest");

    protected EnderStorageChest(String tag, String description) {
        super(tag, description);
    }

    enum InteractionType {
        OPEN,
        PLACE,
        BREAK
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter,
                          SpellStats spellStats, SpellContext spellContext) {
        Spell spell = spellContext.getSpell();
        Frequency freq = new Frequency();

        InteractionType type = InteractionType.OPEN;
        List<AbstractAugment> augments = new ArrayList<AbstractAugment>() {};
        int colorPosition = 1;
        while(spellContext.getCurrentIndex() < spell.getSpellSize()) {
            AbstractSpellPart part = spellContext.nextSpell();
            if(part instanceof ColorGlyph) {
               frequencySetColor(colorPosition++, ((ColorGlyph) part).getColour(), freq);
            }  else {
                if (part instanceof EffectPlaceBlock) {
                    type = InteractionType.PLACE;
                    augments = spell.getAugments(spellContext.getCurrentIndex() - 1, shooter);
                } else if (part instanceof EffectBreak) {
                    type = InteractionType.BREAK;
                    augments = spell.getAugments(spellContext.getCurrentIndex() - 1, shooter);
                }
                break;
            }
        }

        if(type == InteractionType.OPEN) {
            doOpenChest(world, shooter, freq);
        } else if (rayTraceResult.getType() == RayTraceResult.Type.BLOCK) {
            BlockRayTraceResult blockTraceResult = (BlockRayTraceResult) rayTraceResult;
            EnderItemStorage storage = EnderStorageManager.instance(world.isClientSide).getStorage(freq,
                                                                                                   EnderItemStorage.TYPE);

            SpellStats subSpellStats = getSpellStats(blockTraceResult, world, shooter, spellContext, augments);
            List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, blockTraceResult.getBlockPos(),
                                                             blockTraceResult, subSpellStats);

            for (BlockPos pos : posList) {
                if (type == InteractionType.PLACE) {
                        doPlaceBlock(blockTraceResult, pos, storage, world);
                } else { //type == InteractionType.BREAK
                         doBreakBlock(pos, storage, world, subSpellStats, shooter);
                }
            }
        }

        // FIXME: We have to cancel the Spell after our special cases have been handled, otherwise SpellResolver will break
        /*
            See SpellResolver::resolveEffects
            for(int i = 0; i < spell.recipe.size() && !spellContext.isCanceled(); ++i) {
                AbstractSpellPart part = spellContext.nextSpell();

            This loop does not care what teh current index actually is and will stop on the recipe size only.
            Which means, that we would completely break the resolving even if we adjust the spell index.
         */
        spellContext.setCanceled(true);
    }

    private void frequencySetColor(int index, EnumColour colour, Frequency frequency) {
        // @formatter:off
        switch (index) {
            case 1: frequency.setLeft(colour); break;
            case 2: frequency.setMiddle(colour); break;
            case 3: frequency.setRight(colour); break;
            default: break;
        }
        // @formatter:on
    }

    private void doOpenChest(World world, LivingEntity shooter, Frequency frequency) {
        if(!world.isClientSide && this.isRealPlayer(shooter)) {
            EnderStorageManager.instance(world.isClientSide).getStorage(frequency, EnderItemStorage.TYPE)
                               .openContainer((ServerPlayerEntity) shooter, new TranslationTextComponent(getLocaleName()));
        }
    }

    private void doPlaceBlock(BlockRayTraceResult blockTraceResult, BlockPos pos, EnderItemStorage storage,
                              World world) {
        BlockPos hitPos = blockTraceResult.isInside() ? pos : pos.relative(blockTraceResult.getDirection());
        BlockRayTraceResult resolveResult = new BlockRayTraceResult(
                new Vector3d(hitPos.getX(), hitPos.getY(), hitPos.getZ()), blockTraceResult.getDirection(), hitPos, false);
        if (!storage.isEmpty()) {
            for (int iSlot = 0; iSlot < storage.getContainerSize(); iSlot++) {
                ItemStack stack = storage.getItem(iSlot);
                if (!stack.isEmpty() && stack.getItem() instanceof BlockItem) {
                    BlockItem item = (BlockItem) stack.getItem();
                    EffectPlaceBlock.attemptPlace(world, stack, item, resolveResult);
                    break;
                }
            }
        }
    }

    private void doBreakBlock(BlockPos pos, EnderItemStorage storage,
                              World world, SpellStats subSpellStats, @Nullable LivingEntity shooter) {
        if(!canBlockBeHarvested(subSpellStats, world, pos) ||
           !BlockUtil.destroyRespectsClaim(getPlayer(shooter,(ServerWorld) world),world, pos)) {
            return;
        }
        ItemStack toolStack = subSpellStats.hasBuff(AugmentSensitive.INSTANCE) ? new ItemStack(Items.SHEARS) :
                          new ItemStack(Items.DIAMOND_PICKAXE);

        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        List<ItemStack> dropStack;
        if(subSpellStats.hasBuff(AugmentExtract.INSTANCE)) {
            toolStack.enchant(Enchantments.SILK_TOUCH, 1);
            Item item = block.asItem();
            dropStack = Collections.singletonList(new ItemStack(item));
        } else {
            int bonus = 0;
            if (subSpellStats.hasBuff(AugmentFortune.INSTANCE)) {
                bonus = subSpellStats.getBuffCount(AugmentFortune.INSTANCE);
                toolStack.enchant(Enchantments.BLOCK_FORTUNE, bonus);
            }
            block.popExperience((ServerWorld) world, pos, state.getExpDrop(world, pos, bonus, 0));
            dropStack = Block.getDrops(state,(ServerWorld) world, pos, null, null, toolStack);
        }

        if(!placeItemInStorage(storage, dropStack)) {
            state.getBlock().playerDestroy(world, getPlayer(shooter, (ServerWorld) world), pos, world.getBlockState(pos), world.getBlockEntity(pos), toolStack);
        }
        BlockUtil.destroyBlockSafely(world, pos, false, shooter);
    }

    private boolean placeItemInStorage(EnderItemStorage storage, List<ItemStack> dropStacks) {
        boolean result = false;
        for (ItemStack stack : dropStacks) {
            for (int iSlot = 0; iSlot < storage.getContainerSize(); iSlot++) {
                ItemStack stackInSlot = storage.getItem(iSlot);
                if(stackInSlot.isEmpty()) {
                    storage.setItem(iSlot, stack.copy());
                    stack.setCount(0);
                } else if(stackInSlot.getItem() == stack.getItem()) {
                    int spaceLeft = storage.getMaxStackSize() - stackInSlot.getCount();
                    if(spaceLeft > 0) {
                        int stored = Math.min(spaceLeft, stack.getCount());
                        stackInSlot.grow(stored);
                        stack.shrink(stored);
                    }
                }

                if(stack.isEmpty()) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    private SpellStats getSpellStats(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter,
                               SpellContext spellContext, List<AbstractAugment> augments) {
        SpellStats.Builder builder = new SpellStats.Builder();
        AbstractSpellPart part = spellContext.getSpell().recipe.get(spellContext.getCurrentIndex()-1);
        return builder.setAugments(augments).addItemsFromEntity(shooter).build(part, rayTraceResult, world, shooter,
                                                                               spellContext);
    }

    @Nullable
    @Override
    public Item getCraftingReagent() { return new ItemEnderPouch(); }

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
        return augmentSetOf();
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }
}
