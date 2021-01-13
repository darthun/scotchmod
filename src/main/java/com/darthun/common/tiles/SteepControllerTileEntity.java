package com.darthun.common.tiles;

import com.darthun.common.blocks.SteepBlock;
import com.darthun.core.init.BlockInit;
import com.darthun.core.init.ItemInit;
import com.darthun.core.init.TagInit;
import com.darthun.core.init.TileEntityInit;
import com.darthun.scotchmod.ScotchMod;
import com.google.common.collect.Iterables;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IClearable;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class SteepControllerTileEntity extends LockableLootTileEntity implements IClearable, INamedContainerProvider {

    private static final int[] SLOTS = new int[]{9}; // NOT IN USE
    protected NonNullList<ItemStack> items = NonNullList.withSize(9,ItemStack.EMPTY);
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

        world.setBlockState(TOP_LEFT_CORNER,world.getBlockState(TOP_LEFT_CORNER).with(SteepBlock.MACHINECENTER,true)
                .with(SteepBlock.MACHINECORNER,true)
                .with(SteepBlock.FACING, Direction.WEST));
        world.setBlockState(TOP_RIGHT_CORNER,world.getBlockState(TOP_RIGHT_CORNER).with(SteepBlock.MACHINECENTER,true)
                .with(SteepBlock.MACHINECORNER,true)
                .with(SteepBlock.FACING,Direction.NORTH));
        world.setBlockState(BOTTOM_LEFT_CORNER,world.getBlockState(BOTTOM_LEFT_CORNER).with(SteepBlock.MACHINECENTER,true)
                .with(SteepBlock.MACHINECORNER,true)
                .with(SteepBlock.FACING,Direction.SOUTH));
        world.setBlockState(BOTTOM_RIGHT_CORNER,world.getBlockState(BOTTOM_RIGHT_CORNER).with(SteepBlock.MACHINECENTER,true)
                .with(SteepBlock.MACHINECORNER,true)
                .with(SteepBlock.FACING,Direction.EAST));
        world.setBlockState(TOP_ROW_CENTER,world.getBlockState(TOP_ROW_CENTER)
                .with(SteepBlock.MACHINECENTER,true)
                .with(SteepBlock.FACING,Direction.NORTH));
        world.setBlockState(MIDDLE_LEFT_CENTER,world.getBlockState(MIDDLE_LEFT_CENTER)
                .with(SteepBlock.MACHINECENTER,true)
                .with(SteepBlock.FACING,Direction.WEST));
        world.setBlockState(MIDDLE_RIGHT_CENTER,world.getBlockState(MIDDLE_RIGHT_CENTER)
                .with(SteepBlock.MACHINECENTER,true)
                .with(SteepBlock.FACING,Direction.EAST));
        world.setBlockState(BOTTOM_ROW_CENTER,world.getBlockState(BOTTOM_ROW_CENTER)
                .with(SteepBlock.MACHINECENTER,true)
                .with(SteepBlock.FACING,Direction.SOUTH));

    }

    @Override
    public void read(BlockState p_230337_1_, CompoundNBT p_230337_2_) {
        super.read(p_230337_1_, p_230337_2_);
        this.items = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(p_230337_2_,this.items);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        ItemStackHelper.saveAllItems(compound,this.items);
        return compound;
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    public void setItems(NonNullList<ItemStack> nonNullList) {
        this.items = nonNullList;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        this.world.notifyBlockUpdate(this.getPos(),this.getBlockState(),this.getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("container."+ScotchMod.MOD_ID+".steepblockcontroller");
    }

    @Nullable
    @Override
    public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
        return super.createMenu(p_createMenu_1_, p_createMenu_2_, p_createMenu_3_);
    }

    @Override
    public int getSizeInventory() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        for(ItemStack stack : this.items){
            if(!stack.isEmpty()){
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return this.items.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int amount) {
        return ItemStackHelper.getAndSplit(this.items,index,amount);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(this.items,index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        ItemStack itemStack = this.items.get(index);
        boolean flag = !stack.isEmpty() && stack.isItemEqual(itemStack) && ItemStack.areItemStackTagsEqual(stack,itemStack);
        this.items.set(index,stack);
        if(stack.getCount() > this.getInventoryStackLimit()){
            stack.setCount(this.getInventoryStackLimit());
        }
        if(!flag){
            this.markDirty();
        }
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        if(this.world.getTileEntity(pos) != this){
            return false;
        } else {
            return player.getDistanceSq((double)this.pos.getX()+0.5D,(double)this.pos.getY()+0.5D,(double)this.pos.getZ()+0.5D) <= 64.0D;
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return stack.getItem().isIn(TagInit.BARLEY);
    }

    @Override
    public void clear() {
        super.clear();
        this.items.clear();
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        this.write(nbt);
        return new SUpdateTileEntityPacket(this.getPos(),1,nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.read(null,pkt.getNbtCompound());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        this.read(state,tag);
    }
}
