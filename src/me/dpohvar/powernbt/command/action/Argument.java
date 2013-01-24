package me.dpohvar.powernbt.command.action;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.completer.TypeCompleter;
import me.dpohvar.powernbt.nbt.*;
import me.dpohvar.powernbt.utils.Caller;
import me.dpohvar.powernbt.utils.StringParser;
import me.dpohvar.powernbt.utils.TempListener;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public class Argument {

    static final NBTQuery emptyQuery = new NBTQuery();

    private final Caller caller;
    private NBTContainer container;
    private NBTQuery query = null;
    private String objectFuture = null;
    private String queryFuture = null;

    public static final Map<String, Integer> colors = new HashMap<String, Integer>();

    static {
        colors.put("black", 0x1E1B1B);
        colors.put("red", 0xb3312C);
        colors.put("green", 0x3B511A);
        colors.put("brown", 0x51310A);
        colors.put("blue", 0x253192);
        colors.put("purple", 0x7B2FBE);
        colors.put("cyan", 0x287697);
        colors.put("lightgray", 0xABABAB);
        colors.put("gray", 0x434343);
        colors.put("pink", 0xD88198);
        colors.put("lime", 0x41CC34);
        colors.put("lightgreen", 0x41CC34);
        colors.put("yellow", 0xDECF2A);
        colors.put("lightblue", 0x6689D3);
        colors.put("magenta", 0xC354CD);
        colors.put("orange", 0xEB8844);
        colors.put("white", 0xF0F0F0);
    }

    public NBTContainer getContainer() {
        return container;
    }

    public NBTQuery getQuery() {
        return query;
    }

    public boolean needPrepare() {
        return container == null;
    }

    public Argument(Caller caller, String object, String param) {
        this.caller = caller;
        this.container = getContainer(caller, object, param);
        if (container == null) {
            objectFuture = object;
            queryFuture = param;
        } else if (container instanceof NBTContainerBase) {
            NBTContainerBase c = (NBTContainerBase) container;
            byte t = c.getObject().getTypeId();
            if (t == 9 || t == 10) query = NBTQuery.fromString(param);
            else query = emptyQuery;
        } else {
            query = NBTQuery.fromString(param);
        }
    }

    public static NBTContainer getContainer(Caller caller, String object, String param) {
        if (object == null) throw new RuntimeException(plugin.translate("error_undefinedobject", ""));
        if (object.equals("me")) {
            if (!(caller.getOwner() instanceof Player)) throw new RuntimeException(plugin.translate("error_noplayer"));
            return new NBTContainerEntity((Player) caller.getOwner());
        } else if (object.equals("item") || object.equals("i")) {
            if (!(caller.getOwner() instanceof Player)) throw new RuntimeException(plugin.translate("error_noplayer"));
            return new NBTContainerItem(((Player) caller.getOwner()).getItemInHand());
        } else if (object.equals("inventory") || object.equals("inv")) {
            if (!(caller.getOwner() instanceof Player)) throw new RuntimeException(plugin.translate("error_noplayer"));
            return new NBTContainerComplex(
                    new NBTContainerEntity(((Player) caller.getOwner())),
                    new NBTQuery("Inventory")
            );
        } else if (object.startsWith("id")) {
            int id = Integer.parseInt(object.substring(2).replaceAll("\\(.*\\)", ""));
            for (World w : Bukkit.getWorlds())
                for (Entity e : w.getEntities())
                    if (e.getEntityId() == id) {
                        return new NBTContainerEntity(e);
                    }
            throw new RuntimeException(plugin.translate("error_noentity", id));
        } else if (object.equals("block") || object.equals("b")) {
            if (!(caller.getOwner() instanceof Player)) throw new RuntimeException(plugin.translate("error_noplayer"));
            return new NBTContainerBlock(((Player) caller.getOwner()).getTargetBlock(null, 20));
        } else if (object.equals("buffer") || object.equals("clipboard") || object.equals("c")) {
            return caller;
        } else if (object.startsWith("*") && object.length() > 1) {
            return new NBTContainerEntity(Bukkit.getPlayer(object.substring(1)));
        } else if (object.startsWith("%") && object.length() > 1) {
            return new NBTContainerVariable(caller, object.substring(1));
        } else if (object.startsWith("$$") && object.length() > 2) {
            return new NBTContainerFileCustom(object.substring(2));
        } else if (object.startsWith("$") && object.length() > 1) {
            String name = object.substring(1);
            if (name.contains(".") || name.contains(File.separator))
                throw new RuntimeException(plugin.translate("error_customfile", name));
            return new NBTContainerFile(new File(plugin.getNBTFilesFolder(), name + ".nbt"));
        } else if (colors.containsKey(object)) {
            return new NBTContainerBase(new NBTTagInt(colors.get(object)));
        } else if (object.startsWith("file:") && object.length() > 5) {
            String s = object.substring(5);
            if (s.startsWith("\"") && s.endsWith("\"")) s = StringParser.parse(s);
            try {
                File file = new File(s).getCanonicalFile();
                File folder = new File(".").getCanonicalFile();
                if (!file.toString().startsWith(folder.toString())) {
                    throw new RuntimeException(plugin.translate("error_accessfile", file.getName()));
                }
                return new NBTContainerFile(file);
            } catch (IOException e) {
                throw new RuntimeException("file " + s + " not found", e);
            }

        } else if (object.startsWith("gzip:") && object.length() > 5) {
            String s = object.substring(5);
            if (s.startsWith("\"") && s.endsWith("\"")) s = StringParser.parse(s);
            try {
                File file = new File(s).getCanonicalFile();
                File folder = new File(".").getCanonicalFile();
                if (!file.toString().startsWith(folder.toString())) {
                    throw new RuntimeException(plugin.translate("error_accessfile", file.getName()));
                }
                return new NBTContainerFileGZip(file);
            } catch (IOException e) {
                throw new RuntimeException("file " + s + " not found", e);
            }
        } else if (object.equals("compound") || object.equals("com")) {
            return new NBTContainerBase(new NBTTagCompound());
        } else if (object.equals("list")) {
            return new NBTContainerBase(new NBTTagList());
        } else if (object.equals("on") || object.equals("true")) {
            return new NBTContainerBase(new NBTTagByte((byte) 1));
        } else if (object.equals("off") || object.equals("false")) {
            return new NBTContainerBase(new NBTTagByte((byte) 0));
        } else if (object.equals("int[]")) {
            return new NBTContainerBase(new NBTTagIntArray());
        } else if (object.equals("byte[]")) {
            return new NBTContainerBase(new NBTTagByteArray());
        } else if (object.matches("(-?[0-9]+):(-?[0-9]+):(-?[0-9]+)(:.*)?")) {
            String[] t = object.split(":");
            int x = Integer.parseInt(t[0]);
            int y = Integer.parseInt(t[1]);
            int z = Integer.parseInt(t[2]);
            World w;
            String ww = "";
            if (t.length >= 4) ww = t[3];
            if (ww.isEmpty() && caller.getOwner() instanceof Player) {
                w = ((Player) caller.getOwner()).getWorld();
            } else {
                w = Bukkit.getWorld(t[3]);
            }
            if (w == null) {
                throw new RuntimeException(PowerNBT.plugin.translate("error_noworld", ww));
            }
            return new NBTContainerBlock(w.getBlockAt(x, y, z));
        } else if (object.startsWith("@") && !object.contains(File.separator)) {
            File baseDir = (Bukkit.getWorlds().get(0)).getWorldFolder();
            File playerDir = new File(baseDir, "players");
            File file = new File(playerDir, object.substring(1) + ".dat");
            return new NBTContainerFileGZip(file);
        } else if (object.startsWith("\"") && object.endsWith("\"")) {
            String s = StringParser.parse(object.substring(1, object.length() - 1));
            NBTType type = NBTType.STRING;
            if (param != null) type = NBTType.fromString(param);
            return new NBTContainerBase(type.parse(s));
        } else if (object.matches("#-?[0-9a-fA-F]+")) {
            Long l = Long.parseLong(object.substring(1), 16);
            String s = l.toString();
            NBTType type = NBTType.INT;
            if (param != null) type = NBTType.fromString(param);
            return new NBTContainerBase(type.parse(s));
        } else if (object.matches("\\[((-?[0-9]+|#-?[0-9a-fA-F]+)(,(?!\\])|(?=\\])))*\\]")) {
            if (param == null) {
                return null;
            } else {
                NBTType type = NBTType.fromString(param);
                if (type == NBTType.BYTE) type = NBTType.BYTEARRAY;
                else if (type == NBTType.INT) type = NBTType.INTARRAY;
                return new NBTContainerBase(type.parse(object));
            }
        } else if (object.matches("-?[0-9]+(.[0-9]*)?")) {
            if (param == null) {
                return null;
            } else {
                NBTType type = NBTType.fromString(param);
                if(type.equals(NBTType.BYTEARRAY)) type = NBTType.BYTE;
                else if(type.equals(NBTType.INTARRAY)) type = NBTType.INT;
                return new NBTContainerBase(type.parse(object));
            }
        } else if (object.equals("*") || object.equals("self") || object.equals("this")) {
            return null;
        }
        throw new RuntimeException(plugin.translate("error_undefinedobject", object));
    }

    public void prepare(final Action action, final NBTContainer paramContainer, final NBTQuery paramQuery) {
        if (objectFuture.equals("*")) {
            Player tp = null;
            if (caller.getOwner() instanceof Player) tp = (Player) caller.getOwner();
            final Player p = tp;
            caller.send(plugin.translate("request_select"));
            caller.unregisterListener();
            caller.setListener(new TempListener() {

                @EventHandler
                public void block(PlayerInteractEvent event) {
                    try {
                        if (!event.getPlayer().equals(p)) return;
                        if (!event.getAction().equals(org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK)) return;
                        Block b = event.getClickedBlock();
                        unregister();
                        event.setCancelled(true);
                        container = new NBTContainerBlock(b);
                        query = NBTQuery.fromString(queryFuture);
                        action.execute();
                    } catch (Throwable t) {
                        caller.handleException(t);
                    }
                }

                @EventHandler
                public void entity(PlayerInteractEntityEvent event) {
                    try {
                        if (!event.getPlayer().equals(p)) return;
                        Entity e = event.getRightClicked();
                        unregister();
                        event.setCancelled(true);
                        container = new NBTContainerEntity(e);
                        query = NBTQuery.fromString(queryFuture);
                        action.execute();
                    } catch (Throwable t) {
                        caller.handleException(t);
                    }
                }

                @EventHandler
                public void chat(AsyncPlayerChatEvent event) {
                    try {
                        if (!event.getPlayer().equals(p)) return;
                        String s = event.getMessage();
                        unregister();
                        event.setCancelled(true);
                        LinkedList<String> ll = new LinkedList<String>(plugin.getTokenizer().tokenize(s).values());
                        if (ll.size() > 2) throw new RuntimeException(plugin.translate("error_toomanyarguments"));
                        String p1 = ll.poll();
                        String p2 = ll.poll();
                        container = getContainer(caller, p1, p2);
                        if (container == null) {
                            objectFuture = p1;
                            queryFuture = p2;
                            prepare(action, paramContainer, paramQuery);
                        } else if (container instanceof NBTContainerBase) {
                            NBTContainerBase c = (NBTContainerBase) container;
                            byte t = c.getObject().getTypeId();

                            if (t == 9 || t == 10) {
                                if (p2 != null) container = new NBTContainerComplex(container, NBTQuery.fromString(p2));
                                query = NBTQuery.fromString(queryFuture);
                            } else query = emptyQuery;
                            action.execute();
                        } else {
                            if (p2 != null) container = new NBTContainerComplex(container, NBTQuery.fromString(p2));
                            query = NBTQuery.fromString(queryFuture);
                            action.execute();
                        }
                    } catch (Throwable t) {
                        caller.handleException(t);
                    }
                }

            }.register());
        } else if (objectFuture.equals("self") || objectFuture.equals("this")) {
            if (paramContainer == null) throw new RuntimeException(plugin.translate("error_undefinedself"));
            this.container = paramContainer;
            this.query = NBTQuery.fromString(queryFuture);
            action.execute();
        } else if (objectFuture.matches("-?[0-9]*(.[0-9]*)?")) {
            if (paramContainer == null)
                throw new RuntimeException(plugin.translate("error_undefinedtype", objectFuture));
            NBTType type = NBTType.fromBase(paramContainer.getTag(paramQuery));
            if (type == NBTType.END && paramQuery != null) {
                List<Object> q = paramQuery.getValues();
                if (!q.isEmpty()) {
                    q.remove(q.size() - 1);
                    NBTQuery nq = new NBTQuery(q);
                    NBTType nt = NBTType.fromBase(paramContainer.getTag(nq));
                }
                if (type == NBTType.END) {
                    TypeCompleter comp = plugin.getTypeCompleter();
                    for (String name : paramContainer.getTypes()) {
                        NBTType t = comp.getType(name, paramQuery);
                        if (t != null) {
                            type = t;
                            break;
                        }
                    }
                }
            }
            if (type == NBTType.BYTEARRAY) type = NBTType.BYTE;
            else if (type == NBTType.INTARRAY) type = NBTType.INT;

            this.container = new NBTContainerBase(type.parse(objectFuture));
            this.query = emptyQuery;
            action.execute();
        } else if (objectFuture.matches("\\[((-?[0-9]+|#-?[0-9a-fA-F]+)(,(?!\\])|(?=\\])))*\\]")) {
            if (paramContainer == null)
                throw new RuntimeException(plugin.translate("error_undefinedtype", objectFuture));
            NBTType type = NBTType.fromBase(paramContainer.getTag(paramQuery));
            if (type == NBTType.INT) type = NBTType.INTARRAY;
            else if (type == NBTType.BYTE) type = NBTType.BYTEARRAY;
            else if (type == null || type == NBTType.END) {
                TypeCompleter comp = plugin.getTypeCompleter();
                for (String name : paramContainer.getTypes()) {
                    NBTType t = comp.getType(name, paramQuery);
                    if (t != null) {
                        type = t;
                        break;
                    }
                }
            }
            this.container = new NBTContainerBase(type.parse(objectFuture));
            this.query = emptyQuery;
            action.execute();
        } else {
            throw new RuntimeException("future object type ignored");
        }
    }

}
