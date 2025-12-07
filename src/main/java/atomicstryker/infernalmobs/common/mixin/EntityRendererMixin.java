package atomicstryker.infernalmobs.common.mixin;

import atomicstryker.infernalmobs.client.RendererBossGlow;
import net.minecraft.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/Profiler;endStartSection(Ljava/lang/String;)V", ordinal = 19))
    private void inject(float par1, long par2, CallbackInfo ci) {
        RendererBossGlow.onRenderWorldLast(par1);
    }
}
