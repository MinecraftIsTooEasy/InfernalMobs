package atomicstryker.infernalmobs.common.mixin;

import atomicstryker.infernalmobs.common.SaveEventHandler;
import net.minecraft.Chunk;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Chunk.class)
public class ChunkMixin {
    @Inject(method = "onChunkUnload", at = @At("TAIL"))
    private void onChunkUnload(CallbackInfo ci) {
        SaveEventHandler.instance.onChunkUnload(ReflectHelper.dyCast(this));
    }

    @Inject(method = "onChunkLoad", at = @At("TAIL"))
    private void onChunkLoad(CallbackInfo ci) {
        SaveEventHandler.instance.onChunkLoad(ReflectHelper.dyCast(this));
    }
}
