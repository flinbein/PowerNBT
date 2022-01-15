package me.dpohvar.powernbt.utils.viewer.components;

import me.dpohvar.powernbt.utils.query.NBTQuery;
import me.dpohvar.powernbt.utils.query.QSelector;
import me.dpohvar.powernbt.utils.viewer.ContainerControl;
import me.dpohvar.powernbt.utils.viewer.EventBuilder;
import me.dpohvar.powernbt.utils.viewer.ViewerStyle;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;

public class NavbarElement implements Element {

    private final TextComponent component;

    public NavbarElement(ViewerStyle style, ContainerControl control){

        NBTQuery query = control.getAccessQuery();
        if (control.isReadonly()) {
            StringBuilder builder = new StringBuilder();
            Object value = control.getValue();
            builder.append(style.getColorByValue(value));
            String valueType = value == null ? "null" : value.getClass().getSimpleName();
            builder.append(valueType);

            if (query != null) {
                builder.append(" ");
                builder.append(style.getColor("disabledBreadcrumbs.query"));
                builder.append(query);
            }
            this.component = new TextComponent(builder.toString());
        } else {
            TextComponent component = new TextComponent("");
            String selector = control.getSelector();
            InteractiveElement rootElement = new InteractiveElement(
                    style.getColor("breadcrumbs.root"),
                    control.getContainer().getRootContainer().toString(),
                    EventBuilder.runNbt(selector, false),
                    EventBuilder.popup("select " + control.getContainer().getRootContainer()),
                    null
            );
            component.addExtra(rootElement.getComponent());
            component.addExtra(" ");

            QSelector lastStep = null;
            List<QSelector> steps = query.getSelectors();
            ChatColor delimiterColor = style.getColor("breadcrumbs.delimiter");
            for (int i = 0; i < steps.size(); i++) {
                QSelector step = steps.get(i);
                NBTQuery stepQuery = query.getSlice(0, i+1);

                String separator = step.getSeparator(lastStep);
                if (separator != null) component.addExtra(delimiterColor + separator);
                InteractiveElement interactiveElement = new InteractiveElement(
                        style.getColorMod("breadcrumbs.steps", i),
                        step.toString(),
                        EventBuilder.runNbt(selector + " " + stepQuery, false),
                        EventBuilder.popup("select " + step),
                        null
                );
                component.addExtra(interactiveElement.getComponent());
                lastStep = step;
            }

            if (steps.size() > 0) component.addExtra(" ");
            String nextSelector = steps.size() == 0 ? selector + " " : control.getSelectorWithQuery()+".";
            InteractiveElement nextSelectElement = new InteractiveElement(
                    style.getColorMod("breadcrumbs.steps", steps.size()),
                    "...",
                    EventBuilder.suggestNbt(nextSelector, false),
                    EventBuilder.popup("select next"),
                    null
            );
            component.addExtra(nextSelectElement.getComponent());

            this.component = component;
        }
    }

    @Override
    public BaseComponent getComponent() {
        return component;
    }
}
