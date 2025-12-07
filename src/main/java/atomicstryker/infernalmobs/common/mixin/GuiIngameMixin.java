package atomicstryker.infernalmobs.common.mixin;

import atomicstryker.infernalmobs.client.InfernalMobsClient;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class GuiIngameMixin {
    @Inject(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/GuiIngame;renderBossHealth()V"))
    private void onPreRenderGameOverlayBossHealth(float partialTicks, boolean par2, int par3, int par4, CallbackInfo ci) {
        InfernalMobsClient.getInstance().onPreRenderGameOverlay(partialTicks);
    }

    @Inject(method = "func_110327_a", at = @At(value = "INVOKE", target = "Lnet/minecraft/Profiler;endStartSection(Ljava/lang/String;)V", ordinal = 3))
    private void onPreRenderGameOverlayAir(int par1, int par2, CallbackInfo ci) {
        InfernalMobsClient.getInstance().onRenderTick();
    }
}
