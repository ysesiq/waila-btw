package mcp.mobius.waila.network;

import mcp.mobius.waila.utils.NBTUtil;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet250CustomPayload;

import java.io.*;

public class Packet0x04EntNBTData {
    public byte header;
    public NBTTagCompound tag;

    public Packet0x04EntNBTData(Packet250CustomPayload packet) {
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));

        try {
            this.header = inputStream.readByte();
            this.tag = NBTUtil.readNBTTagCompound(inputStream);
        } catch (IOException var4) {
        }

    }

    public static Packet250CustomPayload create(NBTTagCompound tag) {
        Packet250CustomPayload packet = new Packet250CustomPayload();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(17);
        DataOutputStream outputStream = new DataOutputStream(bos);

        try {
            outputStream.writeByte(4);
            NBTUtil.writeNBTTagCompound(tag, outputStream);
        } catch (IOException var5) {
        }

        packet.channel = "Waila";
        packet.data = bos.toByteArray();
        packet.length = bos.size();
        return packet;
    }
}

