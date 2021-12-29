package io.github.moonwolf287.ars_enderstorage.glyphs;

import codechicken.enderstorage.storage.EnderLiquidStorage;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.wrappers.BucketPickupHandlerWrapper;
import net.minecraftforge.fluids.capability.wrappers.FluidBlockWrapper;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class EnderStorageTank extends AbstractEnderStorageEffect {
    public static final EnderStorageTank INSTANCE = new EnderStorageTank("enderstorage_tank", "EnderStorage Tank");

    protected EnderStorageTank(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter,
                          SpellStats spellStats, SpellContext spellContext) {
        if (rayTraceResult.getType() == RayTraceResult.Type.BLOCK) {
            BlockRayTraceResult blockTraceResult = (BlockRayTraceResult) rayTraceResult;
            EnderLiquidStorage storage = getLiquidStorage(world, loadFrequency(spellStats));

            assert shooter != null;
            List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, blockTraceResult.getBlockPos(), blockTraceResult,
                                                             spellStats);

            // Try picking up fluids first and if none were hit, we place liquid instead.
            if (!handlePickupLiquid(posList, world, blockTraceResult.getDirection(), storage)) {
                handlePlaceLiquid(posList, world, blockTraceResult.getDirection(), shooter, storage);
            }
        }
    }

    /***
     * Handle picking up of Liquid.
     * <p>
     * We try picking up every source block for every given position.
     * </p>
     * @param posList the positions to try.
     * @param world the world to resolve in.
     * @param direction the direction of the BlockRayTraceResult.
     * @param storage the EnderLiquidStorage to fill.
     *
     * @return true, if liquid blocks were hit.
     */
    protected static boolean handlePickupLiquid(List<BlockPos> posList, World world, Direction direction,
                                                EnderLiquidStorage storage) {
        boolean hitLiquid = false;
        for (BlockPos pos : posList) {
            if (!world.getFluidState(pos).isEmpty() && world.getFluidState(pos).isSource()) {
                hitLiquid = true;

                // Don't need to try picking up if we are full anyway
                if ((storage.getFluidAmount() + FluidAttributes.BUCKET_VOLUME) > storage.getCapacity()) {
                    break;
                }

                Fluid fluid = world.getFluidState(pos).getType();
                // storage.getFluid = FluidStack to get actual fluid we have to do FluidStack.getFluid() again
                if ((storage.getFluidAmount() == 0 || storage.getFluid().getFluid() == fluid) &&
                    tryPickUpFluid(world, pos, direction, storage)) {
                    world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
        return hitLiquid;
    }

    private void handlePlaceLiquid(List<BlockPos> posList, World world, Direction direction,
                                   @Nullable LivingEntity shooter, EnderLiquidStorage storage) {
        for (BlockPos pos : posList) {
            if (storage.getFluidAmount() < FluidAttributes.BUCKET_VOLUME) {
                return;
            }

            // storage.getFluid = FluidStack to get actual fluid we have to do FluidStack.getFluid() again
            Fluid fluid = storage.getFluid().getFluid();

            Block block = world.getBlockState(pos).getBlock();
            if (block instanceof IWaterLoggable &&
                ((IWaterLoggable) block).canPlaceLiquid(world, pos, world.getBlockState(pos), fluid)) {

                ((IWaterLoggable) block).placeLiquid(world, pos, world.getBlockState(pos), fluid.defaultFluidState());
                storage.drain(FluidAttributes.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
            } else {
                // TODO: use blockState.isAir() in 1.17
                BlockPos hitPos = world.getBlockState(pos).getBlock().is(Blocks.AIR) ? pos : pos.relative(direction);
                if (!BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerWorld) world), world, pos)) {
                    continue;
                }

                if (world.getBlockState(hitPos).canBeReplaced(fluid)) {
                    world.setBlockAndUpdate(hitPos, fluid.defaultFluidState().createLegacyBlock());
                    storage.drain(FluidAttributes.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
                }
            }
        }
    }

    private static boolean tryPickUpFluid(World worldIn, BlockPos pos, Direction side, EnderLiquidStorage storage) {
        BlockState state = worldIn.getBlockState(pos);
        Block block = state.getBlock();
        IFluidHandler targetFluidHandler;
        if (block instanceof IFluidBlock) {
            targetFluidHandler = new FluidBlockWrapper((IFluidBlock) block, worldIn, pos);
        } else if (block instanceof IBucketPickupHandler) {
            targetFluidHandler = new BucketPickupHandlerWrapper((IBucketPickupHandler) block, worldIn, pos);
        } else {
            Optional<IFluidHandler> fluidHandler = FluidUtil.getFluidHandler(worldIn, pos, side).resolve();
            if (!fluidHandler.isPresent()) {
                return false;
            }
            targetFluidHandler = fluidHandler.get();
        }

        return FluidUtil.tryFluidTransfer(storage, targetFluidHandler, FluidAttributes.BUCKET_VOLUME, true) !=
               FluidStack.EMPTY;
    }

    @Override
    public int getManaCost() {
        return 80;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return RegistryObject.of(new ResourceLocation("enderstorage", "ender_tank"), ForgeRegistries.ITEMS).get();
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        AbstractAugment[] augments = ArrayUtils.add(ColorAugment.getAllColorGlyphs(), AugmentAOE.INSTANCE);
        return augmentSetOf(augments);
    }

    @Override
    public String getBookDescription() {
        return "Picks up or places liquids to / from and EnderStorage Tank. The Frequency depends on the Color " +
               "Augment modifiers.";
    }
}
