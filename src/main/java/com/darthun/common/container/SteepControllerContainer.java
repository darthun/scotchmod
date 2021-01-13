package com.darthun.common.container;

import com.darthun.common.tiles.SteepControllerTileEntity;
import com.darthun.core.init.BlockInit;
import com.darthun.core.init.ContainerInit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.event.world.NoteBlockEvent;

import net.minecraft.inventory.container.Container;
import java.util.Objects;

public class SteepControllerContainer extends Container{
    public final SteepControllerTileEntity tileEntity;
    private final IWorldPosCallable canInteractWithCallable;
    public SteepControllerContainer(final int windowId, final PlayerInventory playerInv,
                                    final SteepControllerTileEntity tileEntityIn){
        super(ContainerInit.STEEPCONTROLLER.get(),windowId);
        this.tileEntity = tileEntityIn;
        this.canInteractWithCallable = IWorldPosCallable.of(tileEntityIn.getWorld(),tileEntityIn.getPos());
        this.addSlot(new Slot(tileEntityIn,0,49,16));

        //Main Inventory 8 84
        int startx = 8;
        int starty = 86;
        int slotGap = 18;
        for(int row=0;row < 3;row++){
            for(int column = 0;column < 9;column++){
                this.addSlot(new Slot(playerInv,9+(row*9)+column,startx+(column*slotGap),starty+(row*slotGap)));
            }
        }
        //HotBAR
        int hotbarY = 142;
        for(int column = 0;column <9;column++){
            this.addSlot(new Slot(playerInv,column,startx+(column*slotGap), hotbarY));
        }
    }

    public SteepControllerContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data){
        this(windowId,playerInventory,getTileEntity(playerInventory,data));
    }

    private static SteepControllerTileEntity getTileEntity(final PlayerInventory playerInv, final PacketBuffer data){
        Objects.requireNonNull(playerInv,"PlayerInv cannot be null");
        Objects.requireNonNull(data,"data cannot be null");
        final TileEntity tileAtPos = playerInv.player.world.getTileEntity(data.readBlockPos());
        if(tileAtPos instanceof SteepControllerTileEntity){
            return (SteepControllerTileEntity) tileAtPos;
        }
        throw new IllegalStateException("TileEntity is not correct " + tileAtPos);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerEntity) {
        return isWithinUsableDistance(canInteractWithCallable,playerEntity, BlockInit.STEEPCONTROLLER.get());
    }


}
