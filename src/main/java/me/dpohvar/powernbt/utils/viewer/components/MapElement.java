package me.dpohvar.powernbt.utils.viewer.components;

import me.dpohvar.powernbt.utils.query.KeySelector;
import me.dpohvar.powernbt.utils.viewer.ContainerControl;
import me.dpohvar.powernbt.utils.viewer.DisplayValueHelper;
import me.dpohvar.powernbt.utils.viewer.EventBuilder;
import me.dpohvar.powernbt.utils.viewer.ViewerStyle;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.Map;

public class MapElement implements Element {

    private final TextComponent component;

    public MapElement(ViewerStyle style, ContainerControl control, Map<?,?> map, int start, int end, int limitCol, int limitRow, boolean hex, boolean bin){
        this.component = getValuesComponent(style, control, map, start, end, limitCol, limitRow, hex, bin);
    }

    private static TextComponent getValuesComponent(ViewerStyle style, ContainerControl control, Map<?,?> map, int start, int end, int limitCol, int limitRow, boolean hex, boolean bin){
        int toEnd = end;
        if (start == 0 && toEnd == 0) toEnd = limitCol;
        ArrayList<Map.Entry<?, ?>> entries = new ArrayList<>(map.entrySet());
        if (entries.size() == 0) {
            return new TextComponent("empty");
        }
        TextComponent component = new TextComponent("");

        boolean readonly = control.isReadonly();
        for (int i = start; i < Math.min(toEnd, map.size()); i++) {
            Map.Entry<?, ?> entry = entries.get(i);
            Object mapValue = entry.getValue();
            Object key = entry.getKey();
            if (key instanceof String mapKey) {
                KeySelector selector = new KeySelector(mapKey);
                if (readonly) {
                    StringBuilder builder = new StringBuilder();
                    if (i != start) builder.append("\n").append(ChatColor.RESET);
                    builder.append(style.getColorByValue(mapValue)).append(selector).append(ChatColor.RESET).append(": ");
                    component.addExtra(builder.toString());
                } else {
                    String mapValueType = mapValue == null ? "null" : mapValue.getClass().getSimpleName();
                    InteractiveElement keyNameElement = new InteractiveElement(
                            style.getColorByValue(mapValue),
                            selector.toString(),
                            EventBuilder.runNbt(control.getSelector() + " " + control.getAccessQuery().add(selector), false),
                            EventBuilder.popup("type "+ mapValueType+ "\nselect " + mapKey),
                            null
                    );
                    if (i != start) component.addExtra("\n");
                    component.addExtra(keyNameElement.getComponent());
                    component.addExtra(": ");
                }

            } else {
                StringBuilder builder = new StringBuilder();
                if (i != start) builder.append("\n");
                builder.append(style.getColorByValue(mapValue)).append("<unknown-key>").append(ChatColor.RESET).append(": ");
                component.addExtra(builder.toString());
            }
            // VALUE
            String shortValue = DisplayValueHelper.getShortValue(style, mapValue, limitRow, hex, bin);
            if (ChatColor.stripColor(shortValue).isEmpty()) shortValue+="     ";
            if (readonly || (!(key instanceof String mapKey))) {
                component.addExtra(shortValue);
            } else {
                KeySelector selector = new KeySelector(mapKey);
                InteractiveElement ValueElement = new InteractiveElement(
                        null,
                        shortValue,
                        EventBuilder.suggestNbt(control.getSelector() + " " + control.getAccessQuery().add(selector) + " = ", false),
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
