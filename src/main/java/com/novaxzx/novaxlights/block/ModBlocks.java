package com.novaxzx.novaxlights.block;

import com.mojang.serialization.MapCodec;
import com.novaxzx.novaxlights.NovaxLights;
import com.novaxzx.novaxlights.entity.AreaLightBlockEntity;
import com.novaxzx.novaxlights.entity.PointLightBlockEntity;
import com.novaxzx.novaxlights.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(NovaxLights.MODID);

    public static class PointLightBlock extends BaseEntityBlock {

        public static final IntegerProperty POWER = BlockStateProperties.POWER;

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(POWER);
        }

        @Override
        public BlockState getStateForPlacement(BlockPlaceContext context) {
            return defaultBlockState().setValue(POWER, 0);
        }

        public PointLightBlock() {
            super(BlockBehaviour.Properties.of()
                    .strength(4F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.ANCIENT_DEBRIS)
                    .noOcclusion()
            );

            this.registerDefaultState(
                    this.stateDefinition.any().setValue(POWER, 0)
            );
        }

        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new PointLightBlockEntity(pos, state);
        }

        @Override
        public RenderShape getRenderShape(BlockState state) {
            return RenderShape.MODEL;
        }

        @Override
        protected boolean useShapeForLightOcclusion(BlockState state) {
            return false;
        }

        @Override
        protected MapCodec<? extends BaseEntityBlock> codec() {
            return null;
        }

        @Override
        public void onPlace(
                BlockState state,
                Level level,
                BlockPos pos,
                BlockState oldState,
                boolean movedByPiston
        ) {
            super.onPlace(state, level, pos, oldState, movedByPiston);

            level.scheduleTick(pos, this, 1);
        }

        @Override
        protected void neighborChanged(
                BlockState state,
                Level level,
                BlockPos pos,
                Block block,
                BlockPos fromPos,
                boolean isMoving
        ) {
            super.neighborChanged(state, level, pos, block, fromPos, isMoving);

            level.scheduleTick(pos, this, 1);
        }

        @Override
        protected void tick(
                BlockState state,
                ServerLevel level,
                BlockPos pos,
                RandomSource random
        ) {
            int power = level.getBestNeighborSignal(pos);

            if(state.getValue(POWER) != power) {
                level.setBlock(
                        pos,
                        state.setValue(POWER, power),
                        3
                );
            }
        }

        @Override
        public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
                Level level,
                BlockState state,
                BlockEntityType<T> type
        ) {

            if(level.isClientSide) {

                return (lvl, pos, st, be) -> {

                    if(be instanceof PointLightBlockEntity lightBe) {
                        lightBe.clientTick();
                    }
                };
            }

            return null;
        }

        @Override
        protected InteractionResult useWithoutItem(
                BlockState state,
                Level level,
                BlockPos pos,
                Player player,
                BlockHitResult hit
        ) {
            return InteractionResult.PASS;
        }

        @Override
        protected ItemInteractionResult useItemOn(
                ItemStack stack,
                BlockState state,
                Level level,
                BlockPos pos,
                Player player,
                InteractionHand hand,
                BlockHitResult hit
        ) {

            if(stack.getItem() instanceof DyeItem dye) {

                if(!level.isClientSide && level.getBlockEntity(pos) instanceof PointLightBlockEntity be) {

                    DyeColor color = dye.getDyeColor();

                    be.setLightColor(color);

                    return ItemInteractionResult.SUCCESS;
                }
            }

            if(stack.is(ModItems.POINTLIGHTEDITOR)) {

                if(!level.isClientSide && level.getBlockEntity(pos) instanceof PointLightBlockEntity be) {

                    boolean decrease = hand == InteractionHand.OFF_HAND;

                    be.changeDistance(decrease);

                    return ItemInteractionResult.SUCCESS;
                }
            }

            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        @Override
        public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.novaxlights.point_light_block.tooltip"));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        }

    }

    public static class AreaLightBlock extends BaseEntityBlock {

        public static final IntegerProperty POWER = BlockStateProperties.POWER;

        public static final DirectionProperty FACING =
                BlockStateProperties.FACING;

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(POWER, FACING);
        }

        @Override
        public BlockState getStateForPlacement(BlockPlaceContext context) {
            return defaultBlockState()
                    .setValue(POWER, 0)
                    .setValue(FACING, context.getNearestLookingDirection().getOpposite());
        }

        public AreaLightBlock() {
            super(BlockBehaviour.Properties.of()
                    .strength(4F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.ANCIENT_DEBRIS)
                    .noOcclusion()
            );

            this.registerDefaultState(
                    this.stateDefinition.any()
                            .setValue(POWER, 0).setValue(FACING, Direction.NORTH)
            );
        }

        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new AreaLightBlockEntity(pos, state);
        }

        @Override
        public RenderShape getRenderShape(BlockState state) {
            return RenderShape.MODEL;
        }

        @Override
        protected boolean useShapeForLightOcclusion(BlockState state) {
            return false;
        }

        @Override
        protected MapCodec<? extends BaseEntityBlock> codec() {
            return null;
        }

        @Override
        public void onPlace(
                BlockState state,
                Level level,
                BlockPos pos,
                BlockState oldState,
                boolean movedByPiston
        ) {
            super.onPlace(state, level, pos, oldState, movedByPiston);

            level.scheduleTick(pos, this, 1);
        }

        @Override
        protected void neighborChanged(
                BlockState state,
                Level level,
                BlockPos pos,
                Block block,
                BlockPos fromPos,
                boolean isMoving
        ) {
            super.neighborChanged(state, level, pos, block, fromPos, isMoving);

            level.scheduleTick(pos, this, 1);
        }

        @Override
        protected void tick(
                BlockState state,
                ServerLevel level,
                BlockPos pos,
                RandomSource random
        ) {
            int power = level.getBestNeighborSignal(pos);

            if(state.getValue(POWER) != power) {
                level.setBlock(
                        pos,
                        state.setValue(POWER, power),
                        3
                );
            }
        }

        @Override
        public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
                Level level,
                BlockState state,
                BlockEntityType<T> type
        ) {

            if(level.isClientSide) {

                return (lvl, pos, st, be) -> {

                    if(be instanceof AreaLightBlockEntity lightBe) {
                        lightBe.clientTick();
                    }
                };
            }

            return null;
        }

        @Override
        protected InteractionResult useWithoutItem(
                BlockState state,
                Level level,
                BlockPos pos,
                Player player,
                BlockHitResult hit
        ) {
            return InteractionResult.PASS;
        }

        @Override
        protected ItemInteractionResult useItemOn(
                ItemStack stack,
                BlockState state,
                Level level,
                BlockPos pos,
                Player player,
                InteractionHand hand,
                BlockHitResult hit
        ) {

            if(stack.getItem() instanceof DyeItem dye) {

                if(!level.isClientSide && level.getBlockEntity(pos) instanceof AreaLightBlockEntity be) {

                    DyeColor color = dye.getDyeColor();

                    be.setLightColor(color);

                    return ItemInteractionResult.SUCCESS;
                }
            }

            if(stack.is(ModItems.POINTLIGHTEDITOR)) {

                if(!level.isClientSide && level.getBlockEntity(pos) instanceof AreaLightBlockEntity be) {

                    boolean decrease = hand == InteractionHand.OFF_HAND;

                    be.changeDistance(decrease);

                    return ItemInteractionResult.SUCCESS;
                }
            }
            if(stack.is(ModItems.AREALIGHTEDITOR)) {

                if(!level.isClientSide && level.getBlockEntity(pos) instanceof AreaLightBlockEntity be) {

                    boolean decrease = hand == InteractionHand.OFF_HAND;

                    be.changeAngle(decrease);

                    return ItemInteractionResult.SUCCESS;
                }
            }

            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        @Override
        public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.novaxlights.area_light_block.tooltip"));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        }
    }

    public static final DeferredBlock<Block> POINT_LIGHT_BLOCK = registerBlock("point_light_block",
            () -> new PointLightBlock()
    );

    public static final DeferredBlock<Block> AREA_LIGHT_BLOCK = registerBlock("area_light_block",
            () -> new AreaLightBlock()
    );

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
