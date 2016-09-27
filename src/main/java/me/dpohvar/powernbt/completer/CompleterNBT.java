package me.dpohvar.powernbt.completer;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.command.CommandNBT;
import me.dpohvar.powernbt.command.action.Argument;
import me.dpohvar.powernbt.nbt.*;
import me.dpohvar.powernbt.utils.Caller;
import me.dpohvar.powernbt.utils.NBTQuery;
import me.dpohvar.powernbt.utils.NBTViewer;
import me.dpohvar.powernbt.utils.StringParser;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;

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
            former.addIfStarts("buffer", "list", "compound", "byte[]", "int[]", "debug", "file:", "gz:", "sch:");
            if (caller.getOwner() instanceof Entity) former.addIfStarts("block", "inventory","hand","hand:");
            if (caller.getOwner() instanceof Entity && former.getQuery().startsWith("id")) {
                Player p = (Player) caller.getOwner();
                List<Entity> ents = p.getNearbyEntities(20, 20, 20);
                Location pl = p.getLocation();
                while (true) {
                    if (ents.isEmpty()) break;
                    Entity b = ents.get(0);
                    double l = pl.distance(b.getLocation());
                    for (Entity e : ents) {
                        double x = pl.distance(e.getLocation());
                        if (x < l) {
                            b = e;
                            l = x;
                        }
                    }
                    ents.remove(b);
                    former.addIfStarts("id" + b.getEntityId() + "(" + b.getType().getName() + ")");
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
        String val1 = word;
        word = former.poll(); // query or type or command
        if (container == null && !future) return;
        if (word.isEmpty()) {
            if (container == null) {
                if (val1.matches("#-?[0-9a-fA-F]+") || Argument.colors.containsKey(val1)) {
                    former.addIfStarts("int", "byte", "short", "long");
                } else if (val1.matches("-?[0-9]+(.[0-9]*)?")) {
                    former.addIfStarts("int", "byte", "short", "long", "float", "double");
                } else if (val1.matches("NaN|-?Infinity")) {
                    former.addIfStarts("float", "double");
                } else if (val1.matches("\\[((-?[0-9]+|#-?[0-9a-fA-F]+)(,(?!\\])|(?=\\])))*\\]")) {
                    former.addIfStarts("byte[]", "int[]");
                }
            } else {
                completeTag(container, former);
                former.addIfStarts("rem", "ren");
                former.addIfStarts("copy", "=", "as", "view", "swap","+=");
                if (caller.readTag() != null) former.addIfStarts("paste");
            }
            if (container instanceof NBTContainerVariable) former.addIfStarts("set");
            return;
        }
        NBTQuery query = null;
        NBTBase base = null;
        if (!CommandNBT.specialTokens.contains(word)) {
            //* ~~~ word => query or type ~~~
            container = Argument.getContainer(caller, val1, word);
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
                if (caller.readTag() != null) former.addIfStarts("paste");
            } else {
                if (container instanceof NBTContainerVariable) former.addIfStarts("set");
                if (base != null) {
                    former.addIfStarts("rem","+=", "copy", "cut", "ren", "view", ">", ">>");
                    if (caller.readTag() != null) former.addIfStarts("paste");
                    if ((base instanceof NBTTagNumericArray)||(base instanceof NBTTagList)) former.addIfStarts("ins");
                }
                former.addIfStarts("=", "as", "swap", "paste", "swap", "<<");
            }
            return;
        }
        String index = "";
        if (matches(word, "rename", "ren")) {
            if (base == null) return;
            String t = base.getName();
            String w = StringParser.wrap(t);
            if (t.equals(w)) former.addIfStarts(t);
            else former.addIfStarts('"' + StringParser.wrap(base.getName()) + '"');
            return;
        } else if (matches(word, "insert", "ins")) {
            if (base == null) return;
            int size;
            if(base instanceof NBTTagList) size = ((NBTTagList) base).size();
            else if(base instanceof NBTTagNumericArray) size = ((NBTTagNumericArray) base).size();
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
                        NBTType type = NBTType.fromBase(base);
                        switch (type) {
                            case BYTE:
                            case SHORT:
                            case INT:
                            case LONG:
                            case FLOAT:
                            case DOUBLE:
                            case BYTEARRAY:
                            case INTARRAY:
                                String s = NBTViewer.getShortValue(base, false);
                                former.add(s);
                                return;
                            case STRING:
                                String t = NBTViewer.getShortValue(base, false);
                                String w = StringParser.wrap(t);
                                former.add("\"" + w + "\"");
                                return;
                        }
                    } else if (matches(oper,"insert","ins")){
                        try{
                            int ind = Integer.parseInt(index);
                            if(base instanceof NBTTagNumericArray){
                                former.add("" + ((NBTTagNumericArray) base).get(ind));
                            } else if (base instanceof NBTTagList){
                                NBTBase b = ((NBTTagList) base).get(ind);
                                NBTType type = NBTType.fromBase(b);
                                switch (type) {
                                    case BYTE:
                                    case SHORT:
                                    case INT:
                                    case LONG:
                                    case FLOAT:
                                    case DOUBLE:
                                    case BYTEARRAY:
                                    case INTARRAY:
                                        String s = NBTViewer.getShortValue(b, false);
                                        former.add(s);
                                        return;
                                    case STRING:
                                        String t = NBTViewer.getShortValue(b, false);
                                        String w = StringParser.wrap(t);
                                        former.add("\"" + w + "\"");
                                        return;
                                }
                            }
                        }catch (Exception ignored){
                            return;
                        }
                    }
                }
                former.addIfStarts("me", "item", "buffer", "list", "compound", "byte[]", "int[]");
                if (caller.getOwner() instanceof Entity) former.addIfStarts("block", "inventory","hand","hand:");
                if (caller.getOwner() instanceof Entity && former.getQuery().startsWith("id")) {
                    Player p = (Player) caller.getOwner();
                    List<Entity> ents = p.getNearbyEntities(20, 20, 20);
                    Location pl = p.getLocation();
                    while (true) {
                        if (ents.isEmpty()) break;
                        Entity b = ents.get(0);
                        double l = pl.distance(b.getLocation());
                        for (Entity e : ents) {
                            double x = pl.distance(e.getLocation());
                            if (x < l) {
                                b = e;
                                l = x;
                            }
                        }
                        ents.remove(b);
                        former.addIfStarts("id" + b.getEntityId() + "(" + b.getType().getName() + ")");
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
                    } else if (val1.matches("NaN|-?Infinity")) {
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


    private void completeTag(NBTContainer<?> container, TabFormer former) throws Exception {
        String query = former.getQuery();
        String[] els = query.split("\\.|(?=\\[)");
        if (query.endsWith("..")) {
            els[els.length - 1] = "";
            String t = StringUtils.join(els, '.');
            former.add(t.substring(0, t.length() - 1));
            return;
        }
        if (!query.endsWith(".") && (query.isEmpty() || els.length == 1)) {
            NBTBase base = container.getCustomTag();
            if (base != null) {
                if (base instanceof NBTTagCompound) {
                    for (String s : ((NBTTagCompound) base).getHandleMap().keySet()) {
                        String u = StringParser.wrap(s);
                        if (!u.equals(s) || u.contains(".")) s = '\"' + u + '\"';
                        former.addIfHas(s);
                    }
                } else if (base instanceof NBTTagList) {
                    for (int i = 0; i < ((NBTTagList) base).size(); i++) {
                        former.addIfStarts("[" + i + "]");
                    }
                } else if (base instanceof NBTTagByteArray) {
                    for (int i = 0; i < ((NBTTagByteArray) base).size(); i++) {
                        former.addIfStarts("[" + i + "]");
                    }
                } else if (base instanceof NBTTagIntArray) {
                    for (int i = 0; i < ((NBTTagIntArray) base).size(); i++) {
                        former.addIfStarts("[" + i + "]");
                    }
                }
            }
            for (String type : container.getTypes()) {
                for (String s : typeCompleter.getNextKeys(type, emptyQuery)) {
                    String u = StringParser.wrap(s);
                    if (!u.equals(s) || u.contains(".")) s = '\"' + u + '\"';
                    former.addIfHas(s);
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
        NBTBase base = container.getCustomTag(q);
        if (base != null) {
            if (base instanceof NBTTagCompound) {
                for (String s : ((NBTTagCompound) base).getHandleMap().keySet()) {
                    String u = StringParser.wrap(s);
                    if (!u.equals(s) || u.contains(".")) s = '\"' + u + '\"';
                    if (s.toUpperCase().contains(qu.toUpperCase())) former.add(option + "." + s);
                }
            } else if (base instanceof NBTTagList) {
                for (int i = 0; i < ((NBTTagList) base).size(); i++) {
                    String s = "[" + i + "]";
                    if (s.toUpperCase().startsWith(qu.toUpperCase())) former.add(option + s);
                }
            } else if (base instanceof NBTTagByteArray) {
                for (int i = 0; i < ((NBTTagByteArray) base).size(); i++) {
                    String s = "[" + i + "]";
                    if (s.toUpperCase().startsWith(qu.toUpperCase())) former.add(option + s);
                }
            } else if (base instanceof NBTTagIntArray) {
                for (int i = 0; i < ((NBTTagIntArray) base).size(); i++) {
                    String s = "[" + i + "]";
                    if (s.toUpperCase().startsWith(qu.toUpperCase())) former.add(option + s);
                }
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










