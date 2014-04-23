package me.dpohvar.powernbt.command.action;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.completer.TypeCompleter;
import me.dpohvar.powernbt.nbt.*;
import me.dpohvar.powernbt.utils.Caller;
import me.dpohvar.powernbt.utils.NBTQuery;
import me.dpohvar.powernbt.utils.StringParser;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public class Argument {

    private static final NBTQuery emptyQuery = new NBTQuery();

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
            //noinspection deprecation
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
            if (s.startsWith("\"") && s.endsWith("\"") && s.length()>1 ) {
                s = StringParser.parse(s.substring(1,s.length()-1));
            }
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

        } else if (
                (object.startsWith("gzip:") && object.length() > 5)
                ||
                (object.startsWith("gz:") && object.length() > 3)
                ) {
            String s = object.substring(object.indexOf(':')+1);
            if (s.startsWith("\"") && s.endsWith("\"") && s.length()>1 ) {
                s = StringParser.parse(s.substring(1,s.length()-1));
            }
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
        } else if (object.matches("(-?[0-9]+):(-?[0-9]+):(-?[0-9]+):.*")) {
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
            File playerFile;
            try {
                UUID uuid = Bukkit.getOfflinePlayer(object.substring(1)).getUniqueId();
                File playerDir = new File(baseDir, "playerdata");
                playerFile = new File(playerDir, uuid+".dat");
            } catch (NoSuchMethodError ignored) { // no getUniqueId()
                File playerDir = new File(baseDir, "players");
                playerFile = new File(playerDir, object.substring(1) + ".dat");
            }
            return new NBTContainerFileGZip(playerFile);
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

    public void prepare(final Action action, final NBTContainer<?> paramContainer, final NBTQuery paramQuery) throws Exception {
        if (objectFuture.equals("*")) {
            if (!(caller.getOwner() instanceof Player)) {
                throw new RuntimeException(plugin.translate("error_noplayer"));
            }
            caller.send(plugin.translate("request_select"));
            caller.hold(this,action);
        } else if (objectFuture.equals("self") || objectFuture.equals("this")) {
            if (paramContainer == null) throw new RuntimeException(plugin.translate("error_undefinedself"));
            this.container = paramContainer;
            this.query = NBTQuery.fromString(queryFuture);
            action.execute();
        } else if (objectFuture.equals("hand") || objectFuture.equals("h")) {
            if (!(caller.getOwner() instanceof Player)) throw new RuntimeException(plugin.translate("error_noplayer"));
            Player p = (Player) caller.getOwner();
            NBTContainerEntity player = new NBTContainerEntity(p);
            int pslot = p.getInventory().getHeldItemSlot();
            int ind = 0;
            int result = -1;
            NBTTagList inventory = ((NBTTagCompound)player.getCustomTag()).getList("Inventory");
            for(NBTBase bt: inventory){
                NBTTagCompound ct = (NBTTagCompound) bt;
                if( ct.getByte("Slot") == pslot ){
                    result = ind;
                    break;
                }
                ind++;
            }
            if (result == -1) throw new RuntimeException(plugin.translate("error_null"));
            NBTQuery q = new NBTQuery("Inventory",result);
            this.container = new NBTContainerComplex(player,q);
            this.query = NBTQuery.fromString(queryFuture);
            action.execute();
        } else if (objectFuture.matches("-?[0-9]*(.[0-9]*)?")) {
            if (paramContainer == null){
                throw new RuntimeException(plugin.translate("error_undefinedtype", objectFuture));
            }
            NBTType type = NBTType.fromBase(paramQuery.get(paramContainer.getCustomTag()));
            if (type == NBTType.END && paramQuery != null) {
                List<Object> q = paramQuery.getValues();
                if (!q.isEmpty()) {
                    q.remove(q.size() - 1);
                }
                TypeCompleter comp = plugin.getTypeCompleter();
                for (String name : paramContainer.getTypes()) {
                    NBTType t = comp.getType(name, paramQuery);
                    if (t != null) {
                        type = t;
                        break;
                    }
                }
            }
            if (type == NBTType.BYTEARRAY) type = NBTType.BYTE;
            else if (type == NBTType.INTARRAY) type = NBTType.INT;
            if (type == NBTType.END && paramQuery!=null) {
                NBTQuery parent = paramQuery.getParent();
                if (parent != null) {
                    NBTBase bx = parent.get(paramContainer.getCustomTag());
                    if(bx instanceof NBTTagList){
                        type = NBTType.fromByte(((NBTTagList) bx).getSubTypeId());
                    }
                }
            }

            this.container = new NBTContainerBase(type.parse(objectFuture));
            this.query = emptyQuery;
            action.execute();
        } else if (objectFuture.matches("\\[((-?[0-9]+|#-?[0-9a-fA-F]+)(,(?!\\])|(?=\\])))*\\]")) {
            if (paramContainer == null)
                throw new RuntimeException(plugin.translate("error_undefinedtype", objectFuture));
            NBTType type = NBTType.fromBase(paramQuery.get(paramContainer.getCustomTag()));
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

    public void select(NBTContainer container) {
        this.container = container;
        this.query = NBTQuery.fromString(queryFuture);
    }

}
