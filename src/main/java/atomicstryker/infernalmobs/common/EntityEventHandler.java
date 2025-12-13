package atomicstryker.infernalmobs.common;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.*;
import net.minecraftforge.common.Configuration;

public class EntityEventHandler
{

    private final boolean antiMobFarm;
    private final long mobFarmCheckIntervals;
    private final float mobFarmDamageTrigger;

    private final HashMap<ChunkCoordIntPair, Float> damageMap;
    private long nextMapEvaluation;
    
    public static EntityEventHandler instance;

    /**
     * Links the Forge Event Handler to the registered Entity MobModifier Events
     * (if present) Also keeps track of the anti mobfarm mechanic if enabled
     * 
     * @param antiMobfarming
     *            enables or disables
     */
    public EntityEventHandler()
    {
        Configuration config = InfernalMobsCore.instance().config;

        config.load();
        antiMobFarm =
                config.get(Configuration.CATEGORY_GENERAL, "AntiMobfarmingEnabled", true,
                        "Anti Mob farming mechanic. Might cause overhead if enabled.").getBoolean(true);
        mobFarmCheckIntervals =
                config.get(Configuration.CATEGORY_GENERAL, "AntiMobFarmCheckInterval", 30,
                        "time in seconds between mob check intervals. Higher values cost more performance, but might be more accurate.").getInt() * 1000l;
        mobFarmDamageTrigger =
                (float) config.get(Configuration.CATEGORY_GENERAL, "mobFarmDamageThreshold", 150D,
                        "Damage in chunk per interval that triggers anti farm effects").getDouble(150D);
        config.save();

        damageMap = new HashMap<ChunkCoordIntPair, Float>();
        nextMapEvaluation = System.currentTimeMillis();
    }
    
    public void onEntityJoinedWorld(Entity entity)
    {
        if (entity instanceof EntityLivingBase)
        {
            String savedMods = ((IEntity) entity).getEntityData().getString(InfernalMobsCore.instance().getNBTTag());
            if (!savedMods.equals(""))
            {
                InfernalMobsCore.instance().addEntityModifiersByString((EntityLivingBase) entity, savedMods);
            }
            else
            {
                InfernalMobsCore.instance().processEntitySpawn((EntityLivingBase) entity);
            }
        }
    }


    public void onEntityLivingDeath(EntityLivingBase entityLiving)
    {
        if (!entityLiving.worldObj.isRemote)
        {
            MobModifier mod = InfernalMobsCore.getMobModifiers(entityLiving);
            if (mod != null)
            {
                if (mod.onDeath())
                {
                    return;
                }
            }
        }
    }


    public void onEntityLivingSetAttackTarget(EntityLivingBase entityLiving)
    {
        if (!entityLiving.worldObj.isRemote)
        {
            MobModifier mod = InfernalMobsCore.getMobModifiers(entityLiving);
            if (mod != null)
            {
                return;
            }
        }
    }


//    public void onEntityLivingAttacked(LivingAttackEvent event)
//    {
//        /* fires both client and server before hurt, but we dont need this */
//    }

    /**
     * Hook into EntityLivingHurt. Is always serverside, assured by mc itself
     */

    public void onEntityLivingHurt(EntityLivingBase entityLiving, DamageSource source, float amount)
    {
        // dont allow masochism
        if (source.getResponsibleEntity() != entityLiving)
        {
            MobModifier mod = InfernalMobsCore.getMobModifiers(entityLiving);
            if (mod != null)
            {
                amount = mod.onHurt(entityLiving, source, amount);
            }

            /*
             * We use the Hook two-sided, both with the Mob as possible target
             * and attacker
             */
            Entity attacker = source.getImmediateEntity();
            if (attacker != null && attacker instanceof EntityLivingBase)
            {
                mod = InfernalMobsCore.getMobModifiers((EntityLivingBase) attacker);
                if (mod != null)
                {
                    amount = mod.onAttack(entityLiving, source, amount);
                }
            }

            if (antiMobFarm)
            {
                /*
                 * check for an environmental/automated damage type, aka mob farms
                 */
                if (source == DamageSource.cactus || source == DamageSource.drown || source == DamageSource.fall
                        || source == DamageSource.inWall || source == DamageSource.lava || source.getResponsibleEntity() instanceof EntityPlayer)
                {
                    ChunkCoordIntPair cpair = new ChunkCoordIntPair((int) entityLiving.posX, (int) entityLiving.posZ);
                    Float value = damageMap.get(cpair);
                    if (value == null)
                    {
                        for (Map.Entry<ChunkCoordIntPair, Float> e : damageMap.entrySet())
                        {
                            if (Math.abs(e.getKey().chunkXPos - cpair.chunkXPos) < 3)
                            {
                                if (Math.abs(e.getKey().chunkZPos - cpair.chunkZPos) < 3)
                                {
                                    e.setValue(e.getValue() + amount);
                                    break;
                                }
                            }
                        }
                    }
                    else
                    {
                        damageMap.put(cpair, value + amount);
                    }
                }
            }
        }
    }


    public void onEntityLivingFall(EntityLivingBase entityLiving, float distance)
    {
        if (!entityLiving.worldObj.isRemote)
        {
            MobModifier mod = InfernalMobsCore.getMobModifiers(entityLiving);
            if (mod != null)
            {
                if (mod.onFall(distance)) return;
            }
        }
    }


    public void onEntityLivingJump(EntityLivingBase entityLiving)
    {
        if (!entityLiving.worldObj.isRemote)
        {
            MobModifier mod = InfernalMobsCore.getMobModifiers(entityLiving);
            if (mod != null)
            {
                mod.onJump(entityLiving);
            }
        }
    }


    public void onEntityLivingUpdate(EntityLivingBase entityLiving)
    {
        if (!entityLiving.worldObj.isRemote)
        {
            MobModifier mod = InfernalMobsCore.getMobModifiers(entityLiving);
            if (mod != null)
            {
                mod.onUpdate(entityLiving);
            }

            if (antiMobFarm && System.currentTimeMillis() > nextMapEvaluation)
            {
                if (!damageMap.isEmpty())
                {
                    float maxDamage = 0f;
                    float val;
                    ChunkCoordIntPair maxC = null;
                    for (Map.Entry<ChunkCoordIntPair, Float> e : damageMap.entrySet())
                    {
                        val = e.getValue();
                        if (val > maxDamage)
                        {
                            maxC = e.getKey();
                            maxDamage = val;
                        }
                    }

                    System.out.println("Infernal Mobs AntiMobFarm damage check, max detected chunk damage value " + maxDamage + " near coords "
                            + maxC.getCenterXPos() + ", " + maxC.getCenterZPosition());
                    if (maxDamage > mobFarmDamageTrigger)
                    {
//                        MinecraftForge.EVENT_BUS.post(new MobFarmDetectedEvent(entityLiving.worldObj.getChunkFromChunkCoords(maxC.chunkXPos,
//                                maxC.chunkZPos), mobFarmCheckIntervals, maxDamage));
                    }
                    damageMap.clear();
                }
                nextMapEvaluation = System.currentTimeMillis() + mobFarmCheckIntervals;
            }
        }
    }

//    public static class MobFarmDetectedEvent extends ChunkEvent
//    {
//        public final long triggeringInterval;
//        public final float triggeringDamage;
//
//        public MobFarmDetectedEvent(Chunk chunk, long ti, float td)
//        {
//            super(chunk);
//            triggeringInterval = ti;
//            triggeringDamage = td;
//        }
//    }

    public void onEntityLivingDrops(EntityLivingBase entityLiving, boolean recentlyHit)
    {
        if (!entityLiving.worldObj.isRemote)
        {
            MobModifier mod = InfernalMobsCore.getMobModifiers(entityLiving);
            if (mod != null)
            {
                mod.onDropItems(entityLiving, recentlyHit);
                InfernalMobsCore.removeEntFromElites(entityLiving);
            }
        }
    }
}
