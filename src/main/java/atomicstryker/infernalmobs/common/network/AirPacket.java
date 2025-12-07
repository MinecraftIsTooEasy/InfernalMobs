package atomicstryker.infernalmobs.common.network;

import atomicstryker.infernalmobs.common.InfernalMobsCore;
import moddedmite.rustedironcore.network.Packet;
import moddedmite.rustedironcore.network.PacketByteBuf;
import net.minecraft.EntityPlayer;
import net.minecraft.ResourceLocation;

public class AirPacket implements Packet
{
    
    private int air;
    
    public AirPacket() {}
    
    public AirPacket(int a)
    {
        air = a;
    }

    public AirPacket(PacketByteBuf bytes)
    {
        air = bytes.readInt();
    }

    @Override
    public void write(PacketByteBuf bytes)
    {
        bytes.writeInt(air);
    }

    @Override
    public void apply(EntityPlayer entityPlayer)
    {
        InfernalMobsCore.proxy.onAirPacket(air);
    }

    @Override
    public ResourceLocation getChannel() {
        return NetworkHelper.AIR;
    }
}
