package com.darthun.common.tiles;

import com.darthun.common.blocks.SteepBlock;
import com.darthun.common.blocks.SteepController;
import com.darthun.core.init.BlockInit;
import com.darthun.core.init.TileEntityInit;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SteepBlockTileEntity extends TileEntity {
    public SteepBlockTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public SteepBlockTileEntity(){
        this(TileEntityInit.STEEPBLOCKTILEENTITY.get());
    }
}
