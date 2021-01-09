package com.darthun.core.init;

import com.darthun.scotchmod.ScotchMod;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockInit {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
            ScotchMod.MOD_ID);
    public static final RegistryObject<Block> EXAMPLE_BLOCK =
            BLOCKS.register("example_block",
                    ()-> new Block(AbstractBlock.Properties.create(Material.IRON, MaterialColor.BLUE)
            .hardnessAndResistance(15f,30f)
            .harvestTool(ToolType.AXE)
            .harvestLevel(0)
            .sound(SoundType.METAL)
                    ));
    public static final RegistryObject<Block> BARLEY =
            BLOCKS.register("barley",
                    ()-> new CropsBlock(AbstractBlock.Properties.create(Material.PLANTS,MaterialColor.GOLD )
                    .doesNotBlockMovement()
                    .tickRandomly()
                    .zeroHardnessAndResistance()
                    .sound(SoundType.CROP)));

    /*
    WHEAT = register("wheat", new CropsBlock(Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().zeroHardnessAndResistance().sound(SoundType.CROP)));
        FAR
    * */
/*    public static final RegistryObject<Block> EXAMPLE_BLOCK2 =
            BLOCKS.register("example_block",
                    ()-> new Block(AbstractBlock.Properties.from(Blocks.BASALT)));
    public static final RegistryObject<Block> EXAMPLE_BLOCK3 =
            BLOCKS.register("example_block",
                    ()-> new Block(AbstractBlock.Properties.from(BlockInit.EXAMPLE_BLOCK2.get())));*/
}
