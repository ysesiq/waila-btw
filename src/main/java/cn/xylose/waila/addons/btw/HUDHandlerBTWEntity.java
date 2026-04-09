package cn.xylose.waila.addons.btw;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;

@Environment(EnvType.CLIENT)
public class HUDHandlerBTWEntity implements IWailaEntityProvider {

    @Override
    public Entity getWailaOverride(IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {

        if (entity instanceof EntityAnimal && config.getConfig("btw.animal_hunger")) {
            NBTTagCompound tag = accessor.getNBTData();
            if (tag != null && tag.hasKey("AnimalGrowingAge") && tag.hasKey("fcHungerLvl") && tag.hasKey("fcHungerCnt")) {

                int growingAge = tag.getInteger("AnimalGrowingAge");
                int hungerLevel = tag.getByte("fcHungerLvl") & 0xFF;
                boolean tooHungry = tag.hasKey("TooHungryToGrow") && tag.getBoolean("TooHungryToGrow");

                int secondsLeft = -growingAge / 20;
                if (growingAge < 0) {
                    String template = tooHungry ? I18n.getString("info.btw.growup.toohungry") : I18n.getString("info.btw.growup.normal");
                    currenttip.add(String.format(template, secondsLeft));
                }

                if (hungerLevel > 0) {
                    String hungerLabel = hungerLevel == 1 ? I18n.getString("info.btw.hunger.famished") : I18n.getString("info.btw.hunger.starving");
                    currenttip.add(I18n.getString("info.btw.hunger.state") + hungerLabel);
                }
            }
        }

        if (entity instanceof EntityChicken && config.getConfig("btw.chicken_egg_timer")) {
            NBTTagCompound tag = accessor.getNBTData();
            if (tag != null && tag.hasKey("fcTimeToLayEgg")) {
                long eggTick = tag.getLong("fcTimeToLayEgg");
                if (eggTick > 0) {
                    long now = entity.worldObj.getWorldTime();
                    long remain = eggTick - now;
                    if (remain > 0) {
                        long seconds = remain / 20;
                        String template = I18n.getString("info.btw.chicken_egg_timer");
                        currenttip.add(String.format(template, seconds));
                    }
                }
            }
        }

        if (entity instanceof EntityWolf && config.getConfig("btw.wolf_poop")) {
            NBTTagCompound tag = accessor.getNBTData();
            if (tag != null && tag.hasKey("BTWWolfIsFullyFed") && tag.getBoolean("BTWWolfIsFullyFed")) {
                currenttip.add(I18n.getString("info.btw.wolf_poop_chance")); // "Wolf is well-fed: 1/24000 chance per tick to poop (avg 20min)"
            }
        }

        if (entity instanceof EntitySpider && config.getConfig("btw.spider_web")) {
            NBTTagCompound tag = accessor.getNBTData();
            if (tag != null && tag.hasKey("btw_spider_has_web") && tag.getBoolean("btw_spider_has_web")) {
                currenttip.add(I18n.getString("info.btw.has_web"));
            }
        }

        if (entity instanceof EntityZombie && config.getConfig("btw.zombie_conversion_time")) {
            NBTTagCompound tag = accessor.getNBTData();
            if (tag != null && tag.hasKey("ConversionTime")) {
                int remain = tag.getInteger("ConversionTime");
                if (remain > 0) {
                    int seconds = remain / 20;
                    String template = I18n.getString("info.btw.zombie_conversion_time");
                    currenttip.add(String.format(template, seconds));
                }
            }
        }

        NBTTagCompound tag = accessor.getNBTData();
        if (tag != null && tag.hasKey("Possessible") && tag.getBoolean("Possessible")) {
            int level = tag.getInteger("PossessionLevel");
            int timer = tag.getInteger("PossessionTimer");
            boolean fully = tag.getBoolean("IsFullyPossessed");

            if (fully || level > 0) {
                String phase = null;
                if (fully || level > 1) {
                    phase = I18n.getString("info.btw.possession.full");
                } else if (timer > 0) {
                    phase = String.format(I18n.getString("info.btw.possession.progressing"), timer / 20);
                }
                if (phase != null) {
                    currenttip.add(phase);
                }
            }
        }
        return currenttip;
    }

    @Override
    public List<String> getWailaTail(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, Entity ent, NBTTagCompound tag, World world) {
        return null;
    }

    public static void register() {
        IWailaEntityProvider provider = new HUDHandlerBTWEntity();

        ModuleRegistrar.instance().registerBodyProvider(provider, EntityCreature.class);

        ModuleRegistrar.instance().addConfig("BTW", "btw.animal_hunger");
        ModuleRegistrar.instance().addConfig("BTW", "btw.chicken_egg_timer");
        ModuleRegistrar.instance().addConfig("BTW", "btw.wolf_poop");

        ModuleRegistrar.instance().addConfig("BTW", "btw.possession");

        ModuleRegistrar.instance().addConfig("BTW", "btw.spider_web");

        ModuleRegistrar.instance().addConfig("BTW", "btw.zombie_conversion_time");

    }
}