package com.darthun.common.blocks;

import com.darthun.core.init.TileEntityInit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class SteepController extends Block {
    private static final VoxelShape TOP_SHAPE = makeCuboidShape(0, 0, 0, 16, 1, 16);
    private static final VoxelShape BOTTOM_SHAPE = makeCuboidShape(0, 0, 0, 1, 1, 1);
    private static final VoxelShape SHAPE = VoxelShapes.or(BOTTOM_SHAPE, TOP_SHAPE);

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    public SteepController(Properties p_i48440_1_) {
        super(p_i48440_1_);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return TileEntityInit.STEEPCONTROLLERTILEENTITY.get().create();
    }
}