package com.darthun.common.blocks;

import com.darthun.common.tiles.SteepControllerTileEntity;
import com.darthun.core.init.BlockInit;
import com.darthun.core.init.TileEntityInit;
import com.sun.org.apache.xpath.internal.operations.Bool;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class SteepBlock extends Block {
    private static final VoxelShape TOP_SHAPE = makeCuboidShape(0, 0, 0, 16, 1, 16);
    private static final VoxelShape BOTTOM_SHAPE = makeCuboidShape(0, 0, 0, 1, 1, 1);
    private static final VoxelShape SHAPE = VoxelShapes.or(BOTTOM_SHAPE, TOP_SHAPE);
    private static final VoxelShape SHAPE_MACHINECENTER = VoxelShapes.combineAndSimplify(Block.makeCuboidShape(0, 0, 0, 16, 1, 16),
            Block.makeCuboidShape(0, 1, 0, 16, 16, 1), IBooleanFunction.OR);
    private static final VoxelShape SHAPE_MACHINECORNER = Stream.of(
            Block.makeCuboidShape(0, 1, 0, 16, 16, 1),
            Block.makeCuboidShape(0, 0, 0, 16, 1, 16),
            Block.makeCuboidShape(15, 1, 1, 16, 16, 16)
    ).reduce((v1, v2) -> {return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);}).get();
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    public static final BooleanProperty MACHINECENTER = BooleanProperty.create("machinecenter");
    public static final BooleanProperty MACHINECORNER = BooleanProperty.create("machinecorner");

    public SteepBlock(Properties p_i48440_1_) {
        super(p_i48440_1_);
        this.setDefaultState(this.getStateContainer().getBaseState().with(MACHINECENTER, false).with(MACHINECORNER,false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        //super.fillStateContainer(builder);
        builder.add(MACHINECENTER);
        builder.add(MACHINECORNER);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        //TODO Switch case here
        if(state.get(MACHINECENTER).booleanValue())
        {
            System.out.println("machine center state true");
            if(state.get(MACHINECORNER).booleanValue())
            {
                System.out.println("machine corner state true");
                return SHAPE_MACHINECORNER;
            }
            return SHAPE_MACHINECENTER;

        }
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
