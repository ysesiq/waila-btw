package mcp.mobius.waila.overlay.tooltiprenderers;

import java.awt.Dimension;

import net.minecraft.src.MathHelper;

import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaTooltipRenderer;
import mcp.mobius.waila.overlay.DisplayUtil;
import mcp.mobius.waila.overlay.IconUI;

/**
 * Custom renderer for health bars. Syntax : {waila.health, nheartperline, health, maxhealth}
 * 
 */
public class TTRenderHealth implements IWailaTooltipRenderer {

    @Override
    public Dimension getSize(String[] params, IWailaCommonAccessor accessor) {
        float maxhearts = Float.parseFloat(params[0]);
        float maxhealth = Float.parseFloat(params[2]);

        int heartsPerLine = (int) (Math.min(maxhearts, Math.ceil(maxhealth)));
        int nlines = (int) (Math.ceil(maxhealth / maxhearts));

        return new Dimension(8 * heartsPerLine, 10 * nlines - 2);
    }

    @Override
    public void draw(String[] params, IWailaCommonAccessor accessor) {
        float maxhearts = Float.parseFloat(params[0]);
        float health = Float.parseFloat(params[1]);
        float maxhealth = Float.parseFloat(params[2]);

        int nhearts = MathHelper.ceiling_float_int(maxhealth);
        int heartsPerLine = (int) (Math.min(maxhearts, Math.ceil(maxhealth)));

        int offsetX = 0;
        int offsetY = 0;

        for (int iheart = 1; iheart <= nhearts; iheart++) {

            if (iheart <= MathHelper.floor_float(health)) {
                DisplayUtil.renderIcon(offsetX, offsetY, 8, 8, IconUI.HEART);
                offsetX += 8;
            }

            if ((iheart > health) && (iheart < health + 1)) {
                DisplayUtil.renderIcon(offsetX, offsetY, 8, 8, IconUI.HHEART);
                offsetX += 8;
            }

            if (iheart >= health + 1) {
                DisplayUtil.renderIcon(offsetX, offsetY, 8, 8, IconUI.EHEART);
                offsetX += 8;
            }

            if (iheart % heartsPerLine == 0) {
                offsetY += 10;
                offsetX = 0;
            }

        }
    }
}
