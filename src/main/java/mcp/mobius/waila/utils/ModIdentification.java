package mcp.mobius.waila.utils;

import mcp.mobius.waila.Waila;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.src.*;
import org.apache.commons.lang3.StringUtils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Optional;

public class ModIdentification {

    public static HashMap<String, String> modSource_Name = new HashMap<String, String>();
    public static HashMap<String, String> modSource_ID = new HashMap<String, String>();
//	public static HashMap<Integer, String> itemMap         = new HashMap<Integer, String>();
//	public static HashMap<String, String> keyhandlerStrings = new HashMap<String,  String>();

    public static void init() {

//        NBTTagList itemDataList = new NBTTagList();
//        GameData.writeItemData(itemDataList);
//
//        for(int i = 0; i < itemDataList.tagCount(); i++) {
//            ItemData itemData = new ItemData((NBTTagCompound) itemDataList.tagAt(i));
//            itemMap.put(itemData.getItemId(), itemData.getModId());
//        }

        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            modSource_Name.put(mod.getMetadata().getName(), mod.getMetadata().getName());
            modSource_ID.put(mod.getMetadata().getName(), mod.getMetadata().getId());
        }

        // TODO : Update this to match new version (1.7.2)
        modSource_Name.put("1.6.2.jar", "Minecraft");
        modSource_Name.put("1.6.3.jar", "Minecraft");
        modSource_Name.put("1.6.4.jar", "Minecraft");
        modSource_Name.put("1.7.2.jar", "Minecraft");
        modSource_Name.put("Forge", "Minecraft");
        modSource_ID.put("1.6.2.jar", "Minecraft");
        modSource_ID.put("1.6.3.jar", "Minecraft");
        modSource_ID.put("1.6.4.jar", "Minecraft");
        modSource_ID.put("1.7.2.jar", "Minecraft");
        modSource_ID.put("client-intermediary.jar", "Minecraft");
        modSource_ID.put("Forge", "Minecraft");

//      for (String s : this.modSourceList.keySet())
//    	if (this.modSourceList.get(s) == "Minecraft Coder Pack")
//    		this.modSourceList.put(s, "Minecraft");

        // Code to retrieve all the registered keybindings along with the mod adding them.
//        Field keyHandlers_Field = AccessHelper.getDeclaredField("cpw.mods.fml.client.registry.KeyBindingRegistry", "keyHandlers");
//        try {
//        	Set<KeyHandler> keyHandlers = (Set<KeyHandler>)keyHandlers_Field.get(KeyBindingRegistry.instance());
//            for (KeyHandler keyhandler : keyHandlers)
//            	for (int i = 0; i < keyhandler.getKeyBindings().length; i++)
//            		keyhandlerStrings.put(keyhandler.getKeyBindings()[i].keyDescription, idFromObject(keyhandler));
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		}

    }

    public static String nameFromObject(Object obj) {
        String objPath = obj.getClass().getProtectionDomain().getCodeSource().getLocation().toString();

        objPath = URLDecoder.decode(objPath, StandardCharsets.UTF_8);

        String modName = "<Unknown>";
        for (String s : modSource_Name.keySet()) {
            if (objPath.contains(s)) {
                modName = modSource_Name.get(s);
                break;
            }
        }

        if (modName.equals("Minecraft Coder Pack"))
            modName = "Minecraft";

        return modName;
    }

    public static String nameFromStack(ItemStack stack) {
//        try {
//            ModContainer mod = GameData.findModOwner(GameData.itemRegistry.getNameForObject(stack.getItem()));
//            return mod == null ? "Minecraft" : mod.getName();
//        } catch (NullPointerException e) {
//            return "";
//        }

//        try {
//            String modID = itemMap.get(stack.itemID);
//            Optional<ModContainer> mod = FabricLoader.getInstance().getModContainer(modID);
//            String modname = mod == null ? modID : mod.map(ModContainer::getMetadata).map(ModMetadata::getName).orElse("");
//            return modname;
//        } catch (NullPointerException e){
//            //System.out.printf("NPE : %s\n",itemstack.toString());
//            return "";
//        }

        if (stack.getItem() instanceof ItemBlock) {
            Block block = Block.blocksList[((ItemBlock) stack.getItem()).getBlockID()];
            String textureName = block.getTextureName();
            String iconName = "";
            String modid = block.getModId();
            if (block.blockIcon != null) iconName = block.getIcon(1, 1).getIconName();
            if (textureName.contains(":")) {
                return parseTexture(textureName);
            } else if (iconName.contains(":")) {
                return parseTexture(iconName);
            } else {
                return getModNameById(modid);
            }
        }
        return Waila.modsName;
    }

    public static String parseTexture(String str) {
        String[] parts = str.split(":", 2);
        return getModNameById(parts[0]);
    }

    public static String getModNameById(String modId) {
        return FabricLoader.getInstance().getModContainer(modId)
                .map(ModContainer::getMetadata)
                .map(ModMetadata::getName)
                .map(ModIdentification::normalizeModName)
                .orElse(StringUtils.capitalize(modId));
    }

    private static String normalizeModName(String name) {
        if (name.equals("Better Than Wolves: Community Edition")) {
            return "Better Than Wolves";
        }
        return name;
    }
}
