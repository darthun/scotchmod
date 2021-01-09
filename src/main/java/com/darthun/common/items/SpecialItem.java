package com.darthun.common.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.gen.layer.EdgeLayer;

import javax.annotation.Nullable;
import javax.swing.*;
import java.util.List;

public class SpecialItem extends Item {
    public SpecialItem(Properties properties){
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity playerEntity, Hand hand) {
        playerEntity.addPotionEffect(new EffectInstance(Effects.SLOW_FALLING,200,5));
        return ActionResult.resultSuccess(playerEntity.getHeldItem(hand));
    }

    @Override
    public int getBurnTime(ItemStack itemStack) {
        return 1600;
    }
}
