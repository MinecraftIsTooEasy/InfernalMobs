package atomicstryker.infernalmobs.common.mods;

import net.minecraft.Damage;
import net.minecraft.EntityLivingBase;
import net.minecraft.DamageSource;
import atomicstryker.infernalmobs.common.InfernalMobsCore;
import atomicstryker.infernalmobs.common.MobModifier;

public class MM_Vengeance extends MobModifier
{
    public MM_Vengeance(EntityLivingBase mob)
    {
        this.modName = "Vengeance";
    }

    public MM_Vengeance(EntityLivingBase mob, MobModifier prevMod)
    {
        this.modName = "Vengeance";
        this.nextMod = prevMod;
    }

    @Override
    public float onHurt(EntityLivingBase mob, DamageSource source, float damage)
    {
        if (source.getResponsibleEntity() != null && source.getResponsibleEntity() != mob && !InfernalMobsCore.instance().isInfiniteLoop(mob, source.getResponsibleEntity()))
        {
            source.getResponsibleEntity().attackEntityFrom(new Damage(DamageSource.causeMobDamage(mob),
                    InfernalMobsCore.instance().getLimitedDamage(Math.max(damage / 2, 1))));
        }

        return super.onHurt(mob, source, damage);
    }

    @Override
    protected String[] getModNameSuffix()
    {
        return suffix;
    }

    private static String[] suffix = { "ofRetribution", "theThorned", "ofStrikingBack" };

    @Override
    protected String[] getModNamePrefix()
    {
        return prefix;
    }

    private static String[] prefix = { "thorned", "thorny", "spiky" };

}
