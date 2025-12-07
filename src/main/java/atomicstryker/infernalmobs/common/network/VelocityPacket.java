package atomicstryker.infernalmobs.common.network;

import atomicstryker.infernalmobs.common.InfernalMobsCore;
import moddedmite.rustedironcore.network.Packet;
import moddedmite.rustedironcore.network.PacketByteBuf;
import net.minecraft.EntityPlayer;
import net.minecraft.ResourceLocation;

public class VelocityPacket implements Packet
{
    
    private float xv, yv, zv;
    
    public VelocityPacket() {}
    
    public VelocityPacket(float x, float y, float z)
    {
        xv = x;
        yv = y;
        zv = z;
    }

    public VelocityPacket(PacketByteBuf bytes) {
        xv = bytes.readFloat();
        yv = bytes.readFloat();
        zv = bytes.readFloat();
    }

    @Override
    public void write(PacketByteBuf bytes) {
        bytes.writeFloat(xv);
        bytes.writeFloat(yv);
        bytes.writeFloat(zv);
    }

    @Override
    public void apply(EntityPlayer entityPlayer) {
        InfernalMobsCore.proxy.onVelocityPacket(xv, yv, zv);

    }

    @Override
    public ResourceLocation getChannel() {
        return NetworkHelper.Velocity;
    }
}
