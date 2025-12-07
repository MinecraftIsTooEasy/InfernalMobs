package atomicstryker.infernalmobs.common.mods;

import net.minecraft.EntityLivingBase;
import net.minecraft.Potion;
import net.minecraft.PotionEffect;
import net.minecraft.DamageSource;
import net.minecraft.EntityDamageSourceIndirect;
import atomicstryker.infernalmobs.common.InfernalMobsCore;
import atomicstryker.infernalmobs.common.MobModifier;

public class MM_Poisonous extends MobModifier
{
    public MM_Poisonous(EntityLivingBase mob)
    {
        this.modName = "Poisonous";
    }
    
    public MM_Poisonous(EntityLivingBase mob, MobModifier prevMod)
    {
        this.modName = "Poisonous";
        this.nextMod = prevMod;
    }
    
    @Override
    public float onHurt(EntityLivingBase mob, DamageSource source, float damage)
    {
        if (source.getResponsibleEntity() != null
        && (source.getResponsibleEntity() instanceof EntityLivingBase)
        && InfernalMobsCore.instance().getIsEntityAllowedTarget(source.getResponsibleEntity()))
        {
            EntityLivingBase ent = (EntityLivingBase)source.getResponsibleEntity();
            if (!ent.isPotionActive(Potion.poison)
            && !(source instanceof EntityDamageSourceIndirect))
            {
                ent.addPotionEffect(new PotionEffect(Potion.poison.id, 120, 0));
            }
        }
        
        return super.onHurt(mob, source, damage);
    }
    
    @Override
    public float onAttack(EntityLivingBase entity, DamageSource source, float damage)
    {
        if (entity != null
        && InfernalMobsCore.instance().getIsEntityAllowedTarget(entity)
        && !entity.isPotionActive(Potion.poison))
        {
            entity.addPotionEffect(new PotionEffect(Potion.poison.id, 120, 0));
        }
        
        return super.onAttack(entity, source, damage);
    }
    
    @Override
    protected String[] getModNameSuffix()
    {
        return suffix;
    }
    private static String[] suffix = { "ofVenom", "thedeadlyChalice" };
    
    @Override
    protected String[] getModNamePrefix()
    {
        return prefix;
    }
    private static String[] prefix = { "poisonous", "stinging", "despoiling" };
    
}
