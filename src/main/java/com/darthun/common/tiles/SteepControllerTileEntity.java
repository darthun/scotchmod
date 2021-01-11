package com.darthun.common.tiles;

import com.darthun.core.init.BlockInit;
import com.darthun.core.init.TileEntityInit;
import com.darthun.scotchmod.ScotchMod;
import com.google.common.collect.Iterables;
import net.minecraft.block.Block;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class SteepControllerTileEntity extends TileEntity  {

    public SteepControllerTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public SteepControllerTileEntity(){
        this(TileEntityInit.STEEPCONTROLLERTILEENTITY.get());
    }

    public boolean assembleMachine(){
        Iterable<BlockPos> surroundings = BlockPos.getAllInBoxMutable(pos.add(-1,0,-1),pos.add(1,0,1));
        for (BlockPos s: surroundings)
        {
            Block b = world.getBlockState(s).getBlock();
            if (b != BlockInit.STEEPBLOCK.get() && !this.pos.equals(s)){
                return false;
            }
        }
        for(BlockPos s: surroundings)
        {
            world.setBlockState(s,BlockInit.EXAMPLE_BLOCK.get().getDefaultState());
            world.removeTileEntity(s);
        }
        return true;
    }
}
