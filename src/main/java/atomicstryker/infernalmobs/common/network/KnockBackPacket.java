package atomicstryker.infernalmobs.common.network;

import atomicstryker.infernalmobs.common.InfernalMobsCore;
import moddedmite.rustedironcore.network.Packet;
import moddedmite.rustedironcore.network.PacketByteBuf;
import net.minecraft.EntityPlayer;
import net.minecraft.ResourceLocation;

public class KnockBackPacket implements Packet
{
    
    private float xv, zv;
    
    public KnockBackPacket() {}
    
    public KnockBackPacket(float x, float z)
    {
        xv = x;
        zv = z;
    }

    public KnockBackPacket(PacketByteBuf bytes) {
        xv = bytes.readFloat();
        zv = bytes.readFloat();
    }

    @Override
    public void write(PacketByteBuf bytes) {
        bytes.writeFloat(xv);
        bytes.writeFloat(zv);
    }

    @Override
    public void apply(EntityPlayer entityPlayer) {
        InfernalMobsCore.proxy.onKnockBackPacket(xv, zv);
    }

    @Override
    public ResourceLocation getChannel() {
        return NetworkHelper.KnockBack;
    }
}
