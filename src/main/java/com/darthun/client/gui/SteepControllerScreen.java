package com.darthun.client.gui;

import com.darthun.common.container.SteepControllerContainer;
import com.darthun.scotchmod.ScotchMod;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class SteepControllerScreen extends ContainerScreen<SteepControllerContainer> {
    public SteepControllerScreen(SteepControllerContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.guiLeft = 0;
        this.guiTop = 0;
        this.xSize = 176;
        this.ySize = 166;
    }

    private static ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(ScotchMod.MOD_ID, "textures/guis/barleysteepgui.png");

    @Override
    public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        this.renderBackground(p_230430_1_);
        super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
        this.renderHoveredTooltip(p_230430_1_, p_230430_2_, p_230430_3_);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack p_230451_1_, int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(p_230451_1_, mouseX, mouseY);
        //this.font.drawString(p_230451_1_, this.title.getString(), 8.0f, 8.0f, 4210752);
        this.font.drawString(p_230451_1_, this.playerInventory.getDisplayName().getString(), 8.0f, (float) (this.ySize - 96 + 2), 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialtick, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.blit(matrixStack, x, y, 0, 0, this.xSize, this.ySize);
    }
}
