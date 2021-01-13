package com.darthun.common.tiles;

import com.darthun.common.blocks.SteepBlock;
import com.darthun.core.init.BlockInit;
import com.darthun.core.init.TileEntityInit;
import com.darthun.scotchmod.ScotchMod;
import com.google.common.collect.Iterables;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
        this.assembleAllBlockstates();
        return true;
    }

    private void assembleAllBlockstates(){
        //We need to set all the blocs in a 3x3(x1) fashion.
        //Let's work by row.
        BlockPos TOP_ROW_CENTER = this.pos.north();
        BlockPos TOP_LEFT_CORNER = TOP_ROW_CENTER.west();
        BlockPos TOP_RIGHT_CORNER = TOP_ROW_CENTER.east();
        BlockPos MIDDLE_LEFT_CENTER = this.pos.west();
        BlockPos MIDDLE_RIGHT_CENTER = this.pos.east();
        BlockPos BOTTOM_ROW_CENTER = this.pos.south();
        BlockPos BOTTOM_LEFT_CORNER = BOTTOM_ROW_CENTER.west();
        BlockPos BOTTOM_RIGHT_CORNER = BOTTOM_ROW_CENTER.east();

        world.setBlockState(TOP_LEFT_CORNER,world.getBlockState(TOP_LEFT_CORNER).with(SteepBlock.MACHINECENTER,true).with(SteepBlock.MACHINECORNER,true));
        world.setBlockState(TOP_RIGHT_CORNER,world.getBlockState(TOP_RIGHT_CORNER).with(SteepBlock.MACHINECENTER,true).with(SteepBlock.MACHINECORNER,true));
        world.setBlockState(BOTTOM_LEFT_CORNER,world.getBlockState(BOTTOM_LEFT_CORNER).with(SteepBlock.MACHINECENTER,true).with(SteepBlock.MACHINECORNER,true));
        world.setBlockState(BOTTOM_RIGHT_CORNER,world.getBlockState(BOTTOM_RIGHT_CORNER).with(SteepBlock.MACHINECENTER,true).with(SteepBlock.MACHINECORNER,true));
        world.setBlockState(TOP_ROW_CENTER,world.getBlockState(TOP_ROW_CENTER).with(SteepBlock.MACHINECENTER,true));
        world.setBlockState(MIDDLE_LEFT_CENTER,world.getBlockState(MIDDLE_LEFT_CENTER).with(SteepBlock.MACHINECENTER,true));
        world.setBlockState(MIDDLE_RIGHT_CENTER,world.getBlockState(MIDDLE_RIGHT_CENTER).with(SteepBlock.MACHINECENTER,true));
        world.setBlockState(BOTTOM_ROW_CENTER,world.getBlockState(BOTTOM_ROW_CENTER).with(SteepBlock.MACHINECENTER,true));

    }
}
