package atomicstryker.infernalmobs.common.mods;

import net.minecraft.EntityLivingBase;
import atomicstryker.infernalmobs.common.InfernalMobsCore;
import atomicstryker.infernalmobs.common.MobModifier;

public class MM_Regen extends MobModifier
{
    public MM_Regen(EntityLivingBase mob)
    {
        this.modName = "Regen";
    }
    
    public MM_Regen(EntityLivingBase mob, MobModifier prevMod)
    {
        this.modName = "Regen";
        this.nextMod = prevMod;
    }
    
    private long nextAbilityUse = 0L;
    private final static long coolDown = 500L;
    
    @Override
    public boolean onUpdate(EntityLivingBase mob)
    {
        if (mob.getHealth() < mob.getMaxHealth())
        {
            long time = System.currentTimeMillis();
            if (time > nextAbilityUse)
            {
                nextAbilityUse = time+coolDown;
//                InfernalMobsCore.instance().setEntityHealthPastMax(mob, mob.getHealth()+1);
                mob.setHealth(mob.getHealth()+1);
            }
        }
        return super.onUpdate(mob);
    }
    
    @Override
    protected String[] getModNameSuffix()
    {
        return suffix;
    }
    private static String[] suffix = { "ofWTFIMBA", "theCancerous", "ofFirstAid" };
    
    @Override
    protected String[] getModNamePrefix()
    {
        return prefix;
    }
    private static String[] prefix = { "regenerating", "healing", "nighunkillable" };
}
