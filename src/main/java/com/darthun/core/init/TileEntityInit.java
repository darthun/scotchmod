package com.darthun.core.init;

import com.darthun.common.tiles.BogEarthTileEntity;
import com.darthun.scotchmod.ScotchMod;
import com.google.common.collect.Sets;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TileEntityInit {

    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPE = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES,ScotchMod.MOD_ID);
    public static final RegistryObject<TileEntityType<BogEarthTileEntity>> BOGEARTH = TILE_ENTITY_TYPE.register("bogearth",
            ()-> new TileEntityType<>(BogEarthTileEntity::new, Sets.newHashSet(BlockInit.BOGEARTH.get()),null));
}
