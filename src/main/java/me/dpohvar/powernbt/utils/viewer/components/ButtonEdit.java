package me.dpohvar.powernbt.utils.viewer.components;

import me.dpohvar.powernbt.utils.viewer.ContainerControl;
import me.dpohvar.powernbt.utils.viewer.EventBuilder;
import me.dpohvar.powernbt.utils.viewer.ViewerStyle;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;

public class ButtonEdit extends InteractiveElement {

    static ButtonEdit create(ViewerStyle style, ContainerControl control){
        ClickEvent click = getClickEvent(control);
        if (click == null) return null;
        return new ButtonEdit(style, click, getHoverEvent(control));
    }

    private ButtonEdit(ViewerStyle style, ClickEvent click, HoverEvent hover){
        super(style.getColor("buttons.control.edit"), style.getIcon("buttons.control.edit"), click, hover, null);
    }

    private static ClickEvent getClickEvent(ContainerControl control){
        if (control.isReadonly()) return null;
        return EventBuilder.suggestNbt(control.getSelectorWithQuery()+" = ", false);
    }

    private static HoverEvent getHoverEvent(ContainerControl control){
        if (control.isReadonly()) return null;
        return EventBuilder.popup("edit value");
    }
}
