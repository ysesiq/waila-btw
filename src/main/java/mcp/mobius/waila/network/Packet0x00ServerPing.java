package mcp.mobius.waila.network;

import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;

import java.io.*;
import java.util.HashMap;

public class Packet0x00ServerPing {
    public byte header;
    public HashMap<String, Boolean> forcedKeys = new HashMap();

    public Packet0x00ServerPing(Packet250CustomPayload packet) {
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));

        try {
            this.header = inputStream.readByte();
            this.forcedKeys.clear();

            while(true) {
                String key = Packet.readString(inputStream, 255);
                if (key.equals("END OF LIST")) {
                    break;
                }

                boolean value = inputStream.readBoolean();
                this.forcedKeys.put(key, value);
            }
        } catch (IOException var5) {
        }

    }

    public static Packet250CustomPayload create() {
        Packet250CustomPayload packet = new Packet250CustomPayload();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1);
        DataOutputStream outputStream = new DataOutputStream(bos);

        try {
            outputStream.writeByte(0);
//            ConfigCategory serverForcing = ConfigHandler.instance().config.getCategory(Constants.CATEGORY_SERVER);
//            Iterator i$ = serverForcing.keySet().iterator();
//
//            while(i$.hasNext()) {
//                String key = (String)i$.next();
//                if (WailaConfig.CATEGORY_SERVER.getBooleanValue()) {
//                    Packet.writeString(key, outputStream);
                    outputStream.writeBoolean(false);
//                }
//            }

            Packet.writeString("END OF LIST", outputStream);
        } catch (IOException var6) {
        }

        packet.channel = "Waila";
        packet.data = bos.toByteArray();
        packet.length = bos.size();
        return packet;
    }
}
