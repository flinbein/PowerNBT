package me.dpohvar.powernbt.utils.viewer.components;

import me.dpohvar.powernbt.utils.viewer.ContainerControl;
import me.dpohvar.powernbt.utils.viewer.EventBuilder;
import me.dpohvar.powernbt.utils.viewer.ViewerStyle;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;

public class ButtonRemove extends InteractiveElement {

    static ButtonRemove create(ViewerStyle style, ContainerControl control, String parentAccess){
        ClickEvent click = getClickEvent(control, parentAccess);
        if (click == null) return null;
        return new ButtonRemove(style, click, getHoverEvent(control));
    }

    private ButtonRemove(ViewerStyle style, ClickEvent click, HoverEvent hover){
        super(style.getColor("buttons.control.remove"), style.getIcon("buttons.control.remove"), click, hover, null);
    }

    private static ClickEvent getClickEvent(ContainerControl control, String parentAccess){
        if (control.isReadonly()) return null;
        String parentPart;
        if (parentAccess == null) parentPart = "";
        else if (parentAccess.isEmpty()) parentPart = " ,";
        else parentPart = " " + parentAccess;
        return EventBuilder.runNbt(control.getSelectorWithQuery() + " remove" + parentPart, false);
    }

    private static HoverEvent getHoverEvent(ContainerControl control){
        if (control.isReadonly()) return null;
        return EventBuilder.popup("remove value");
    }
}
