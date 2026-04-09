package mcp.mobius.waila.client;

import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.gui.truetyper.FontLoader;
import mcp.mobius.waila.gui.truetyper.TrueTypeFont;
import mcp.mobius.waila.handlers.HUDHandlerBlocks;
import mcp.mobius.waila.handlers.HUDHandlerEntities;
import mcp.mobius.waila.overlay.tooltiprenderers.TTRenderArmor;
import mcp.mobius.waila.overlay.tooltiprenderers.TTRenderHealth;
import mcp.mobius.waila.overlay.tooltiprenderers.TTRenderProgressBar;
import mcp.mobius.waila.overlay.tooltiprenderers.TTRenderStack;
import mcp.mobius.waila.server.ProxyServer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.ResourceLocation;

@Environment(EnvType.CLIENT)
public class ProxyClient extends ProxyServer {

    TrueTypeFont minecraftiaFont;

    public ProxyClient() {}

    @Override
    public void registerHandlers() {
        ModuleRegistrar.instance().registerHeadProvider(new HUDHandlerBlocks(), Block.class);
        ModuleRegistrar.instance().registerTailProvider(new HUDHandlerBlocks(), Block.class);

        ModuleRegistrar.instance().registerHeadProvider(new HUDHandlerEntities(), Entity.class);
        ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerEntities(), Entity.class);
        ModuleRegistrar.instance().registerTailProvider(new HUDHandlerEntities(), Entity.class);

        ModuleRegistrar.instance().addConfig("General", "general.showents");
        ModuleRegistrar.instance().addConfig("General", "general.showhp");
        ModuleRegistrar.instance().addConfig("General", "general.showatk");
        ModuleRegistrar.instance().addConfig("General", "general.showarmor");
        ModuleRegistrar.instance().addConfig("General", "general.showbreed");
        ModuleRegistrar.instance().addConfig("General", "general.showcrop");
        ModuleRegistrar.instance().addConfig("General", "general.showcropdetails");

        ModuleRegistrar.instance().registerTooltipRenderer("waila.health", new TTRenderHealth());
        ModuleRegistrar.instance().registerTooltipRenderer("waila.stack", new TTRenderStack());
        ModuleRegistrar.instance().registerTooltipRenderer("waila.progress", new TTRenderProgressBar());
        ModuleRegistrar.instance().registerTooltipRenderer("waila.armor", new TTRenderArmor());
    }

    @Override
    public Object getFont() {
        if (minecraftiaFont == null)
            minecraftiaFont = FontLoader.createFont(new ResourceLocation("waila", "fonts/Minecraftia.ttf"), 14, true);
        return this.minecraftiaFont;
    }
}
