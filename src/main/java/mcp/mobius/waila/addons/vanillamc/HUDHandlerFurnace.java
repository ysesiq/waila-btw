//package mcp.mobius.waila.addons.vanillamc;
//
//import java.util.List;
//
//import mcp.mobius.waila.api.IWailaConfigHandler;
//import mcp.mobius.waila.api.IWailaDataAccessor;
//import mcp.mobius.waila.api.IWailaDataProvider;
//import mcp.mobius.waila.api.SpecialChars;
//import net.minecraft.src.*;
//
//public class HUDHandlerFurnace implements IWailaDataProvider {
//
//    @Override
//    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
//        return null;
//    }
//
//    @Override
//    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
//            IWailaConfigHandler config) {
//        return currenttip;
//    }
//
//    @Override
//    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
//            IWailaConfigHandler config) {
//        int cookTime = accessor.getNBTData().getShort("CookTime");
//        NBTTagList tag = accessor.getNBTData().getTagList("Items", 10);
//
//        String renderStr = "";
//        {
//            ItemStack stack = ItemStack.loadItemStackFromNBT(tag.getCompoundTagAt(0));
//            String name = GameData.getItemRegistry().getNameForObject(stack.getItem());
//            renderStr += SpecialChars.getRenderString(
//                    "waila.stack",
//                    "1",
//                    name,
//                    String.valueOf(stack.stackSize),
//                    String.valueOf(stack.getItemDamage()));
//        }
//        {
//            ItemStack stack = ItemStack.loadItemStackFromNBT(tag.getCompoundTagAt(1));
//            String name = GameData.getItemRegistry().getNameForObject(stack.getItem());
//            renderStr += SpecialChars.getRenderString(
//                    "waila.stack",
//                    "1",
//                    name,
//                    String.valueOf(stack.stackSize),
//                    String.valueOf(stack.getItemDamage()));
//        }
//
//        renderStr += SpecialChars.getRenderString("waila.progress", String.valueOf(cookTime), String.valueOf(200));
//
//        {
//            ItemStack stack = ItemStack.loadItemStackFromNBT(tag.getCompoundTagAt(2));
//            String name = GameData.getItemRegistry().getNameForObject(stack.getItem());
//            renderStr += SpecialChars.getRenderString(
//                    "waila.stack",
//                    "1",
//                    name,
//                    String.valueOf(stack.stackSize),
//                    String.valueOf(stack.getItemDamage()));
//        }
//
//        currenttip.add(renderStr);
//
//        return currenttip;
//    }
//
//    @Override
//    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
//            IWailaConfigHandler config) {
//        return currenttip;
//    }
//
//    @Override
//    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x,
//                                     int y, int z) {
//        if (te != null) te.writeToNBT(tag);
//        return tag;
//    }
//
//    public static void register() {}
//}
