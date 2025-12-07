package atomicstryker.infernalmobs.common.network;

import moddedmite.rustedironcore.network.Packet;
import moddedmite.rustedironcore.network.PacketByteBuf;
import net.minecraft.*;
import net.minecraft.server.MinecraftServer;
import atomicstryker.infernalmobs.common.InfernalMobsCore;
import atomicstryker.infernalmobs.common.MobModifier;

import java.util.Arrays;

public class HealthPacket implements Packet
{
    
    private String stringData;
    private int entID;
    private float health;
    private float maxhealth;
    
    public HealthPacket() {}
    
    public HealthPacket(String u, int i, float f1, float f2)
    {
        stringData = u;
        entID = i;
        health = f1;
        maxhealth = f2;
    }

    public HealthPacket(PacketByteBuf bytes) {
        short len = bytes.readShort();
        String[] strings = new String[len];
        for (int i = 0; i < len; i++) strings[i] = bytes.readString();
        stringData = Arrays.toString(strings);
        entID = bytes.readInt();
        health = bytes.readFloat();
        maxhealth = bytes.readFloat();


    }

    @Override
    public void write(PacketByteBuf bytes) {
        bytes.writeShort(stringData.length());
        for (char c : stringData.toCharArray()) bytes.writeString(String.valueOf(c));
        bytes.writeInt(entID);
        bytes.writeFloat(health);
        bytes.writeFloat(maxhealth);
    }

    @Override
    public void apply(EntityPlayer entityPlayer) {
        // client always sends packets with health = maxhealth = 0
        if (maxhealth > 0)
        {
            InfernalMobsCore.proxy.onHealthPacketForClient(stringData, entID, health, maxhealth);
        }
        else
        {
            ServerPlayer p = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(stringData);
            if (p != null)
            {
                Entity ent = p.worldObj.getEntityByID(entID);
                if (ent != null && ent instanceof EntityLivingBase)
                {
                    EntityLivingBase e = (EntityLivingBase) ent;
                    MobModifier mod = InfernalMobsCore.getMobModifiers(e);
                    if (mod != null)
                    {
                        health = e.getHealth();
                        maxhealth = e.getMaxHealth();
                        p.playerNetServerHandler.sendPacketToPlayer(new HealthPacket(stringData, entID, health, maxhealth).toVanilla());
                    }
                }
            }
        }
    }

    @Override
    public ResourceLocation getChannel() {
        return NetworkHelper.Health;
    }
}
