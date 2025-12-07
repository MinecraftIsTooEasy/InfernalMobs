package atomicstryker.infernalmobs.common.mods;

import atomicstryker.infernalmobs.common.LegacyHelper;
import net.minecraft.EntityLivingBase;
import net.minecraft.EntityPlayer;
import net.minecraft.DamageSource;
import net.minecraft.EntityDamageSourceIndirect;
import atomicstryker.infernalmobs.common.MobModifier;

public class MM_Rust extends MobModifier
{
    public MM_Rust(EntityLivingBase mob)
    {
        this.modName = "Rust";
    }
    
    public MM_Rust(EntityLivingBase mob, MobModifier prevMod)
    {
        this.modName = "Rust";
        this.nextMod = prevMod;
    }
    
    @Override
    public float onHurt(EntityLivingBase mob, DamageSource source, float damage)
    {
        if (source.getResponsibleEntity() != null
        && (source.getResponsibleEntity() instanceof EntityPlayer)
        && !(source instanceof EntityDamageSourceIndirect))
        {
            EntityPlayer p = (EntityPlayer)source.getResponsibleEntity();
            if (p.inventory.getCurrentItemStack() != null)
            {
                p.inventory.getCurrentItemStack().tryDamageItem(DamageSource.generic, 4, (EntityLivingBase) source.getResponsibleEntity());
            }
        }
        
        return super.onHurt(mob, source, damage);
    }
    
    @Override
    public float onAttack(EntityLivingBase entity, DamageSource source, float damage)
    {
        if (entity != null
        && entity instanceof EntityPlayer)
        {
            LegacyHelper.helperInstance.damageArmor(((EntityPlayer)entity).inventory, damage*3);
        }
        
        return super.onAttack(entity, source, damage);
    }
    
    @Override
    protected String[] getModNameSuffix()
    {
        return suffix;
    }
    private static String[] suffix = { "ofDecay", "theEquipmentHaunter" };
    
    @Override
    protected String[] getModNamePrefix()
    {
        return prefix;
    }
    private static String[] prefix = { "rusting", "decaying" };
    
}
