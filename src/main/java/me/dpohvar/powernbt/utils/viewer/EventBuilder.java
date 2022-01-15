package me.dpohvar.powernbt.utils.viewer;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class EventBuilder {

    public static ClickEvent runNbt(String powerNBTCommand, boolean silent){
        String command = "/powernbt:nbt" + (silent ? ". " : " ") + powerNBTCommand;
        return new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);
    }

    public static ClickEvent suggestNbt(String powerNBTCommand, boolean silent){
        String command = "/powernbt:nbt" + (silent ? ". " : " ") + powerNBTCommand;
        return new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command);
    }

    public static ClickEvent copy(String value){
        return new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, value);
    }

    public static ClickEvent url(String value){
        return new ClickEvent(ClickEvent.Action.OPEN_URL, value);
    }

    public static HoverEvent popup(String value){
        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(value));
    }
}
