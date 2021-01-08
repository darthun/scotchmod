package com.darthun.core.init;

import com.darthun.scotchmod.ScotchMod;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ScotchMod.MOD_ID);

    public static final RegistryObject<Item> GLENDARTHUN = ITEMS.register("glendarthun", ()-> new Item(new Item.Properties().group(ItemGroup.MISC)));
    public static final RegistryObject<BlockItem> EXAMPLE_BLOCK = ITEMS.register("example_block", ()-> new BlockItem(BlockInit.EXAMPLE_BLOCK.get(), new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)) );
}
