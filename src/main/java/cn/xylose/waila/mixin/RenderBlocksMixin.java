package cn.xylose.waila.mixin;

import btw.block.blocks.LooseSparseGrassBlock;
import btw.block.blocks.LooseSparseGrassSlabBlock;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderBlocks.class)
public abstract class RenderBlocksMixin {

    @Shadow public abstract void renderBlockAsItemVanilla(Block par1Block, int par2, float par3);

    @Inject(method = "renderBlockAsItem", at = @At("HEAD"), cancellable = true)
    public void fixGrassRender(Block block, int iItemDamage, float fBrightness, CallbackInfo ci) {
        if (block instanceof LooseSparseGrassBlock || block instanceof LooseSparseGrassSlabBlock) {
            this.renderBlockAsItemVanilla(block, iItemDamage, fBrightness);
            ci.cancel();
        }
    }
}
