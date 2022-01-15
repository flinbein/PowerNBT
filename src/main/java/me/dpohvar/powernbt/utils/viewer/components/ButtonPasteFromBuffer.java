package me.dpohvar.powernbt.utils.viewer.components;

import me.dpohvar.powernbt.utils.viewer.ContainerControl;
import me.dpohvar.powernbt.utils.viewer.EventBuilder;
import me.dpohvar.powernbt.utils.viewer.ViewerStyle;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;

public class ButtonPasteFromBuffer extends InteractiveElement {

    static ButtonPasteFromBuffer create(ViewerStyle style, ContainerControl control){
        ClickEvent click = getClickEvent(control);
        if (click == null) return null;
        return new ButtonPasteFromBuffer(style, click, getHoverEvent(control));
    }

    private ButtonPasteFromBuffer(ViewerStyle style, ClickEvent click, HoverEvent hover){
        super(style.getColor("buttons.control.pasteFromBuffer"), style.getIcon("buttons.control.pasteFromBuffer"), click, hover, null);
    }

    private static ClickEvent getClickEvent(ContainerControl control){
        if (control.isReadonly()) return null;
        return EventBuilder.runNbt(control.getSelectorWithQuery()+" paste", false);
    }

    private static HoverEvent getHoverEvent(ContainerControl control){
        if (control.isReadonly()) return null;
        return EventBuilder.popup("paste from buffer");
    }
}
