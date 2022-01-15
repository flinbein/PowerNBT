package me.dpohvar.powernbt.utils.viewer;

import me.dpohvar.powernbt.api.NBTManager;
import me.dpohvar.powernbt.exception.NBTTagNotFound;
import me.dpohvar.powernbt.nbt.NBTContainer;
import me.dpohvar.powernbt.utils.query.NBTQuery;
import me.dpohvar.powernbt.utils.viewer.components.FooterElement;
import me.dpohvar.powernbt.utils.viewer.components.ListElement;
import me.dpohvar.powernbt.utils.viewer.components.MapElement;
import me.dpohvar.powernbt.utils.viewer.components.NavbarElement;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Arrays;
import java.util.Map;

public class InteractiveViewer {

    private ViewerStyle style;
    private int colLimit;
    private int rowLimit;

    public InteractiveViewer(ViewerStyle viewerStyle, int rowLimit, int colLimit){
        this.style = viewerStyle;
        this.colLimit = colLimit;
        this.rowLimit = rowLimit;
    }

    public void setStyle(ViewerStyle viewerStyle) {
        this.style = viewerStyle;
    }

    public String getShortValue(Object base, boolean hex, boolean bin){
        return DisplayValueHelper.getShortValue(style, base, rowLimit, hex, bin);
    }

    public TextComponent getFullValue(final NBTContainer<?> container, final NBTQuery query, int start, int end, final boolean hex, final boolean bin) throws NBTTagNotFound {
        TextComponent result = new TextComponent();

        ContainerControl control;
        try {
            Object value = container.getCustomTag(query);
            control = new ContainerControl(container, query, value);
        } catch (NBTTagNotFound error) {
            control = new ContainerControl(container, query);
        }

        NavbarElement navbarElement = new NavbarElement(style, control);
        result.addExtra(navbarElement.getComponent());
        if (control.hasValue()) {
            Object value = control.getValue();
            result.addExtra(topSeparator());
            if (value instanceof Map map) {
                MapElement mapElement = new MapElement(style, control, map, start, end, colLimit, rowLimit, hex, bin);
                result.addExtra(mapElement.getComponent());
            } else if (value instanceof String s) {
                result.addExtra(DisplayValueHelper.getStringValue(style, s, start, end, colLimit*rowLimit, hex));
            } else {
                Object[] objects = NBTManager.convertToObjectArrayOrNull(value);
                if (objects != null) {
                    ListElement listElement = new ListElement(style, control, Arrays.asList(objects), start, end, colLimit, rowLimit, hex, bin);
                    result.addExtra(listElement.getComponent());
                } else {
                    result.addExtra(DisplayValueHelper.getShortValue(style, value, rowLimit, hex, bin));
                }
            }
        }
        result.addExtra(bottomSeparator());

        FooterElement footerElement = new FooterElement(style, control, colLimit, rowLimit, start, end, hex, bin);
        result.addExtra(footerElement.getComponent());

        return result;
    }

    private TextComponent topSeparator(){
        TextComponent component = new TextComponent("\n" + style.getIcon("lineTopSeparator") + "\n");
        component.setColor(style.getColor("lineTopSeparator"));
        return component;
    }

    private TextComponent bottomSeparator(){
        TextComponent component = new TextComponent("\n" + style.getIcon("lineBottomSeparator") + "\n");
        component.setColor(style.getColor("lineBottomSeparator"));
        return component;
    }

}
