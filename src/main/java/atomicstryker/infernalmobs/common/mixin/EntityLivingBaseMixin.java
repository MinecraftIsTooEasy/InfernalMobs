package atomicstryker.infernalmobs.common.mixin;

import atomicstryker.infernalmobs.common.EntityEventHandler;
import atomicstryker.infernalmobs.common.InfernalMobsCore;
import atomicstryker.infernalmobs.common.MobModifier;
import net.minecraft.*;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin extends Entity {

    public EntityLivingBaseMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "attackEntityFrom", at = @At("HEAD"))
    private void onAttackEntityFrom(Damage damage, CallbackInfoReturnable<EntityDamageResult> cir) {
        EntityEventHandler.instance.onEntityLivingHurt(ReflectHelper.dyCast(this), damage.getSource(), damage.getAmount());
    }

    @Inject(method = "setRevengeTarget", at = @At("HEAD"))
    private void onSetAttackTarget(EntityLivingBase target, CallbackInfo ci) {
        EntityEventHandler.instance.onEntityLivingSetAttackTarget(ReflectHelper.dyCast(this));
    }

    @Inject(method = "fall", at = @At("HEAD"))
    private void onFall(float distance, CallbackInfo ci) {
        EntityEventHandler.instance.onEntityLivingFall(ReflectHelper.dyCast(this), distance);
    }

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void onUpdate(CallbackInfo ci) {
        EntityEventHandler.instance.onEntityLivingUpdate(ReflectHelper.dyCast(this));
    }

    @Inject(method = "jump", at = @At("TAIL"))
    private void onJump(CallbackInfo ci) {
        EntityEventHandler.instance.onEntityLivingJump(ReflectHelper.dyCast(this));
    }

    @Inject(method = "onDeath", at = @At("HEAD"), cancellable = true)
    private void onDeath(CallbackInfo ci) {
        if (!this.worldObj.isRemote)
        {
            MobModifier mod = InfernalMobsCore.getMobModifiers(ReflectHelper.dyCast(this));
            if (mod != null)
            {
                if (mod.onDeath(ReflectHelper.dyCast(this)))
                {
                    ci.cancel();
                }
            }
        }
    }
}
