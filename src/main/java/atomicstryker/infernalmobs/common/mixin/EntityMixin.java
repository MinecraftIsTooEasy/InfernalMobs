package atomicstryker.infernalmobs.common.mixin;

import atomicstryker.infernalmobs.common.IEntity;
import net.minecraft.Entity;
import net.minecraft.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin implements IEntity {
    @Unique private NBTTagCompound customEntityData;

    @Inject(method = "writeToNBT", at = @At("HEAD"))
    private void writeCustomEntityData(NBTTagCompound compound, CallbackInfo ci) {
        if (customEntityData != null) {
            compound.setCompoundTag("EntityData", customEntityData);
        }
    }

    @Inject(method = "readFromNBT", at = @At("HEAD"))
    private void readCustomEntityData(NBTTagCompound compound, CallbackInfo ci) {
        if (compound.hasKey("EntityData")) {
            customEntityData = compound.getCompoundTag("EntityData");
        }
    }

    /**
     * Returns a NBTTagCompound that can be used to store custom data for this entity.
     * It will be written, and read from disc, so it persists over world saves.
     * @return A NBTTagCompound
     */
    public NBTTagCompound getEntityData() {
        if (customEntityData == null) {
            customEntityData = new NBTTagCompound();
        }
        return customEntityData;
    }
}
