package atomicstryker.infernalmobs.client;

import java.util.concurrent.ConcurrentHashMap;

import atomicstryker.infernalmobs.common.network.NetworkHelper;
import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.listener.IInitializationListener;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.*;

import org.lwjgl.opengl.GL11;

import atomicstryker.infernalmobs.common.ISidedProxy;
import atomicstryker.infernalmobs.common.InfernalMobsCore;
import atomicstryker.infernalmobs.common.MobModifier;
import atomicstryker.infernalmobs.common.mods.MM_Gravity;
import atomicstryker.infernalmobs.common.network.HealthPacket;
import atomicstryker.infernalmobs.common.network.MobModsPacket;

public class InfernalMobsClient implements ISidedProxy, ClientModInitializer
{
    private final double NAME_VISION_DISTANCE = 32D;
    private World lastWorld;
    private long nextPacketTime;
    private ConcurrentHashMap<EntityLivingBase, MobModifier> rareMobsClient;
    private int airOverrideValue = -999;
    
    private long healthBarRetainTime;
    private EntityLivingBase retainedTarget;

    public static InfernalMobsClient instance;
    
    @Override
    public void preInit()
    {

    }

    @Override
    public void onInitializeClient() {
        instance = new InfernalMobsClient();
    }

    @Override
    public void load()
    {
        nextPacketTime = 0;
        rareMobsClient = new ConcurrentHashMap<EntityLivingBase, MobModifier>();
        
        healthBarRetainTime = 0;
        retainedTarget = null;
    }

    public static InfernalMobsClient getInstance() {
        return instance;
    }
    
    public void onEntityJoinedWorld(World world, Entity entity) {
        if (Minecraft.getMinecraft() == null) return;

        if (world.isRemote && Minecraft.getMinecraft().thePlayer != null
                && (entity instanceof EntityMob || (entity instanceof EntityLivingBase && entity instanceof IMob))) {
            NetworkHelper.sendToServer(new MobModsPacket(Minecraft.getMinecraft().thePlayer.getEntityName(), entity
                    .entityId, (byte) 0));
        }
    }

    private void askServerHealth(Entity ent)
    {
        if (Minecraft.getMinecraft() == null) return;
        if (System.currentTimeMillis() > nextPacketTime)
        {
            NetworkHelper.sendToServer(new HealthPacket(Minecraft.getMinecraft().thePlayer.getEntityName(), ent.entityId, 0f, 0f));
            nextPacketTime = System.currentTimeMillis() + 100l;
        }
    }

    public void onPreRenderGameOverlay(float partialTicks)
    {
        if (Minecraft.getMinecraft() == null) return;
        if (InfernalMobsCore.instance().getIsHealthBarDisabled() || 
                (BossStatus.bossName != null && BossStatus.statusBarLength > 0))
        {
            return;
        }

        Entity ent = getEntityCrosshairOver(partialTicks, Minecraft.getMinecraft());
        boolean retained = false;
        
        if (ent == null && System.currentTimeMillis() < healthBarRetainTime)
        {
            ent = retainedTarget;
            retained = true;
        }

        if (ent != null && ent instanceof EntityLivingBase)
        {
            MobModifier mod = InfernalMobsCore.getMobModifiers((EntityLivingBase) ent);
            if (mod != null)
            {
                askServerHealth(ent);

                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.icons);
                GL11.glDisable(GL11.GL_BLEND);

                EntityLivingBase target = (EntityLivingBase) ent;
                String buffer = mod.getEntityDisplayName(target);

                ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft().gameSettings, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
                int screenwidth = resolution.getScaledWidth();
                FontRenderer fontR = Minecraft.getMinecraft().fontRenderer;

                GuiIngame gui = Minecraft.getMinecraft().ingameGUI;
                short lifeBarLength = 182;
                int x = screenwidth / 2 - lifeBarLength / 2;

                int lifeBarLeft = (int) ((float) mod.getActualHealth(target) / (float) mod.getActualMaxHealth(target) * (float) (lifeBarLength + 1));
                byte y = 12;
                gui.drawTexturedModalRect(x, y, 0, 74, lifeBarLength, 5);
                gui.drawTexturedModalRect(x, y, 0, 74, lifeBarLength, 5);

                if (lifeBarLeft > 0)
                {
                    gui.drawTexturedModalRect(x, y, 0, 79, lifeBarLeft, 5);
                }

                int yCoord = 10;
                fontR.drawStringWithShadow(buffer, screenwidth / 2 - fontR.getStringWidth(buffer) / 2, yCoord, 0x2F96EB);

                String[] display = mod.getDisplayNames();
                int i = 0;
                while (i < display.length && display[i] != null)
                {
                    yCoord += 10;
                    fontR.drawStringWithShadow(display[i], screenwidth / 2 - fontR.getStringWidth(display[i]) / 2, yCoord, 0xffffff);
                    i++;
                }

                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.icons);
                
                if (!retained)
                {
                    retainedTarget = target;
                    healthBarRetainTime = System.currentTimeMillis() + 3000L;
                }
                
            }
        }
    }

    private Entity getEntityCrosshairOver(float renderTick, Minecraft mc)
    {
        Entity returnedEntity = null;

        if (mc.renderViewEntity != null)
        {
            if (mc.theWorld != null)
            {
                double reachDistance = NAME_VISION_DISTANCE;
                final RaycastCollision mopos = mc.renderViewEntity.getBlockCollisionAlongLookVector(RaycastPolicies.for_entity_item_pickup, renderTick, reachDistance);
                double reachDist2 = reachDistance;
                final Vec3 viewEntPositionVec = mc.renderViewEntity.getPosition(renderTick);

                if (mopos != null)
                {
                    reachDist2 = mopos.position_hit.distanceTo(viewEntPositionVec);
                }

                final Vec3 viewEntityLookVec = mc.renderViewEntity.getLook(renderTick);
                final Vec3 actualReachVector =
                        viewEntPositionVec.addVector(viewEntityLookVec.xCoord * reachDistance, viewEntityLookVec.yCoord * reachDistance,
                                viewEntityLookVec.zCoord * reachDistance);
                float expandBBvalue = 1.0F;
                double lowestDistance = reachDist2;
                Entity iterEnt;
                Entity pointedEntity = null;
                for (Object obj : mc.theWorld.getEntitiesWithinAABBExcludingEntity(
                        mc.renderViewEntity,
                        mc.renderViewEntity.boundingBox.addCoord(viewEntityLookVec.xCoord * reachDistance, viewEntityLookVec.yCoord * reachDistance,
                                viewEntityLookVec.zCoord * reachDistance).expand((double) expandBBvalue, (double) expandBBvalue,
                                (double) expandBBvalue)))
                {
                    iterEnt = (Entity) obj;
                    if (iterEnt.canBeCollidedWith())
                    {
                        float entBorderSize = iterEnt.getCollisionBorderSize(mc.renderViewEntity);
                        AxisAlignedBB entHitBox = iterEnt.boundingBox.expand((double) entBorderSize, (double) entBorderSize, (double) entBorderSize);
                        AABBIntercept interceptObjectPosition = entHitBox.calculateIntercept(mc.renderViewEntity.worldObj, viewEntPositionVec, actualReachVector);

                        if (entHitBox.isVecInside(viewEntPositionVec))
                        {
                            if (0.0D < lowestDistance || lowestDistance == 0.0D)
                            {
                                pointedEntity = iterEnt;
                                lowestDistance = 0.0D;
                            }
                        }
                        else if (interceptObjectPosition != null)
                        {
                            double distanceToEnt = viewEntPositionVec.distanceTo(interceptObjectPosition.position_hit);

                            if (distanceToEnt < lowestDistance || lowestDistance == 0.0D)
                            {
                                pointedEntity = iterEnt;
                                lowestDistance = distanceToEnt;
                            }
                        }
                    }
                }

                if (pointedEntity != null && (lowestDistance < reachDist2 || mopos == null))
                {
                    returnedEntity = pointedEntity;
                }
            }
        }

        return returnedEntity;
    }
    
    public void onTick()
    {
        if (Minecraft.getMinecraft() == null || Minecraft.getMinecraft().theWorld == null || (Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen.doesGuiPauseGame()))
            return;

        /* client reset in case of swapping worlds */
        if (Minecraft.getMinecraft().theWorld != lastWorld)
        {
            boolean newGame = lastWorld == null;
            lastWorld = Minecraft.getMinecraft().theWorld;

            if (!newGame)
            {
                InfernalMobsCore.proxy.getRareMobs().clear();
            }
        }
    }

    @Override
    public ConcurrentHashMap<EntityLivingBase, MobModifier> getRareMobs()
    {
        return rareMobsClient;
    }

    @Override
    public void onHealthPacketForClient(String stringData, int entID, float health, float maxhealth)
    {
        Entity ent = Minecraft.getMinecraft().theWorld.getEntityByID(entID);
        if (ent != null && ent instanceof EntityLivingBase)
        {
            MobModifier mod = InfernalMobsCore.getMobModifiers((EntityLivingBase) ent);
            if (mod != null)
            {
                //System.out.printf("health packet [%f of %f] for %s\n", health, maxhealth, ent);
                mod.setActualHealth(health, maxhealth);
            }
        }
    }

    @Override
    public void onKnockBackPacket(float xv, float zv)
    {
        MM_Gravity.knockBack(Minecraft.getMinecraft().thePlayer, xv, zv);
    }

    @Override
    public void onMobModsPacketToClient(String stringData, int entID)
    {
        InfernalMobsCore.instance().addRemoteEntityModifiers(Minecraft.getMinecraft().theWorld, entID, stringData);
    }

    @Override
    public void onVelocityPacket(float xv, float yv, float zv)
    {
        Minecraft.getMinecraft().thePlayer.addVelocity(xv, yv, zv);
    }

    @Override
    public void onAirPacket(int air)
    {
        airOverrideValue = air;
    }
    
    public void onRenderTick() {
        if (Minecraft.getMinecraft() == null) return;
        if (!Minecraft.getMinecraft().thePlayer.isInsideOfMaterial(Material.water) && airOverrideValue != -999)
        {
            final ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft().gameSettings, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
            GL11.glEnable(GL11.GL_BLEND);

            int right_height = 39;

            final int left = res.getScaledWidth() / 2 + 91;
            final int top = res.getScaledHeight() - right_height;
            final int full = MathHelper.ceiling_double_int((double) (airOverrideValue - 2) * 10.0D / 300.0D);
            final int partial = MathHelper.ceiling_double_int((double) airOverrideValue * 10.0D / 300.0D) - full;

            for (int i = 0; i < full + partial; ++i)
            {
                Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(left - i * 8 - 9, top, (i < full ? 16 : 25), 18, 9, 9);
            }
            GL11.glDisable(GL11.GL_BLEND);
        }
    }
}
