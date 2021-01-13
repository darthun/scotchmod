package com.darthun.common.blocks;

import com.darthun.client.util.ClientUtils;
import com.darthun.common.tiles.SteepBlockTileEntity;
import com.darthun.common.tiles.SteepControllerTileEntity;
import com.darthun.core.init.BlockInit;
import com.darthun.core.init.TileEntityInit;
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
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;

import net.minecraft.util.math.BlockPos;

import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.stream.Stream;

import static net.minecraft.block.HorizontalBlock.*;

public class SteepBlock extends Block {
    private static final VoxelShape TOP_SHAPE = makeCuboidShape(0, 0, 0, 16, 1, 16);
    private static final VoxelShape BOTTOM_SHAPE = makeCuboidShape(0, 0, 0, 1, 1, 1);
    private static final VoxelShape SHAPE = VoxelShapes.or(BOTTOM_SHAPE, TOP_SHAPE);
    private static final VoxelShape SHAPE_MACHINECENTER_NORTH = VoxelShapes.combineAndSimplify(Block.makeCuboidShape(0, 0, 0, 16, 1, 16),
            Block.makeCuboidShape(0, 1, 0, 16, 16, 1), IBooleanFunction.OR);
    private static final VoxelShape SHAPE_MACHINECORNER_NORTH = Stream.of(
            Block.makeCuboidShape(0, 1, 0, 16, 16, 1),
            Block.makeCuboidShape(0, 0, 0, 16, 1, 16),
            Block.makeCuboidShape(15, 1, 1, 16, 16, 16)
    ).reduce((v1, v2) -> {return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);}).get();
    private static final VoxelShape SHAPE_MACHINECENTER_EAST = ClientUtils.rotateShape(Direction.NORTH,Direction.EAST,SHAPE_MACHINECENTER_NORTH);
    private static final VoxelShape SHAPE_MACHINECENTER_SOUTH = ClientUtils.rotateShape(Direction.NORTH,Direction.SOUTH,SHAPE_MACHINECENTER_NORTH);
    private static final VoxelShape SHAPE_MACHINECENTER_WEST = ClientUtils.rotateShape(Direction.NORTH,Direction.WEST,SHAPE_MACHINECENTER_NORTH);
    private static final VoxelShape SHAPE_MACHINECORNER_EAST = ClientUtils.rotateShape(Direction.NORTH,Direction.EAST,SHAPE_MACHINECORNER_NORTH);
    private static final VoxelShape SHAPE_MACHINECORNER_SOUTH = ClientUtils.rotateShape(Direction.NORTH,Direction.SOUTH,SHAPE_MACHINECORNER_NORTH);
    private static final VoxelShape SHAPE_MACHINECORNER_WEST = ClientUtils.rotateShape(Direction.NORTH,Direction.WEST,SHAPE_MACHINECORNER_NORTH);

    public static final DirectionProperty FACING = HORIZONTAL_FACING;
    public static final BooleanProperty MACHINECENTER = BooleanProperty.create("machinecenter");
    public static final BooleanProperty MACHINECORNER = BooleanProperty.create("machinecorner");

    public SteepBlock(Properties p_i48440_1_) {
        super(p_i48440_1_);
        this.setDefaultState(this.getStateContainer().getBaseState()
                .with(MACHINECENTER, false)
                .with(MACHINECORNER,false)
                .with(FACING, Direction.NORTH));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        //super.fillStateContainer(builder);
        builder.add(MACHINECENTER);
        builder.add(MACHINECORNER);
        builder.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        //TODO Switch case here

        if(state.get(MACHINECENTER).booleanValue())
        {
            if(state.get(MACHINECORNER).booleanValue())
            {
                switch(state.get(FACING))
                {
                    case NORTH:
                        return SHAPE_MACHINECORNER_NORTH;
                    case EAST:
                        return SHAPE_MACHINECORNER_EAST;
                    case SOUTH:
                        return SHAPE_MACHINECORNER_SOUTH;
                    case WEST:
                        return SHAPE_MACHINECORNER_WEST;
                    default:
                        return SHAPE_MACHINECORNER_NORTH;
                }
            }
            switch(state.get(FACING))
            {
                case NORTH:
                    return SHAPE_MACHINECENTER_NORTH;
                case EAST:
                    return SHAPE_MACHINECENTER_EAST;
                case SOUTH:
                    return SHAPE_MACHINECENTER_SOUTH;
                case WEST:
                    return SHAPE_MACHINECENTER_WEST;
                default:
                    return SHAPE_MACHINECENTER_NORTH;
            }
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
