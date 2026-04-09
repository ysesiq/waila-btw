package mcp.mobius.waila.handlers;

import cn.xylose.waila.mixin.accessor.EntityChickenAccessor;
import cn.xylose.waila.mixin.accessor.EntityZombieAccessor;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import net.minecraft.src.*;

import java.util.List;

public class HUDHandlerEntitiesServer implements IWailaEntityProvider {

    @Override
    public Entity getWailaOverride(IWailaEntityAccessor accessor, IWailaConfigHandler config) { return null; }

    @Override
    public List<String> getWailaHead(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) { return currenttip; }

    @Override
    public List<String> getWailaBody(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) { return currenttip; }

    @Override
    public List<String> getWailaTail(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) { return currenttip; }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, Entity entity, NBTTagCompound tag, World world) {
        if (tag == null || entity == null) {
            return tag;
        }

        tag.setInteger("WailaEntityID", entity.entityId);

        if (entity instanceof EntityAnimal animal) {
            tag.setInteger("AnimalGrowingAge", animal.getGrowingAge());
            tag.setByte("fcHungerLvl", (byte) animal.getHungerLevel());
            tag.setInteger("fcHungerCnt", animal.hungerCountdown);
            tag.setBoolean("TooHungryToGrow", animal.isTooHungryToGrow());
        }

        if (entity instanceof EntityChickenAccessor chickenAccessor) {
            tag.setLong("fcTimeToLayEgg", chickenAccessor.getTimeToLayEgg());
        }

        if (entity instanceof EntityWolf) {
            tag.setBoolean("BTWWolfIsFullyFed", ((EntityWolf) entity).isFullyFed());
        }

        if (entity instanceof EntityZombieAccessor zombieAccessor) {
            int conversionTime = zombieAccessor.getConversionTime();
            if (conversionTime > 0) {
                tag.setInteger("ConversionTime", conversionTime);
            }
        }

        if (entity instanceof EntitySpider spider) {
            tag.setInteger("btw_spider_web_count", spider.hasWeb() ? 1 : 0);
            tag.setBoolean("btw_spider_has_web", spider.hasWeb());
        }

        if (entity instanceof EntityCreature creature) {
            tag.setBoolean("Possessible", creature.getCanCreatureTypeBePossessed());
            tag.setBoolean("IsPossessed", creature.isPossessed());
            tag.setBoolean("IsFullyPossessed", creature.isFullyPossessed());
            tag.setInteger("PossessionLevel", creature.getPossessionLevel());
            tag.setInteger("PossessionTimer", creature.possessionTimer);
        }

        return tag;
    }

    public static void register() {
        IWailaEntityProvider serverProvider = new HUDHandlerEntitiesServer();

        ModuleRegistrar.instance().registerNBTProvider(serverProvider, EntityCreature.class);
    }
}