package me.dpohvar.powernbt.completer;

import com.google.common.base.Strings;
import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.command.CommandNBT;
import me.dpohvar.powernbt.command.action.Argument;
import me.dpohvar.powernbt.nbt.NBTContainer;
import me.dpohvar.powernbt.nbt.NBTContainerValue;
import me.dpohvar.powernbt.nbt.NBTContainerVariable;
import me.dpohvar.powernbt.nbt.NBTType;
import me.dpohvar.powernbt.utils.Caller;
import me.dpohvar.powernbt.utils.NBTStaticViewer;
import me.dpohvar.powernbt.utils.StringParser;
import me.dpohvar.powernbt.utils.query.KeySelector;
import me.dpohvar.powernbt.utils.query.NBTQuery;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;

import static java.util.Comparator.comparingDouble;

public class CompleterNBT extends Completer {
    private final TypeCompleter typeCompleter;
    private final NBTQuery emptyQuery = new NBTQuery();

    public CompleterNBT() {
        super();
        typeCompleter = PowerNBT.plugin.getTypeCompleter();
    }

    @Override
    public void fillTabs(Caller caller, TabFormer former) throws Exception {
        String word = former.poll(); // object
        if (word.isEmpty()) {
            former.addIfStarts("buffer", "list", "compound", "byte[]", "int[]", "long[]", "debug", "file:", "gz:", "sch:");
            if (caller.getOwner() instanceof Entity) former.addIfStarts("block", "inventory","item","hand","hand:");
            if (caller.getOwner() instanceof Entity p && former.getQuery().startsWith("id")) {
                TreeSet<Entity> nearbyEntities = new TreeSet<>(comparingDouble(e -> e.getLocation().distance(p.getLocation())));
                nearbyEntities.addAll( p.getNearbyEntities(20, 20, 20) );
                var pow = (int) Math.ceil(Math.log10(nearbyEntities.size()));
                var i = 0;
                for (Entity entity : nearbyEntities) {
                    var prefix = pow == 0 ? "" : "["+Strings.padStart(String.valueOf(i), pow, ' ')+"]";
                    former.addIfStarts("id" + prefix + entity.getEntityId() + "(\"" + ChatColor.stripColor(entity.getName()) + "\")");
                    i++;
                }
            }
            if (former.getQuery().startsWith("%")) {
                for (String s : caller.getVariables().keySet()) {
                    former.addIfStarts("%" + s);
                }
            } else if (former.getQuery().startsWith("@")) {
                for (OfflinePlayer f : Bukkit.getOfflinePlayers()) {
                    former.addIfStarts("@" + f.getName());
                }
            } else if (former.getQuery().startsWith("*")) {
                for (Player f : Bukkit.getOnlinePlayers()) {
                    former.addIfStarts("*" + f.getName());
                }
            } else if (former.getQuery().startsWith("$$")) {
                File folder = PowerNBT.plugin.getNBTFilesFolder();
                File[] files = folder.listFiles();
                if (files != null) for (File f : files) {
                    String n = f.getName();
                    if (!n.endsWith(".nbtz")) continue;
                    former.addIfStarts("$$" + n.substring(0, n.length() - 5));
                }
            } else if (former.getQuery().startsWith("sch:")||former.getQuery().startsWith("schematic:")) {
                File folder = new File("plugins/WorldEdit/schematics");
                File[] files = folder.listFiles();
                if (files != null) for (File f : files) {
                    String n = f.getName();
                    if (!n.endsWith(".schematic")) continue;
                    former.addIfStarts(former.getQuery().split(":")[0] +":"+ n.substring(0, n.length() - 10));
                }
            } else if (former.getQuery().startsWith("$")) {
                File folder = PowerNBT.plugin.getNBTFilesFolder();
                File[] files = folder.listFiles();
                if (files != null) for (File f : files) {
                    String n = f.getName();
                    if (!n.endsWith(".nbt")) continue;
                    former.addIfStarts("$" + n.substring(0, n.length() - 4));
                }
            }

            return;
        }
        if (word.equals("debug")) {
            former.addIfStarts("on", "off", "toggle");
            return;
        }
        NBTContainer container = null;
        boolean future = true;
        try {
            container = Argument.getContainer(caller, word, null);
        } catch (Throwable ignored) {
            future = false;
        }
        String containerRequest = word;
        word = former.poll(); // query or type or command
        if (container == null && !future) return;
        Object buffer = caller.readTag();
        if (word.isEmpty()) {
            if (container == null) {
                if (containerRequest.matches("#-?[0-9a-fA-F]+") || Argument.colors.containsKey(containerRequest)) {
                    former.addIfStarts("int", "byte", "short", "long");
                } else if (containerRequest.matches("-?[0-9]+(.[0-9]*)?")) {
                    former.addIfStarts("int", "byte", "short", "long", "float", "double");
                } else if (containerRequest.matches("NaN|-?Infinity")) {
                    former.addIfStarts("float", "double");
                } else if (containerRequest.matches("\\[((-?[0-9]+|#-?[0-9a-fA-F]+)(,(?!\\])|(?=\\])))*\\]")) {
                    former.addIfStarts("byte[]", "int[]");
                }
            } else {
                completeTag(container, former);
                former.addIfStarts("rem");
                former.addIfStarts("copy", "as", "view", "swap");
                if (!(container instanceof NBTContainerValue)) former.addIfStarts( "=", "+=", "ren");
                if (caller.readTag() != null) former.addIfStarts("paste");
            }
            if (container instanceof NBTContainerVariable) former.addIfStarts("set");
            if (container instanceof NBTContainerValue ctValue && ctValue.getObject() instanceof String) {
                former.addIfStarts("json");
            }
            if (containerRequest.startsWith("{") && containerRequest.endsWith("}") || containerRequest.startsWith("[") && containerRequest.endsWith("]")) {
                former.addIfStarts("json", "mojangson");
            }
            return;
        }
        NBTQuery query = null;
        Object base = null;
        if (!CommandNBT.specialTokens.contains(word)) {
            //* ~~~ word => query or type ~~~
            container = Argument.getContainer(caller, containerRequest, word);
            query = NBTQuery.fromString(word);
            word = former.poll(); // command;
        }
        try {
            base = container.getCustomTag(query);
        } catch (Throwable ignored) {
        }
        if (word.isEmpty()) {
            if (container == null) {
                former.addIfStarts("=", "rem", "ren", "paste","+=", "cut", "set", "as", "view", "swap", ">", ">>", "<<");
                if (buffer != null) former.addIfStarts("paste");
            } else {
                if (container instanceof NBTContainerVariable) former.addIfStarts("set");
                if (base != null) {
                    former.addIfStarts("rem", "+=", "copy", "cut", "ren", "view", ">", ">>");
                    if (!(container instanceof NBTContainerValue)) former.addIfStarts("+=");
                    if (buffer != null) former.addIfStarts("paste");
                    if (base instanceof Collection) former.addIfStarts("ins");
                    if (base.getClass().isArray()) former.addIfStarts("ins");
                }
                if (!(container instanceof NBTContainerValue)) former.addIfStarts("=", "swap", "paste", "<<");
                former.addIfStarts("as");
            }
            return;
        }
        String index = "";
        if (matches(word, "rename", "ren")) {
            if (base == null) return;
            // TODO GET NAME
            String t = "";
            String w = StringParser.wrap(t);
            if (t.equals(w)) former.addIfStarts(t);
            else former.addIfStarts('"' + StringParser.wrap("") + '"');
            return;
        } else if (matches(word, "insert", "ins")) {
            if (base == null) return;
            int size;
            if(base instanceof Collection list) size = list.size();
            else if(base instanceof byte[] arr) size = arr.length;
            else if(base instanceof int[] arr) size = arr.length;
            else if(base instanceof long[] arr) size = arr.length;
            else return;
            index = former.poll();
            if (index.isEmpty()){
                for(int i=0;i<=size;i++){
                    former.addIfStarts(Integer.toString(i));
                }
                return;
            }
        }
        String oper = word;
        if (matches(oper, "=","insert", "ins","+=","add", "<", "set", "select", "swap", "<>", ">", ">>", "<<")) {
            if (matches(oper, "set", "select") && !(container instanceof NBTContainerVariable)) return;
            word = former.poll();
            if (word.isEmpty()) {
                if (base != null && former.getQuery().isEmpty()) {
                    if (matches(oper,"=","<","set","select")){
                        NBTType type = NBTType.fromValue(base);
                        switch (type) {
                            case BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, BYTEARRAY, INTARRAY -> {
                                String s = NBTStaticViewer.getShortValue(base, false, false);
                                former.add(s);
                                return;
                            }
                            case STRING -> {
                                String t = NBTStaticViewer.getShortValue(base, false, false);
                                String w = StringParser.wrap(t);
                                former.add("\"" + w + "\"");
                                return;
                            }
                        }
                    } else if (matches(oper,"insert","ins")){
                        try{
                            int ind = Integer.parseInt(index);
                            if (base instanceof byte[] arr){
                                former.add("" + arr[ind]);
                            } else if (base instanceof int[] arr){
                                former.add("" + arr[ind]);
                            } else if (base instanceof long[] arr){
                                former.add("" + arr[ind]);
                            } else if (base instanceof Collection<?> list){
                                Object b = new ArrayList<Object>(list).get(ind);
                                NBTType type = NBTType.fromValue(b);
                                switch (type) {
                                    case BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, BYTEARRAY, INTARRAY -> {
                                        String s = NBTStaticViewer.getShortValue(b, false, false);
                                        former.add(s);
                                        return;
                                    }
                                    case STRING -> {
                                        String t = NBTStaticViewer.getShortValue(b, false, false);
                                        String w = StringParser.wrap(t);
                                        former.add("\"" + w + "\"");
                                        return;
                                    }
                                }
                            }
                        }catch (Exception ignored){
                            return;
                        }
                    }
                }
                former.addIfStarts("me", "item", "buffer", "list", "compound", "byte[]", "int[]", "long[]");
                if (caller.getOwner() instanceof Entity) former.addIfStarts("block", "inventory","item","hand","hand:");
                if (caller.getOwner() instanceof Entity p && former.getQuery().startsWith("id")) {
                    TreeSet<Entity> nearbyEntities = new TreeSet<>(comparingDouble(e -> e.getLocation().distance(p.getLocation())));
                    nearbyEntities.addAll( p.getNearbyEntities(20, 20, 20) );
                    var pow = (int) Math.ceil(Math.log10(nearbyEntities.size()));
                    var i = 0;
                    for (Entity entity : nearbyEntities) {
                        var prefix = pow == 0 ? "" : "["+Strings.padStart(String.valueOf(i), pow, ' ')+"]";
                        former.addIfStarts("id" + prefix + entity.getEntityId() + "(\"" + ChatColor.stripColor(entity.getName()) + "\")");
                        i++;
                    }
                }
                if (former.getQuery().startsWith("%")) {
                    for (String s : caller.getVariables().keySet()) {
                        former.addIfStarts("%" + s);
                    }
                } else if (former.getQuery().startsWith("@")) {
                    for (OfflinePlayer f : Bukkit.getOfflinePlayers()) {
                        former.addIfStarts("@" + f.getName());
                    }
                } else if (former.getQuery().startsWith("*")) {
                    for (Player f : Bukkit.getOnlinePlayers()) {
                        former.addIfStarts("*" + f.getName());
                    }
                } else if (former.getQuery().startsWith("$$")) {
                    File folder = PowerNBT.plugin.getNBTFilesFolder();
                    File[] files = folder.listFiles();
                    if (files != null) for (File f : files) {
                        String n = f.getName();
                        if (!n.endsWith(".nbtz")) continue;
                        former.addIfStarts("$$" + n.substring(0, n.length() - 5));
                    }
                } else if (former.getQuery().startsWith("sch:")||former.getQuery().startsWith("schematic:")) {
                    File folder = new File("plugins/WorldEdit/schematics");
                    File[] files = folder.listFiles();
                    if (files != null) for (File f : files) {
                        String n = f.getName();
                        if (!n.endsWith(".schematic")) continue;
                        former.addIfStarts(former.getQuery().split(":")[0] +":"+ n.substring(0, n.length() - 10));
                    }
                } else if (former.getQuery().startsWith("$")) {
                    File folder = PowerNBT.plugin.getNBTFilesFolder();
                    if (folder.isDirectory()) for (File f : folder.listFiles()) {
                        String n = f.getName();
                        if (!n.endsWith(".nbt")) continue;
                        former.addIfStarts("$" + n.substring(0, n.length() - 4));
                    }
                }
                return;
            }


            NBTContainer container2 = null;
            boolean future2 = true;
            try {
                container2 = Argument.getContainer(caller, word, null);
            } catch (Throwable ignored) {
                future2 = false;
            }
            String val2 = word;
            if (val2.equals("this") || val2.equals("self")) container2 = container;
            word = former.poll(); // query or type or command
            if (container2 == null && !future2) return;
            if (word.isEmpty()) {
                if (container2 == null) {
                    if (val2.matches("#-?[0-9a-fA-F]+") || Argument.colors.containsKey(val2)) {
                        former.addIfStarts("int", "byte", "short", "long");
                    } else if (val2.matches("-?[0-9]+(.[0-9]*)?")) {
                        former.addIfStarts("int", "byte", "short", "long", "float", "double");
                    } else if (containerRequest.matches("NaN|-?Infinity")) {
                        former.addIfStarts("float", "double");
                    } else if (val2.matches("\\[((-?[0-9]+|#-?[0-9a-fA-F]+)(,(?!\\])|(?=\\])))*\\]")) {
                        former.addIfStarts("byte[]", "int[]");
                    }
                } else {
                    completeTag(container2, former);
                }
            }
        }
    }


    private void completeTag(NBTContainer<?> container, TabFormer former) throws Exception {String query = former.getQuery();
        String[] els = query.split("\\.|(?=\\[)|(?<=#)|(?=#)");
        if (query.endsWith("..")) {
            els[els.length - 1] = "";
            StringBuilder stringBuilder = new StringBuilder();
            for (String el : els) {
                if (el.isEmpty()) continue;
                if (!el.startsWith("[") && !el.equals("#")) {
                    if (!stringBuilder.toString().endsWith("#") && !stringBuilder.isEmpty()) {
                        stringBuilder.append(".");
                    }
                }
                stringBuilder.append(el);
            }
            former.add(stringBuilder.toString());
            return;
        }
        if (!query.endsWith(".") && (query.isEmpty() || els.length == 1)) {
            Object base = container.getCustomTag();
            if (base != null) {
                if (base instanceof Map map && !(container instanceof NBTContainerValue)) {
                    for (Object key : map.keySet()) {
                        if (!(key instanceof String s)) continue;
                        KeySelector selector = new KeySelector(s);
                        former.addIfHas(selector.toString());
                    }
                } else if (base instanceof Collection list && !(container instanceof NBTContainerValue)) {
                    for (int i = 0; i < list.size(); i++) {
                        former.addIfStarts("[" + i + "]");
                    }
                } else if (base instanceof byte[] arr && !(container instanceof NBTContainerValue)) {
                    for (int i = 0; i < arr.length; i++) former.addIfStarts("[" + i + "]");
                } else if (base instanceof int[] arr && !(container instanceof NBTContainerValue)) {
                    for (int i = 0; i < arr.length; i++) former.addIfStarts("[" + i + "]");
                } else if (base instanceof long[] arr && !(container instanceof NBTContainerValue)) {
                    for (int i = 0; i < arr.length; i++) former.addIfStarts("[" + i + "]");
                }
            }
            for (String type : container.getTypes()) {
                for (String s : typeCompleter.getNextKeys(type, emptyQuery)) {
                    KeySelector selector = new KeySelector(s);
                    former.addIfHas(selector.toString());
                }
            }
            return;
        }
        String qu = els[els.length - 1];
        String option = query.substring(0, query.length() - qu.length());
        if (query.endsWith(".")) {
            option = query.substring(0, query.length() - 1);
            qu = "";
        } else if (option.endsWith(".")) {
            option = option.substring(0, option.length() - 1);
        }
        NBTQuery q = NBTQuery.fromString(option);
        Object base = container.getCustomTag(q);
        if (base != null) {
            if (base instanceof Map compound) {
                for (Object key : compound.keySet()) {
                    if (!(key instanceof String s)) continue;
                    KeySelector selector = new KeySelector(s);
                    String delimiter = option.endsWith("#") ? "" : ".";
                    if (s.toUpperCase().contains(qu.toUpperCase())) former.add(option + delimiter + selector);
                }
            } else if (base instanceof Collection list) {
                for (int i = 0; i < list.size(); i++) {
                    String s = "[" + i + "]";
                    if (s.toUpperCase().startsWith(qu.toUpperCase())) former.add(option + s);
                }
            } else if (base instanceof byte[] arr) {
                for (int i = 0; i < arr.length; i++) {
                    String s = "[" + i + "]";
                    if (s.toUpperCase().startsWith(qu.toUpperCase())) former.add(option + s);
                }
            } else if (base instanceof int[] arr) {
                for (int i = 0; i < arr.length; i++) {
                    String s = "[" + i + "]";
                    if (s.toUpperCase().startsWith(qu.toUpperCase())) former.add(option + s);
                }
            } else if (base instanceof long[] arr) {
                for (int i = 0; i < arr.length; i++) {
                    String s = "[" + i + "]";
                    if (s.toUpperCase().startsWith(qu.toUpperCase())) former.add(option + s);
                }
            } else if (base instanceof String) {
                former.add(option + "#");
            }
        }
        for (String type : container.getTypes()) {
            for (String s : typeCompleter.getNextKeys(type, q)) {
                if (s.toUpperCase().startsWith(qu.toUpperCase())) {
                    String u = StringParser.wrap(s);
                    if (!u.equals(s) || u.contains(".")) s = '\"' + u + '\"';
                    if (!s.matches("\\[[0-9]*\\]")) s = "." + s;
                    former.add(option + s);
                }
            }
        }
    }

}










