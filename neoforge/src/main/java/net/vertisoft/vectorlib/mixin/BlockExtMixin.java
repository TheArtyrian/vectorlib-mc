package net.vertisoft.vectorlib.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.extensions.IBlockEntityRendererExtension;
import net.vertisoft.vectorlib.agnostic.neoforge_stitching.VectorIBlockIntf;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(IBlockEntityRendererExtension.class)
@Debug(export = true)
public interface BlockExtMixin
{
    @ModifyReturnValue(method = "getRenderBoundingBox", at = @At("RETURN"))
    default <T extends BlockEntity> AABB vectorLib$stopBoundingBoxCheck(AABB original, @Local(argsOnly = true) T blockEntity)
    {
        if (this instanceof VectorIBlockIntf self) return self.getVectorLibIntfRenderBox(blockEntity);
        return original;
    }
}
