package com.darthun.common.tiles;

import com.darthun.common.blocks.SteepBlock;
import com.darthun.common.blocks.SteepController;
import com.darthun.common.container.SteepControllerContainer;
import com.darthun.core.init.*;
import com.darthun.scotchmod.ScotchMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IClearable;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SteepControllerTileEntity extends LockableLootTileEntity implements IClearable, INamedContainerProvider {

    private static final int[] SLOTS = new int[]{9}; // NOT IN USE
    protected NonNullList<ItemStack> machinecontents = NonNullList.withSize(9,ItemStack.EMPTY);
    protected int numPlayersUsing;
    private IItemHandlerModifiable items = createHandler();
    private LazyOptional<IItemHandlerModifiable> itemHandler = LazyOptional.of(() -> items);

    public SteepControllerTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }
    public SteepControllerTileEntity(){
        this(TileEntityInit.STEEPCONTROLLERTILEENTITY.get());
    }
//region  SteepController TileEntity
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
//endregion

    //region TurtyWurty
    @Override
    public void read(BlockState p_230337_1_, CompoundNBT p_230337_2_) {
        super.read(p_230337_1_, p_230337_2_);
        this.machinecontents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(p_230337_2_,this.machinecontents);
    }


    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        if(!this.checkLootAndWrite(compound)){
            ItemStackHelper.saveAllItems(compound,this.machinecontents);
        }
        return compound;
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return this.machinecontents;
    }

    @Override
    public void setItems(NonNullList<ItemStack> nonNullList) {
        this.machinecontents = nonNullList;
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

    @Override
    protected Container createMenu(int i, PlayerInventory playerInventory) {
        return new SteepControllerContainer(i,playerInventory,this);
    }

    @Override
    public int getSizeInventory() {
        return 9;
    }

    @Override
    public boolean isEmpty() {
        for(ItemStack stack : this.machinecontents){
            if(!stack.isEmpty()){
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return this.machinecontents.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int amount) {
        System.out.println("decrStackSize was called darthundebug");
        return ItemStackHelper.getAndSplit(this.machinecontents,index,amount);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        System.out.println("removeStackFromSlot was called darthundebug");
        return ItemStackHelper.getAndRemove(this.machinecontents,index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        ItemStack itemStack = this.machinecontents.get(index);
        boolean flag = !stack.isEmpty() && stack.isItemEqual(itemStack) && ItemStack.areItemStackTagsEqual(stack,itemStack);
        this.machinecontents.set(index,stack);
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
        System.out.println("isValidForSlot called darthundebug");
        System.out.println("isValidForSlot tostring:");
        System.out.println(stack.getItem().getTags().toString());
        System.out.println("isValidForSlot getname:");
        System.out.println(TagInit.BARLEY.getName());
        System.out.println("isValidForSlot returns:");
        System.out.println(stack.getItem().isIn(TagInit.BARLEY));
        System.out.println("endebug darthundebug");
        return stack.getItem().isIn(TagInit.BARLEY);
    }

    @Override
    public void clear() {
        super.clear();
        this.machinecontents.clear();
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

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == 1) {
            this.numPlayersUsing = type;
            return true;
        } else {
            return super.receiveClientEvent(id, type);
        }
    }

    @Override
    public void openInventory(PlayerEntity player) {
        if (!player.isSpectator()) {
            if (this.numPlayersUsing < 0) {
                this.numPlayersUsing = 0;
            }

            ++this.numPlayersUsing;
            this.onOpenOrClose();
        }
    }

    @Override
    public void closeInventory(PlayerEntity player) {
        if (!player.isSpectator()) {
            --this.numPlayersUsing;
            this.onOpenOrClose();
        }
    }

    protected void onOpenOrClose() {
        Block block = this.getBlockState().getBlock();
        if (block instanceof SteepController) {
            this.world.addBlockEvent(this.pos, block, 1, this.numPlayersUsing);
            this.world.notifyNeighborsOfStateChange(this.pos, block);
        }
    }


    public static int getPlayersUsing(IBlockReader reader, BlockPos pos) {
        BlockState blockstate = reader.getBlockState(pos);
        if (blockstate.hasTileEntity()) {
            TileEntity tileentity = reader.getTileEntity(pos);
            if (tileentity instanceof SteepControllerTileEntity) {
                return ((SteepControllerTileEntity) tileentity).numPlayersUsing;
            }
        }
        return 0;
    }


/*    public static void swapContents(ExampleChestTileEntity te, ExampleChestTileEntity otherTe) {
        NonNullList<ItemStack> list = te.getItems();
        te.setItems(otherTe.getItems());
        otherTe.setItems(list);
    }*/

    @Override
    public void updateContainingBlockInfo() {
        super.updateContainingBlockInfo();
        if (this.itemHandler != null) {
            this.itemHandler.invalidate();
            this.itemHandler = null;
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nonnull Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    private IItemHandlerModifiable createHandler() {
        return new InvWrapper(this);
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();
        itemHandler.invalidate();
    }

    @Override
    public void remove() {
        super.remove();
        if(itemHandler != null) {
            itemHandler.invalidate();
        }
    }
    //endregion
}
