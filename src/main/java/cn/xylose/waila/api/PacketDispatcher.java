package cn.xylose.waila.api;

import mcp.mobius.waila.Waila;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.Minecraft;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;

public class PacketDispatcher {
    public static Packet250CustomPayload getPacket(String type, byte[] data) {
        return new Packet250CustomPayload(type, data);
    }

    public static void sendPacketToServer(Packet packet) {
        Minecraft.getMinecraft().getNetHandler().addToSendQueue(packet);
    }

    public static void sendPacketToPlayer(Packet packet, EntityPlayerMP player) {
        player.playerNetServerHandler.sendPacketToPlayer(packet);
    }

    public static void sendPacketToAllAround(double X, double Y, double Z, double range, int dimensionId, Packet packet) {
        MinecraftServer server = MinecraftServer.getServer();
        if (server != null) {
            server.getConfigurationManager().sendToAllNear(X, Y, Z, range, dimensionId, packet);
        } else {
            Waila.log.warn("Attempt to send packet to all around without a server instance available");
        }
    }

    public static void sendPacketToAllInDimension(Packet packet, int dimId) {
        MinecraftServer server = MinecraftServer.getServer();
        if (server != null) {
            server.getConfigurationManager().sendPacketToAllPlayersInDimension(packet, dimId);
        } else {
            Waila.log.warn("Attempt to send packet to all in dimension without a server instance available");
        }
    }

    public static void sendPacketToAllPlayers(Packet packet) {
        MinecraftServer server = MinecraftServer.getServer();
        if (server != null) {
            server.getConfigurationManager().sendPacketToAllPlayers(packet);
        } else {
            Waila.log.warn("Attempt to send packet to all in dimension without a server instance available");
        }
    }
}
