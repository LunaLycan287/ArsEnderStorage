package io.github.moonwolf287.ars_enderstorage.glyphs;

import codechicken.enderstorage.storage.EnderItemStorage;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectPlaceBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class EnderStoragePlace extends AbstractEnderStorageEffect {

    public static final EnderStoragePlace INSTANCE = new EnderStoragePlace("enderstorage_place",
                                                                           "EnderStorage Place Block");

    protected EnderStoragePlace(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter,
                          SpellStats spellStats, SpellContext spellContext) {
        if (rayTraceResult.getType() == RayTraceResult.Type.BLOCK) {
            BlockRayTraceResult blockTraceResult = (BlockRayTraceResult) rayTraceResult;
            EnderItemStorage storage = getItemStorage(world, loadFrequency(spellStats));
            FakePlayer fakePlayer = ANFakePlayer.getPlayer((ServerWorld) world);

            assert shooter != null;
            List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, blockTraceResult.getBlockPos(), blockTraceResult,
                                                             spellStats);

            for (BlockPos pos : posList) {
                BlockPos hitPos = blockTraceResult.isInside() ? pos : pos.relative(blockTraceResult.getDirection());
                BlockRayTraceResult resolveResult = new BlockRayTraceResult(
                        new Vector3d(hitPos.getX(), hitPos.getY(), hitPos.getZ()), blockTraceResult.getDirection(),
                        hitPos, false);

                if (!storage.isEmpty()) {
                    for (int iSlot = 0; iSlot < storage.getContainerSize(); iSlot++) {
                        ItemStack stack = storage.getItem(iSlot);
                        if (!stack.isEmpty() && stack.getItem() instanceof BlockItem) {
                            BlockItem item = (BlockItem) stack.getItem();
                            fakePlayer.setItemInHand(Hand.MAIN_HAND, stack);
                            EffectPlaceBlock.attemptPlace(world, stack, item, resolveResult, fakePlayer);
                            break;
                        }
                    }
                }

            } // for BlockPos pos : posList
        }
    }

    @Override
    public String getBookDescription() {
        return "Places Blocks from an EnderStorage ender chest inventory. The Frequency depends on the Color Augment" +
               " modifiers.";
    }

    @Override
    public int getManaCost() {
        return 110;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        AbstractAugment[] augments = ArrayUtils.addAll(ColorAugment.getAllColorGlyphs(), AugmentAOE.INSTANCE,
                                                       AugmentPierce.INSTANCE);
        return augmentSetOf(augments);
    }
}
