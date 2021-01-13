package com.darthun.common.blocks;

import com.darthun.common.tiles.SteepBlockTileEntity;
import com.darthun.common.tiles.SteepControllerTileEntity;
import com.darthun.core.init.BlockInit;
import com.darthun.core.init.TileEntityInit;
import com.google.common.collect.Iterables;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

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

    @Override
    public void onBlockPlacedBy(World world, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        super.onBlockPlacedBy(world, blockPos, blockState, livingEntity, itemStack);
        SteepControllerTileEntity controllerTileEntity = (SteepControllerTileEntity) world.getTileEntity(blockPos);
        controllerTileEntity.assembleMachine();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        //return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
        if(!worldIn.isRemote){
            TileEntity tile = worldIn.getTileEntity(pos);
            if(tile instanceof SteepControllerTileEntity){
                NetworkHooks.openGui((ServerPlayerEntity) player,(SteepControllerTileEntity) tile,pos);
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.FAIL;
    }

    @Override
    public void onReplaced(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
        //super.onReplaced(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
        if(p_196243_1_.getBlock() != p_196243_4_.getBlock()){
            TileEntity tile = p_196243_2_.getTileEntity(p_196243_3_);
            if(tile instanceof SteepControllerTileEntity){
                InventoryHelper.dropItems(p_196243_2_,p_196243_3_,((SteepControllerTileEntity) tile).getItems());
            }
        }
    }

}
