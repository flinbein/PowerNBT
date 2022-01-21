package me.dpohvar.powernbt.utils.viewer.components;

import com.google.common.base.Strings;
import me.dpohvar.powernbt.api.NBTManager;
import me.dpohvar.powernbt.api.NBTManagerUtils;
import me.dpohvar.powernbt.utils.viewer.ContainerControl;
import me.dpohvar.powernbt.utils.viewer.EventBuilder;
import me.dpohvar.powernbt.utils.viewer.ViewerStyle;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.stream.Stream;

public class FooterElement implements Element {

    private final TextComponent component;

    public FooterElement(ViewerStyle style, ContainerControl control, int colLimit, int rowLimit, int start, int end, boolean hex, boolean bin){
        TextComponent result = new TextComponent("");

        String parentAccess = getParentAccess(start, end, hex, bin);
        ChatColor barColor = style.getColor("buttons.bar");
        Object value = control.getValue();

        // valueClass
        String valueTypeName = value == null ? "null" : value.getClass().getSimpleName();
        TextComponent typeNameCmp = new TextComponent(valueTypeName+" ");
        typeNameCmp.setColor(style.getColorByValue(value));
        result.addExtra(typeNameCmp);

        // buttons
        List<InteractiveElement> buttonElements = Stream.of(
                ButtonUpdate.create(style, control, parentAccess),
                ButtonSelectJson.create(style, control),
                ButtonCopyToBuffer.create(style, control),
                ButtonPasteFromBuffer.create(style, control),
                ButtonCopyToClipboard.create(style, control),
                ButtonEdit.create(style, control),
                ButtonRemove.create(style, control, null)
        ).filter(Objects::nonNull).toList();

        if (buttonElements.size() > 0) {
            result.addExtra(barColor+"[");
            boolean firstButton = true;
            for (InteractiveElement buttonElement : buttonElements) {
                if (!firstButton) result.addExtra(barColor+"|");
                firstButton = false;
                result.addExtra(buttonElement.getComponent());
            }
            result.addExtra(barColor+"] ");
        }

        // pages
        int size = -1;
        if (value instanceof Map map) size = map.size();
        else if (value instanceof String s) size = s.length();
        else if (value instanceof Collection c) size = c.size();
        else {
            Object[] objects = NBTManagerUtils.convertToObjectArrayOrNull(value);
            if (objects != null) size = objects.length;
        }
        if (size >= 0) {
            TextComponent paginatorCmp = new TextComponent("");
            if (start == 0 && end == 0) end = (value instanceof String) ? colLimit * rowLimit : colLimit;

            int pageSize = end - start;

            paginatorCmp.setColor(style.getColor("paginator.text"));

            TextComponent btnLeftComponent = new TextComponent(style.getIcon("paginator.prev"));
            btnLeftComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("navigate to previous page")));
            if (start > 0 && !control.isReadonly()) {
                btnLeftComponent.setColor(style.getColor("paginator.navActive"));
                btnLeftComponent.setBold(true);
                int pStart = start - pageSize;
                if (pStart < 0) pStart = 0;
                int pEnd = pStart + pageSize;
                //btnLeftComponent.setClickEvent(runOnClick(getViewCommand(selector, query, pStart, pEnd, hex, bin), false));
                String pageParentAccess = getParentAccess(pStart, pEnd, hex, bin);
                btnLeftComponent.setClickEvent(EventBuilder.runNbt(control.getSelectorWithQuery()+ " "+pageParentAccess, false));
            } else {
                btnLeftComponent.setColor(style.getColor("paginator.nav"));
            }

            if (start == 0 && end >= size) { // full listed
                result.addExtra("["+size+"]");
            } else if (start >= size) { // out of range
                StringBuilder builder = new StringBuilder();
                paginatorCmp.addExtra("[");
                paginatorCmp.addExtra(btnLeftComponent);

                TextComponent errorCmp = new TextComponent(" out of range "+size);
                errorCmp.setColor(style.getColor("paginator.errorText"));
                paginatorCmp.addExtra(errorCmp);
                paginatorCmp.addExtra(size + " ]");
            } else {
                var pow = (int) Math.ceil(Math.log10(size));
                String startText = pow == 0 ? String.valueOf(start) : Strings.padStart(String.valueOf(start), pow, '0');
                String endText = pow == 0 ? String.valueOf(end) : Strings.padStart(String.valueOf(end), pow, '0');

                paginatorCmp.addExtra("[");
                paginatorCmp.addExtra(btnLeftComponent);
                paginatorCmp.addExtra(" "+startText+"-"+endText+" ");

                TextComponent btnRightComponent = new TextComponent(style.getIcon("paginator.next"));
                btnRightComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("navigate to next page")));
                if (end < size) {
                    btnRightComponent.setColor(style.getColor("paginator.navActive"));
                    btnRightComponent.setBold(true);
                    int pStart = start + pageSize;
                    int pEnd = pStart + pageSize;
                    String pageParentAccess = getParentAccess(pStart, pEnd, hex, bin);
                    btnRightComponent.setClickEvent(EventBuilder.runNbt(control.getSelectorWithQuery()+ " "+pageParentAccess, false));
                } else {
                    btnRightComponent.setColor(style.getColor("paginator.nav"));
                }
                paginatorCmp.addExtra(btnRightComponent);
                paginatorCmp.addExtra(" / ");

                TextComponent btnTotalComponent = new TextComponent(String.valueOf(size));
                btnTotalComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("show all values")));
                String fullPageParentAccess = getParentAccess(0, Integer.MAX_VALUE, hex, bin);
                btnTotalComponent.setClickEvent(EventBuilder.runNbt(control.getSelectorWithQuery()+ " "+fullPageParentAccess, false));
                btnTotalComponent.setColor(style.getColor("paginator.navActive"));

                paginatorCmp.addExtra(btnTotalComponent);

                paginatorCmp.addExtra("]");


            }

            result.addExtra(paginatorCmp);
        }


        this.component = result;
    }

    String getParentAccess(int start, int end, boolean hex, boolean bin){
        List<String> list = new ArrayList<>();
        if (start == 0 && end == Integer.MAX_VALUE) {
            list.add("a");
        } else if (start != 0 || end != 0) {
            list.add(start+"-"+end);
        }
        if (hex) list.add("h");
        if (bin) list.add("b");
        return StringUtils.join(list,",");
    }

    @Override
    public BaseComponent getComponent() {
        return component;
    }
}
