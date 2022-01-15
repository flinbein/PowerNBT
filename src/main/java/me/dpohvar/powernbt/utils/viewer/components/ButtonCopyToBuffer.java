package me.dpohvar.powernbt.utils.viewer.components;

import me.dpohvar.powernbt.utils.viewer.ContainerControl;
import me.dpohvar.powernbt.utils.viewer.EventBuilder;
import me.dpohvar.powernbt.utils.viewer.ViewerStyle;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;

public class ButtonCopyToBuffer extends InteractiveElement {

    static ButtonCopyToBuffer create(ViewerStyle style, ContainerControl control){
        ClickEvent click = getClickEvent(control);
        if (click == null) return null;
        return new ButtonCopyToBuffer(style, click, getHoverEvent(control));
    }

    private ButtonCopyToBuffer(ViewerStyle style, ClickEvent click, HoverEvent hover){
        super(style.getColor("buttons.control.copyToBuffer"), style.getIcon("buttons.control.copyToBuffer"), click, hover, null);
    }

    private static ClickEvent getClickEvent(ContainerControl control){
        if (control.isReadonly()) return null;
        return EventBuilder.runNbt(control.getSelectorWithQuery()+" copy", true);
    }

    private static HoverEvent getHoverEvent(ContainerControl control){
        if (control.isReadonly()) return null;
        return EventBuilder.popup("copy to buffer");
    }
}
