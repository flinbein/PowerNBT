package me.dpohvar.powernbt.utils.viewer;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ViewerStyle {
    private ConfigurationSection colorConfig;
    private ConfigurationSection iconConfig;
    private final ChatColor defaultColor = ChatColor.WHITE;
    private final Map<String, ChatColor> colors = new HashMap<>();
    private final Map<String, List<ChatColor>> colorLists = new HashMap<>();
    private final Map<String, String> icons = new HashMap<>();

    public ViewerStyle(ConfigurationSection colorConfig, ConfigurationSection iconConfig){
        this.colorConfig = colorConfig;
        this.iconConfig = iconConfig;
    }

    public ChatColor getColor(String key){
        if (colors.containsKey(key)) return colors.get(key);
        String colorDescription = colorConfig.getString(key);
        ChatColor color;
        if (colorDescription == null) color = defaultColor;
        else color = ChatColor.of(colorDescription);
        colors.put(key, color);
        return color;
    }

    public ChatColor getColorMod(String key, int index){
        if (colorLists.containsKey(key)) {
            List<ChatColor> colors = colorLists.get(key);
            if (colors.size() == 0) return defaultColor;
            return colors.get(index % colors.size());
        }
        List<String> colorDescriptions = colorConfig.getStringList(key);
        List<ChatColor> colors = colorDescriptions.stream().map(colorDescription -> {
            if (colorDescription == null) return defaultColor;
            return ChatColor.of(colorDescription);
        }).toList();
        this.colorLists.put(key, colors);
        if (colors.size() == 0) return defaultColor;
        return colors.get(index % colors.size());
    }

    public String getIcon(String key){
        if (icons.containsKey(key)) return icons.get(key);
        String icon = iconConfig.getString(key);
        if (icon == null) icon = "?";
        icons.put(key, icon);
        return icon;
    }

    private @NotNull String translateColor(@NotNull String colorDescription){
        String pattern = colorDescription.replace('#', 'x').chars().mapToObj(c -> "&" + (char) c).collect(Collectors.joining());
        return ChatColor.translateAlternateColorCodes('&', pattern);
    } //

    public ChatColor getColorByValue(Object value){
        if (value == null) return getColor("types.null");
        if (value instanceof Boolean) return getColor("types.bool");
        if (value instanceof Character) return getColor("types.char");
        if (value instanceof Byte) return getColor("types.byte");
        if (value instanceof Short) return getColor("types.short");
        if (value instanceof Integer) return getColor("types.int");
        if (value instanceof Long) return getColor("types.long");
        if (value instanceof Float) return getColor("types.float");
        if (value instanceof Double) return getColor("types.double");
        if (value instanceof String) return getColor("types.string");
        if (value instanceof byte[]) return getColor("types.byte[]");
        if (value instanceof int[]) return getColor("types.int[]");
        if (value instanceof long[]) return getColor("types.long[]");
        if (value instanceof Map) return getColor("types.map");
        if (value instanceof Collection) return getColor("types.list");
        return getColor("types.unknown");
    }
}
