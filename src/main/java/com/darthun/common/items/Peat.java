package com.darthun.common.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Peat extends Item{
    public Peat(Item.Properties properties){
        super(properties);
    }

    @Override
    public int getBurnTime(ItemStack itemStack) {
        return 1600;
    }
}
