package mcp.mobius.waila.gui.events;

import net.minecraft.src.Minecraft;
import org.lwjgl.input.Mouse;

import mcp.mobius.waila.gui.interfaces.IWidget;

public class MouseEvent {

    public enum EventType {
        NONE,
        MOVE,
        CLICK,
        RELEASED,
        DRAG,
        WHEEL,
        ENTER,
        LEAVE
    }

    public long timestamp;
    public Minecraft mc;
    public IWidget srcwidget;
    public IWidget trgwidget;
    public double x, y;
    public int z;
    public static int buttonCount = Mouse.getButtonCount();
    public boolean[] buttonState = new boolean[buttonCount];
    public EventType type;
    public int button = -1;

    public MouseEvent(IWidget widget) {
        this.srcwidget = widget;
        this.timestamp = System.nanoTime();

        this.mc = Minecraft.getMinecraft();

        this.x = (double) Mouse.getEventX() * (double) this.srcwidget.getSize().getX() / (double) this.mc.displayWidth;
        this.y = (double) this.srcwidget.getSize().getY()
                - (double) Mouse.getEventY() * (double) this.srcwidget.getSize().getY() / (double) this.mc.displayHeight
                - 1.0;

        this.z = Mouse.getDWheel();

        for (int i = 0; i < buttonCount; i++) buttonState[i] = Mouse.isButtonDown(i);

        this.trgwidget = this.srcwidget.getWidgetAtCoordinates(this.x, this.y);
    }

    public String toString() {
        StringBuilder retstring = new StringBuilder(
                String.format(
                        "MOUSE %s :  [%s] [ %.2f %.2f %d ] [",
                        this.type,
                        this.timestamp,
                        this.x,
                        this.y,
                        this.z));
        if (buttonCount < 5)
            for (int i = 0; i < buttonCount; i++) retstring.append(String.format(" %s ", this.buttonState[i]));
        else for (int i = 0; i < 5; i++) retstring.append(String.format(" %s ", this.buttonState[i]));
        retstring.append("]");

        if (this.button != -1) retstring.append(String.format(" Button %s", this.button));

        return retstring.toString();
    }

    // Returns the event type based on the previous mouse event.
    public EventType getEventType(MouseEvent me) {

        this.type = EventType.NONE;

        if (this.trgwidget != me.trgwidget) {
            this.type = EventType.ENTER;
            return this.type;
        }

        if (this.z != 0) {
            this.type = EventType.WHEEL;
            return this.type;
        }

        for (int i = 0; i < buttonCount; i++) {
            if (this.buttonState[i] != me.buttonState[i]) {
                if (this.buttonState[i]) this.type = EventType.CLICK;
                else this.type = EventType.RELEASED;
                this.button = i;
                return this.type;
            }
        }

        // MOVE & DRAG EVENTS (we moved the mouse and button 0 was clicked or not)
        if ((this.x != me.x) || (this.y != me.y)) {
            if (this.buttonState[0]) this.type = EventType.DRAG;
            else this.type = EventType.MOVE;
            return this.type;
        }

        return this.type;
    }
}
