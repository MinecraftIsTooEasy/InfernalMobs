package atomicstryker.infernalmobs.common;

import net.minecraft.*;

public class LegacyHelper {

    public static LegacyHelper helperInstance;

    /**
     * Returns the closest vulnerable player to this entity within the given radius, or null if none is found
     */
    public EntityPlayer getClosestVulnerablePlayerToEntity(World instance, Entity entityIn, double distance) {
        return this.getClosestVulnerablePlayer(instance, entityIn.posX, entityIn.posY, entityIn.posZ, distance);
    }

    /**
     * Returns the closest vulnerable player within the given radius, or null if none is found.
     */
    public EntityPlayer getClosestVulnerablePlayer(World instance, double p_72846_1_, double p_72846_3_, double p_72846_5_, double p_72846_7_) {
        double d4 = -1.0D;
        EntityPlayer entityplayer = null;

        for (int i = 0; i < instance.playerEntities.size(); ++i) {
            EntityPlayer entityplayer1 = (EntityPlayer) instance.playerEntities.get(i);

            if (!entityplayer1.capabilities.disableDamage && entityplayer1.isEntityAlive()) {
                double d5 = entityplayer1.getDistanceSq(p_72846_1_, p_72846_3_, p_72846_5_);
                double d6 = p_72846_7_;

                if (entityplayer1.isSneaking()) {
                    d6 = p_72846_7_ * 0.800000011920929D;
                }

                if (entityplayer1.isInvisible()) {
                    float f = entityplayer1.getArmorVisibility();

                    if (f < 0.1F) {
                        f = 0.1F;
                    }

                    d6 *= (double) (0.7F * f);
                }

                if ((p_72846_7_ < 0.0D || d5 < d6 * d6) && (d4 == -1.0D || d5 < d4)) {
                    d4 = d5;
                    entityplayer = entityplayer1;
                }
            }
        }

        return entityplayer;
    }

    /**
     * returns true if the entity provided in the argument can be seen. (Raytrace)
     */
    public boolean canEntityBeSeen(EntityLivingBase instance, Entity p_70685_1_) {
        Raycast raycast = new Raycast(
                instance.worldObj,
                Vec3.createVectorHelper(instance.posX, instance.posY + (double) instance.getEyeHeight(), instance.posZ),
                Vec3.createVectorHelper(p_70685_1_.posX, p_70685_1_.posY + (double) p_70685_1_.getEyeHeight(), p_70685_1_.posZ)
        );
        raycast.setPolicies(RaycastPolicies.for_physical_reach);
        return instance.worldObj.tryRaycastVsBlocks(raycast) == null;
    }

    /**
     * Damages armor in each slot by the specified amount.
     */
    public void damageArmor(InventoryPlayer instance, float p_70449_1_) {
        p_70449_1_ /= 4.0F;

        if (p_70449_1_ < 1.0F) {
            p_70449_1_ = 1.0F;
        }

        for (int i = 0; i < instance.armorInventory.length; ++i) {
            if (instance.armorInventory[i] != null && instance.armorInventory[i].getItem() instanceof ItemArmor) {
                instance.armorInventory[i].tryDamageItem(DamageSource.generic, (int) p_70449_1_, instance.player);

                if (instance.armorInventory[i].stackSize == 0) {
                    instance.armorInventory[i] = null;
                }
            }
        }
    }
}
