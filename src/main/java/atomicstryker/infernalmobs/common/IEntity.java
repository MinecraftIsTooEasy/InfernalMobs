package atomicstryker.infernalmobs.common;

import net.minecraft.NBTTagCompound;

public interface IEntity {
    default NBTTagCompound getEntityData() {
        return null;
    }
}
