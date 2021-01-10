package com.darthun.common.tiles;

import com.darthun.core.init.TileEntityInit;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class SteepControllerTileEntity extends TileEntity  {
    public SteepControllerTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }
    public SteepControllerTileEntity(){
        this(TileEntityInit.STEEPCONTROLLERTILEENTITY.get());
    }
}
