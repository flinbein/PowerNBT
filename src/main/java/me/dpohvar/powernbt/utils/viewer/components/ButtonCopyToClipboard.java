package me.dpohvar.powernbt.utils.viewer.components;

import me.dpohvar.powernbt.nbt.NBTContainer;
import me.dpohvar.powernbt.utils.viewer.ContainerControl;
import me.dpohvar.powernbt.utils.viewer.EventBuilder;
import me.dpohvar.powernbt.utils.viewer.ViewerStyle;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;

public class ButtonCopyToClipboard extends InteractiveElement {

    static ButtonCopyToClipboard create(ViewerStyle style, ContainerControl control){
        ClickEvent click = getClickEvent(control);
        if (click == null) return null;
        return new ButtonCopyToClipboard(style, click, getHoverEvent(control));
    }

    private ButtonCopyToClipboard(ViewerStyle style, ClickEvent click, HoverEvent hover){
        super(style.getColor("buttons.control.copyToClipboard"), style.getIcon("buttons.control.copyToClipboard"), click, hover, null);
    }

    private static ClickEvent getClickEvent(ContainerControl control){
        if (control.hasValue()) return null;
        String clipboardValue = NBTContainer.parseValueToSelector(control.getValue(), 255);
        if (clipboardValue == null) return null;
        return EventBuilder.copy(clipboardValue);
    }

    private static HoverEvent getHoverEvent(ContainerControl control){
        if (control.hasValue()) return null;
        return EventBuilder.popup("copy value to clipboard");
    }
}
