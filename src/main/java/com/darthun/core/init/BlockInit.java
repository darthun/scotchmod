package com.darthun.core.init;

import com.darthun.scotchmod.ScotchMod;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
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
/*    public static final RegistryObject<Block> EXAMPLE_BLOCK2 =
            BLOCKS.register("example_block",
                    ()-> new Block(AbstractBlock.Properties.from(Blocks.BASALT)));
    public static final RegistryObject<Block> EXAMPLE_BLOCK3 =
            BLOCKS.register("example_block",
                    ()-> new Block(AbstractBlock.Properties.from(BlockInit.EXAMPLE_BLOCK2.get())));*/
}
