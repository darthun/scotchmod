package com.darthun.common.tiles;

import com.darthun.common.blocks.SteepBlock;
import com.darthun.core.init.TileEntityInit;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class SteepBlockTileEntity extends TileEntity {
    public SteepBlockTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }
    /*    public SteepControllerTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }
    public SteepControllerTileEntity(){
        this(TileEntityInit.STEEPCONTROLLERTILEENTITY.get());
    }*/
    public SteepBlockTileEntity(){
        this(TileEntityInit.STEEPBLOCKTILEENTITY.get());
    }

}
