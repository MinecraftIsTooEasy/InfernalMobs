package atomicstryker.infernalmobs.client;

import java.util.Map;

import net.minecraft.*;
import atomicstryker.infernalmobs.common.InfernalMobsCore;
import atomicstryker.infernalmobs.common.MobModifier;

public class RendererBossGlow
{
    private static long lastRender = 0L;
    
    public static void onRenderWorldLast(float partialTicks)
    {
        if (System.currentTimeMillis() > lastRender+10L)
        {
            lastRender = System.currentTimeMillis();
            
            renderBossGlow(partialTicks);
        }
    }
    
    private static void renderBossGlow(float renderTick)
    {
        Minecraft mc = Minecraft.getMinecraft();
        EntityLivingBase viewEnt = mc.renderViewEntity;
        Vec3 curPos = viewEnt.getPosition(renderTick);

        if (mc.isGamePaused) return;
        
        Frustrum f = new Frustrum();
        double var7 = viewEnt.lastTickPosX + (viewEnt.posX - viewEnt.lastTickPosX) * (double)renderTick;
        double var9 = viewEnt.lastTickPosY + (viewEnt.posY - viewEnt.lastTickPosY) * (double)renderTick;
        double var11 = viewEnt.lastTickPosZ + (viewEnt.posZ - viewEnt.lastTickPosZ) * (double)renderTick;
        f.setPosition(var7, var9, var11);
        
        Map<EntityLivingBase, MobModifier> mobsmap = InfernalMobsCore.proxy.getRareMobs();
        for (EntityLivingBase ent : mobsmap.keySet())
        {
            if (ent.isInRangeToRenderDist(curPos.distanceTo(ent.getPosition(1.0f)))
            && (ent.ignoreFrustumCheck || f.isBoundingBoxInFrustum(ent.boundingBox))
            && ent.isEntityAlive())
            {
                mc.renderGlobal.spawnParticle(EnumParticle.mobSpell,
                        ent.posX + (ent.worldObj.rand.nextDouble() - 0.5D) * (double)ent.width,
                        ent.posY + ent.worldObj.rand.nextDouble() * (double)ent.height - 0.25D,
                        ent.posZ + (ent.worldObj.rand.nextDouble() - 0.5D) * (double)ent.width,
                        (ent.worldObj.rand.nextDouble() - 0.5D) * 2.0D,
                        -ent.worldObj.rand.nextDouble(),
                        (ent.worldObj.rand.nextDouble() - 0.5D) * 2.0D);
            }
        }
    }
}
