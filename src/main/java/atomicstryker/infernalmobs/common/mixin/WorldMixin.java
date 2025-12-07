package atomicstryker.infernalmobs.common.mixin;

import atomicstryker.infernalmobs.client.InfernalMobsClient;
import atomicstryker.infernalmobs.common.EntityEventHandler;
import net.minecraft.*;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class WorldMixin {
    @Inject(method = "spawnEntityInWorld", at = @At("HEAD"))
    private void onEntityJoinedWorld(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        InfernalMobsClient.getInstance().onEntityJoinedWorld(ReflectHelper.dyCast(this), entity);
        EntityEventHandler.instance.onEntityJoinedWorld(entity);
    }
}
