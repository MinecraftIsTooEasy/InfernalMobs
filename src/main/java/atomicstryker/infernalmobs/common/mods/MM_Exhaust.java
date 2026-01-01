package atomicstryker.infernalmobs.common.mods;

import net.minecraft.EntityLivingBase;
import net.minecraft.EntityPlayer;
import net.minecraft.DamageSource;
import atomicstryker.infernalmobs.common.MobModifier;

public class MM_Exhaust extends MobModifier
{
    public MM_Exhaust(EntityLivingBase mob)
    {
        this.modName = "Exhaust";
    }
    
    public MM_Exhaust(EntityLivingBase mob, MobModifier prevMod)
    {
        this.modName = "Exhaust";
        this.nextMod = prevMod;
    }
    
    @Override
    public float onHurt(EntityLivingBase mob, DamageSource source, float damage)
    {
        if (source.getImmediateEntity() != null
        && (source.getImmediateEntity() instanceof EntityPlayer player))
        {
            player.getFoodStats().setNutrition(-1, true);
            player.getFoodStats().setSatiation(-1, true);
        }
        
        return super.onHurt(mob, source, damage);
    }
    
    @Override
    public float onAttack(EntityLivingBase entity, DamageSource source, float damage)
    {
        if (source.getImmediateEntity() != null
        && (source.getImmediateEntity() instanceof EntityPlayer player))
        {
            player.getFoodStats().setNutrition(-1, true);
            player.getFoodStats().setSatiation(-1, true);
        }
        
        return super.onAttack(entity, source, damage);
    }
    
    @Override
    protected String[] getModNameSuffix()
    {
        return suffix;
    }
    private static String[] suffix = { "ofFatigue", "theDrainer" };
    
    @Override
    protected String[] getModNamePrefix()
    {
        return prefix;
    }
    private static String[] prefix = { "exhausting", "draining" };
    
}
