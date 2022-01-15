package me.dpohvar.powernbt.utils.viewer.components;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class InteractiveElement implements Element {

    private final TextComponent component;

    public InteractiveElement(ChatColor color, String buttonText, ClickEvent clickEvent, HoverEvent hoverEvent, String insertion){
        component = new TextComponent(buttonText);
        if (color != null) component.setColor(color);
        if (clickEvent != null) component.setClickEvent(clickEvent);
        if (hoverEvent != null) component.setHoverEvent(hoverEvent);
        if (insertion != null) component.setInsertion(insertion);
    }

    @Override
    public BaseComponent getComponent() {
        return component;
    }
}
