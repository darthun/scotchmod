package com.darthun.core.init;

import com.darthun.common.blocks.SteepController;
import com.darthun.common.container.SteepControllerContainer;
import com.darthun.scotchmod.ScotchMod;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ContainerInit {
    public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES =  DeferredRegister.create(ForgeRegistries.CONTAINERS,ScotchMod.MOD_ID);
    public static final RegistryObject<ContainerType<SteepControllerContainer>> STEEPCONTROLLER = CONTAINER_TYPES.register("steepcontroller",
            ()-> IForgeContainerType.create(SteepControllerContainer::new));
}
