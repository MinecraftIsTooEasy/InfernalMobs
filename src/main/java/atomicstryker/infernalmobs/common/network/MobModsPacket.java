package atomicstryker.infernalmobs.common.network;

import moddedmite.rustedironcore.network.Packet;
import moddedmite.rustedironcore.network.PacketByteBuf;
import net.minecraft.*;
import net.minecraft.server.MinecraftServer;
import atomicstryker.infernalmobs.common.InfernalMobsCore;
import atomicstryker.infernalmobs.common.MobModifier;

import java.util.Arrays;

public class MobModsPacket implements Packet
{
    
    private String stringData;
    private int entID;
    private byte sentFromServer;
    
    public MobModsPacket() {}
    
    public MobModsPacket(String str, int i, byte ir)
    {
        stringData = str;
        entID = i;
        sentFromServer = ir;
    }

    public MobModsPacket(PacketByteBuf bytes) {
        sentFromServer = bytes.readByte();
        short len = bytes.readShort();
        String[] strings = new String[len];
        for (int i = 0; i < len; i++) strings[i] = bytes.readString();
        stringData = Arrays.toString(strings);
        entID = bytes.readInt();
    }

    @Override
    public void write(PacketByteBuf bytes) {
        bytes.writeByte(sentFromServer);
        bytes.writeShort(stringData.length());
        for (char c : stringData.toCharArray()) bytes.writeString(String.valueOf(c));
        bytes.writeInt(entID);
    }

    @Override
    public void apply(EntityPlayer entityPlayer) {
        if (sentFromServer != 0)
        {
            // so we are on client now
            InfernalMobsCore.proxy.onMobModsPacketToClient(stringData, entID);
        }
        else
        {
            // else we are on serverside
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
                        stringData = mod.getLinkedModNameUntranslated();
                        p.playerNetServerHandler.sendPacketToPlayer(new MobModsPacket(stringData, entID, (byte)1).toVanilla());
                        InfernalMobsCore.instance().sendHealthPacket(e, mod.getActualHealth(e));
                    }
                }
            }
        }
    }

    @Override
    public ResourceLocation getChannel() {
        return NetworkHelper.MobMods;
    }
}
