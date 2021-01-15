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
import net.minecraft.tileentity.ITickableTileEntity;
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

public class SteepControllerTileEntity extends TileEntity implements ITickableTileEntity {

    public static final String INPUT = "input";
    public static final String FUEL = "fuel";
    public static final String OUTPUT = "output";
    public static final String MULTIPROCESS_UPGRADES = "multiprocess_upgrades";
    public static final String COOK_PROGRESS = "cook_progress";
    public static final String BURN_TIME = "burn_time";
    public static final String BURN_VALUE = "burn_value";

    public final InputItemHandler input = new InputItemHandler(this);


    public final LazyOptional<IItemHandler> inputOptional = LazyOptional.of(() -> this.input);


    public int burnTimeRemaining = 0;
    public int lastItemBurnedValue = 200;
    public int cookProgress = 0;
    public boolean isRoomToCook = true;
    public boolean canConsumeFuel = false;
    public ClaimableRecipeWrapper cachedRecipes = this.input.getFreshRecipeInput();
    public boolean needsRecipeUpdate = false;
    public boolean needsOutputUpdate = false;
    public boolean needsFuelUpdate = false;


    public SteepControllerTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }
    public SteepControllerTileEntity(){
        this(TileEntityInit.STEEPCONTROLLERTILEENTITY.get());
    }

    @Override
    public void invalidateCaps()
    {
        this.inputOptional.invalidate();
    }

    @Override
    public void read(BlockState state, CompoundNBT compound)
    {
        //TODO : remove cooking
        super.read(state, compound);
        this.input.deserializeNBT(compound.getCompound(INPUT));
        this.cookProgress = compound.getInt(COOK_PROGRESS);
        this.burnTimeRemaining = compound.getInt(BURN_TIME);
        this.lastItemBurnedValue = compound.getInt(BURN_VALUE);
        this.onInputInventoryChanged();
    }


    @Override
    public CompoundNBT write(CompoundNBT compound)
    {
        //TODO : remove cooking
        super.write(compound);
        compound.put(INPUT, this.input.serializeNBT());
        compound.putInt(COOK_PROGRESS, this.cookProgress);
        compound.putInt(BURN_TIME, this.burnTimeRemaining);
        compound.putInt(BURN_VALUE, this.lastItemBurnedValue);
        return compound;
    }

    public int getBurnConsumption()
    {
        return Math.max(1, this.cachedRecipes.getRecipeCount());
    }

    public boolean isBurning()
    {
        return this.burnTimeRemaining > 0;
    }

    public void updateBurningBlockstates(boolean burning)
    {
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            BlockPos adjacentPos = this.pos.offset(direction);
            BlockState state = this.world.getBlockState(adjacentPos);
            if (state.getBlock() instanceof JumboFurnaceBlock)
            {
                this.world.setBlockState(adjacentPos, state.with(JumboFurnaceBlock.LIT, burning));
            }
        }
    }

    public void markInputInventoryChanged()
    {
        this.markDirty();
        this.onInputInventoryChanged();
    }

    public void onInputInventoryChanged()
    {
        this.needsRecipeUpdate = true;
    }

    /** Called at the start of a tick when the input inventory has changed **/
    public void updateRecipes()
    {
        ClaimableRecipeWrapper wrapper = this.input.getFreshRecipeInput();
        // get all recipes allowed by furnace or jumbo furnace
        // sort them by specificity (can we do this on recipe reload?)
        // recipes requiring multiple ingredients = most important, ingredients with more matching items (tags) = less important
        List<JumboFurnaceRecipe> recipes = RecipeSorter.INSTANCE.getSortedFurnaceRecipes(this.world.getRecipeManager());
        // start assigning input slots to usable recipes as they are found
        for (JumboFurnaceRecipe recipe : recipes)
        {
            // loop recipe over inputs until it can't match or we have no unused inputs left
            while (wrapper.getRecipeCount() < this.getMaxSimultaneousRecipes() && wrapper.matchAndClaimInputs(recipe, this.world) && wrapper.hasUnusedInputsLeft());
        }
        // when all input slots are claimed or the recipe list is exhausted, set the new recipe cache
        this.cachedRecipes = wrapper;
        this.needsRecipeUpdate = false;
        this.needsOutputUpdate = true;
    }


    /** Called at the start of a tick when the output inventory has changed, or if the recipe cache has updated**/
    public void updateOutput()
    {
        this.isRoomToCook = this.checkIfRoomToCook();
        this.needsOutputUpdate = false;
    }

    public boolean checkIfRoomToCook()
    {
        return true;
    }

    @Override
    public void tick()
    {
        // if burning, decrement burn time
        boolean dirty = false;
        boolean wasBurningBeforeTick = this.isBurning();
        if (wasBurningBeforeTick)
        {
            this.burnTimeRemaining -= this.getBurnConsumption();
            dirty = true;
        }

        if (!this.world.isRemote)
        {
            // reinform self of own state if inventories have changed
            if (this.needsRecipeUpdate)
            {
                this.updateRecipes();
            }


            boolean hasSmeltableInputs = this.cachedRecipes.getRecipeCount() > 0;

            // if burning, or if it can consume fuel and has a smeltable input
            if (this.isBurning() || (this.canConsumeFuel && hasSmeltableInputs))
            {
                // if not burning but can start cooking
                // this also implies that we can consume fuel
                if (!this.isBurning() && hasSmeltableInputs)
                {
                    // consume fuel and start burning
                    this.consumeFuel();
                }

                // if burning and has smeltable inputs
                if (this.isBurning() && hasSmeltableInputs)
                {
                    // increase cook progress
                    this.cookProgress++;

                    // if cook progress is complete, reset cook progress and do crafting
                    if (this.cookProgress >= JumboFurnace.SERVER_CONFIG.jumboFurnaceCookTime.get())
                    {
                        this.cookProgress = 0;
                        this.craft();
                    }
                    dirty = true;
                }
                else // otherwise, reset cook progress
                {
                    this.cookProgress = 0;
                    dirty = true;
                }
            }
            // otherwise, if not burning but has cookprogress, reduce cook progress
            else if (!this.isBurning() && this.cookProgress > 0)
            {
                if (hasSmeltableInputs)
                {
                    this.cookProgress = Math.max(0, this.cookProgress - 2);
                }
                else
                {
                    this.cookProgress = 0;
                }
                dirty = true;
            }

            boolean isBurningAfterTick = this.isBurning();

            // if burning state changed since tick started, update furnace blockstates
            if (isBurningAfterTick != wasBurningBeforeTick)
            {
                this.updateBurningBlockstates(isBurningAfterTick);
            }

            if (dirty)
            {
                this.markDirty();

                BlockState state = this.world.getBlockState(pos);
                this.world.notifyNeighborsOfStateChange(pos, state.getBlock());


            }


        }
    }

    public void consumeFuel()
    {

    }

    public void craft()
    {
            result = this.output.insertCraftResult(slot, result, false);
            this.input.setStackInSlot(slot, unusedInputs.getStackInSlot(slot));
    }

    @Override
    public void markDirty()
    {
        super.markDirty();
    }

    //region  SteepController Assemble Logic
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

}
