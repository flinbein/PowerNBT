package me.dpohvar.powernbt.utils;

import me.dpohvar.powernbt.PowerNBT;
import org.bukkit.Bukkit;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class Translator {

    private Map<String, String> tran = new HashMap<String, String>();

    public Translator(PowerNBT plugin, String locale) {
        if (locale.contains(File.separator)) throw new RuntimeException("locale name is not valid");
        if (locale.equals("system")) locale = System.getProperty("user.language");
        InputStream is = null;
        File file = new File(plugin.getLangFolder(), locale + ".yml");
        if (!file.exists()) {
            is = plugin.getResource(locale + ".yml");
            if (is == null) is = plugin.getResource("en.yml");
        } else {
            try {
                is = new FileInputStream(file);
            } catch (FileNotFoundException ignored) {
            }
        }
        try {
            Yaml yaml = new Yaml();
            Map<String, Object> map = (Map<String, Object>) yaml.load(is);
            Map<String, Object> vals = (Map<String, Object>) map.get("locale");
            for (Map.Entry<String, Object> e : vals.entrySet()) {
                tran.put(e.getKey(), e.getValue().toString());
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "Can not translate to "+locale, e);
        }
    }

    public String translate(String key, Object... values) {
        if (!tran.containsKey(key)) return "{" + key + "}";
        return String.format(tran.get(key), values);
    }

    public String translate(String key) {
        if (!tran.containsKey(key)) return "{" + key + "}";
        return tran.get(key);
    }
}
