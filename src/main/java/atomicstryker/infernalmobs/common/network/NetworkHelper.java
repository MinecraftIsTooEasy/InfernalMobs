package atomicstryker.infernalmobs.common.network;

import moddedmite.rustedironcore.network.Network;
import moddedmite.rustedironcore.network.Packet;
import moddedmite.rustedironcore.network.PacketReader;
import net.minecraft.ResourceLocation;
import net.minecraft.ServerPlayer;
import net.minecraft.server.MinecraftServer;

public class NetworkHelper
{
    public static final ResourceLocation AIR = new ResourceLocation("InfernalMobs", "OpenWindow");
    public static final ResourceLocation Health = new ResourceLocation("InfernalMobs", "OpenShop");
    public static final ResourceLocation KnockBack = new ResourceLocation("InfernalMobs", "SyncShopInfo");
    public static final ResourceLocation MobMods = new ResourceLocation("InfernalMobs", "SyncMoney");
    public static final ResourceLocation Velocity = new ResourceLocation("InfernalMobs", "SyncPrice");

    public static String Channel = "AS_IF";
    public static void sendToClient(ServerPlayer player, Packet packet) {
        Network.sendToClient(player, packet);
    }

    public static void sendToServer(Packet packet) {
        Network.sendToServer(packet);
    }

    public static void sendToAllPlayers(Packet packet) {
        Network.sendToAllPlayers(packet);
    }

    public static void init() {
        initServer();
    }

    private static void initServer() {
        PacketReader.registerServerPacketReader(NetworkHelper.AIR, AirPacket::new);
        PacketReader.registerServerPacketReader(NetworkHelper.Health, HealthPacket::new);
        PacketReader.registerServerPacketReader(NetworkHelper.KnockBack, KnockBackPacket::new);
        PacketReader.registerServerPacketReader(NetworkHelper.MobMods, MobModsPacket::new);
        PacketReader.registerServerPacketReader(NetworkHelper.Velocity, VelocityPacket::new);
    }
    
}
