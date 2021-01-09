package com.darthun.common.tiles;

import com.darthun.core.init.BlockInit;
import com.darthun.core.init.TileEntityInit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class BogEarthTileEntity extends TileEntity implements ITickableTileEntity {
    private int tickCounter = 0;

    public BogEarthTileEntity(final TileEntityType<?> tileEntityTypeIn){
        super(tileEntityTypeIn);
    }
    public BogEarthTileEntity(){
         this(TileEntityInit.BOGEARTH.get());
    }

    private boolean isMoistened(BlockPos pos) {
        Iterable<BlockPos> surroundings = BlockPos.getAllInBoxMutable(pos.add(-2, -2, -2), pos.add(2, 2, 2));
        for( BlockPos s:surroundings)
        {
            FluidState fluidState = world.getFluidState(s);
            Fluid f = fluidState.getFluid();
            if( f== Fluids.WATER || f == Fluids.FLOWING_WATER)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void tick() {
        tickCounter++;
        if (tickCounter >= 200)
        {
            tickCounter=0;
            if(this.isMoistened(this.pos))
            {
                this.world.setBlockState(this.pos, BlockInit.PEATBLOCK.get().getDefaultState());
                this.world.removeTileEntity(this.pos);
            }
        }
    }
}
