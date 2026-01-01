package atomicstryker.infernalmobs.common.mods;

import net.minecraft.EntityLivingBase;
import net.minecraft.EntityCreeper;
import atomicstryker.infernalmobs.common.InfernalMobsCore;
import atomicstryker.infernalmobs.common.MobModifier;

public class MM_1UP extends MobModifier
{
    public boolean healed;
    
    public MM_1UP(EntityLivingBase mob)
    {
        this.modName = "1UP";
        healed = false;
    }
    
    public MM_1UP(EntityLivingBase mob, MobModifier prevMod)
    {
        this.modName = "1UP";
        this.nextMod = prevMod;
        healed = false;
    }

    @Override
    public boolean onDeath(EntityLivingBase mob)
    {
        if (!healed)
        {

            InfernalMobsCore.instance().setEntityHealthPastMax(mob, mob.getMaxHealth());
            mob.worldObj.playSoundAtEntity(mob, "random.levelup", 1.0F, 1.0F);
            healed = true;
            return true;
        }
        return super.onDeath(mob);
    }
    
    @Override
    public Class<?>[] getBlackListMobClasses()
    {
        return disallowed;
    }
    private static Class<?>[] disallowed = { EntityCreeper.class };
    
    @Override
    protected String[] getModNameSuffix()
    {
        return suffix;
    }
    private static String[] suffix = { "ofRecurrence", "theUndying", "oftwinLives" };
    
    @Override
    protected String[] getModNamePrefix()
    {
        return prefix;
    }
    private static String[] prefix = { "recurring", "undying", "twinlived" };
}
