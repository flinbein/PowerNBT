package me.dpohvar.powernbt.utils.viewer.components;

import me.dpohvar.powernbt.utils.query.IndexSelector;
import me.dpohvar.powernbt.utils.viewer.ContainerControl;
import me.dpohvar.powernbt.utils.viewer.DisplayValueHelper;
import me.dpohvar.powernbt.utils.viewer.EventBuilder;
import me.dpohvar.powernbt.utils.viewer.ViewerStyle;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;

public class ListElement implements Element {

    private final TextComponent component;

    public ListElement(ViewerStyle style, ContainerControl control, List<?> list, int start, int end, int limitCol, int limitRow, boolean hex, boolean bin){
        this.component = getValuesComponent(style, control, list, start, end, limitCol, limitRow, hex, bin);
    }

    private static TextComponent getValuesComponent(ViewerStyle style, ContainerControl control, List<?> list, int start, int end, int limitCol, int limitRow, boolean hex, boolean bin){
        int toEnd = end;
        if (start == 0 && toEnd == 0) toEnd = limitCol;
        if (list.size() == 0) {
            return new TextComponent("empty");
        }
        TextComponent component = new TextComponent("");

        boolean readonly = control.isReadonly();
        for (int i = start; i < Math.min(toEnd, list.size()); i++) {
            Object listValue = list.get(i);
            IndexSelector indexSelector = new IndexSelector(i);
            if (readonly) {
                StringBuilder builder = new StringBuilder();
                if (i != start) builder.append("\n").append(ChatColor.RESET);
                builder.append(style.getColorByValue(listValue)).append(indexSelector).append(": ");
                component.addExtra(builder.toString());
            } else {
                InteractiveElement keyNameElement = new InteractiveElement(
                        style.getColorByValue(listValue),
                        indexSelector.toString(),
                        EventBuilder.runNbt(control.getSelector() + " " + control.getAccessQuery().add(indexSelector), false),
                        EventBuilder.popup("select " + i),
                        null
                );
                if (i != start) component.addExtra("\n");
                component.addExtra(keyNameElement.getComponent());
                component.addExtra(": ");
            }
            // VALUE
            String shortValue = DisplayValueHelper.getShortValue(style, listValue, limitRow, hex, bin);
            if (ChatColor.stripColor(shortValue).isEmpty()) shortValue+="     ";
            if (readonly) {
                component.addExtra(shortValue);
            } else {
                InteractiveElement ValueElement = new InteractiveElement(
                        null,
                        shortValue,
                        EventBuilder.suggestNbt(control.getSelector() + " " + control.getAccessQuery().add(indexSelector) + " = ", false),
                        EventBuilder.popup("edit"),
                        null
                );
                component.addExtra(ValueElement.getComponent());
            }
        }
        return component;

    }

    @Override
    public BaseComponent getComponent() {
        return component;
    }
}
