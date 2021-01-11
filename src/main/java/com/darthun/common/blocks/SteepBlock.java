package com.darthun.common.blocks;

import com.darthun.common.tiles.SteepControllerTileEntity;
import com.darthun.core.init.BlockInit;
import com.darthun.core.init.TileEntityInit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class SteepBlock extends Block {
    private static final VoxelShape TOP_SHAPE = makeCuboidShape(0, 0, 0, 16, 1, 16);
    private static final VoxelShape BOTTOM_SHAPE = makeCuboidShape(0, 0, 0, 1, 1, 1);
    private static final VoxelShape SHAPE = VoxelShapes.or(BOTTOM_SHAPE, TOP_SHAPE);

    public SteepBlock(Properties p_i48440_1_) {
        super(p_i48440_1_);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }
    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return TileEntityInit.STEEPBLOCKTILEENTITY.get().create();
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        super.onBlockPlacedBy(world, blockPos, blockState, livingEntity, itemStack);
        SteepControllerTileEntity steepControllerTileEntity = this.getSteepControllerTileEntity(blockPos, world);
        if(steepControllerTileEntity!=null){
            steepControllerTileEntity.assembleMachine();
        }
    }

    private SteepControllerTileEntity getSteepControllerTileEntity(BlockPos pos, World world){

        Iterable<BlockPos> surroundings = BlockPos.getAllInBoxMutable(pos.add(-1,0,-1),pos.add(1,0,1));
        for(BlockPos s: surroundings)
        {
            Block b = world.getBlockState(s).getBlock();
            if( b == BlockInit.STEEPCONTROLLER.get())
            {
                SteepControllerTileEntity steepControllerTileEntity = (SteepControllerTileEntity) world.getTileEntity(s);
                return steepControllerTileEntity;
            }
        }
        return null;
    }

}
