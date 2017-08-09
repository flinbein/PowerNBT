package me.dpohvar.powernbt.command.action;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.completer.TypeCompleter;
import me.dpohvar.powernbt.nbt.*;
import me.dpohvar.powernbt.utils.Caller;
import me.dpohvar.powernbt.utils.NBTParser;
import me.dpohvar.powernbt.utils.NBTQuery;
import me.dpohvar.powernbt.utils.StringParser;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
        }
        if (object.equals("item") || object.equals("i")) {
            if (!(caller.getOwner() instanceof Player)) throw new RuntimeException(plugin.translate("error_noplayer"));
            return new NBTContainerItem(((Player) caller.getOwner()).getItemInHand());
        }
        if (object.equals("inventory") || object.equals("inv")) {
            if (!(caller.getOwner() instanceof Player)) throw new RuntimeException(plugin.translate("error_noplayer"));
            return new NBTContainerComplex(
                    new NBTContainerEntity(((Player) caller.getOwner())),
                    new NBTQuery("Inventory")
            );
        }
        if (object.startsWith("id")) {
            int id = Integer.parseInt(object.substring(2).replaceAll("\\(.*\\)", ""));
            for (World w : Bukkit.getWorlds())
                for (Entity e : w.getEntities())
                    if (e.getEntityId() == id) {
                        return new NBTContainerEntity(e);
                    }
            throw new RuntimeException(plugin.translate("error_noentity", id));
        }
        if (object.equals("block") || object.equals("b")) {
            if (!(caller.getOwner() instanceof Player)) throw new RuntimeException(plugin.translate("error_noplayer"));
            //noinspection deprecation
            return new NBTContainerBlock(((Player) caller.getOwner()).getTargetBlock((Set<Material>)null, 128));
        }
        if (object.equals("chunk")) {
            CommandSender owner = caller.getOwner();
            Chunk chunk = null;
            if (owner instanceof Entity) chunk = ((Entity) owner).getLocation().getChunk();
            if (owner instanceof BlockCommandSender) chunk = ((BlockCommandSender) owner).getBlock().getChunk();
            if (chunk == null) throw new RuntimeException(plugin.translate("error_noplayer"));
            //noinspection deprecation
            return new NBTContainerChunk(chunk);
        }
        if (object.equals("buffer") || object.equals("clipboard") || object.equals("c")) {
            return caller;
        }
        if (object.startsWith("*") && object.length() > 1 || object.startsWith("player:") && object.length() > 7) {
            String tokenText = object.substring( object.startsWith("*") ? 1 : 7);
            if (tokenText.startsWith("\"") && tokenText.endsWith("\"")) {
                tokenText = StringParser.parse(tokenText.substring(1,tokenText.length()-1)).trim();
            }
            Player player = Bukkit.getPlayer(tokenText);
            if (player == null) throw new RuntimeException(plugin.translate("error_playernotfound", tokenText));
            return new NBTContainerEntity(player);
        }
        if (object.startsWith("%") && object.length() > 1) {
            String tokenText = object.substring(1);
            if (tokenText.startsWith("\"") && tokenText.endsWith("\"") && tokenText.length()>1) {
                tokenText = StringParser.parse(tokenText.substring(1,tokenText.length()-1)).trim();
            }
            return new NBTContainerVariable(caller, tokenText);
        }
        if (object.startsWith("$$") && object.length() > 2) {
            String tokenText = object.substring(2);
            if (tokenText.startsWith("\"") && tokenText.endsWith("\"") && tokenText.length()>1) {
                tokenText = StringParser.parse(tokenText.substring(1,tokenText.length()-1)).trim();
            }
            return new NBTContainerFileCustom(tokenText);
        }
        if (object.startsWith("$") && object.length() > 1) {
            String tokenText = object.substring(1);
            if (tokenText.startsWith("\"") && tokenText.endsWith("\"") && tokenText.length()>1) {
                tokenText = StringParser.parse(tokenText.substring(1,tokenText.length()-1)).trim();
            }
            if (tokenText.contains(".") || tokenText.contains(File.separator))
                throw new RuntimeException(plugin.translate("error_customfile", tokenText));
            return new NBTContainerFile(new File(plugin.getNBTFilesFolder(), tokenText + ".nbt"));
        }
        if (colors.containsKey(object)) {
            return new NBTContainerBase(new NBTTagInt(colors.get(object)));
        }
        if (object.startsWith("file:") && object.length() > 5) {
            String tokenText = object.substring(5);
            if (tokenText.startsWith("\"") && tokenText.endsWith("\"") && tokenText.length()>1 ) {
                tokenText = StringParser.parse(tokenText.substring(1,tokenText.length()-1)).trim();
            }
            try {
                File file = new File(tokenText).getCanonicalFile();
                File folder = new File(".").getCanonicalFile();
                if (!file.toString().startsWith(folder.toString())) {
                    throw new RuntimeException(plugin.translate("error_accessfile", file.getName()));
                }
                return new NBTContainerFile(file);
            } catch (IOException e) {
                throw new RuntimeException("file " + tokenText + " not found", e);
            }

        }
        if ((object.startsWith("gzip:") && object.length() > 5)||(object.startsWith("gz:") && object.length() > 3)) {
            String tokenText = object.substring(object.indexOf(':')+1);
            if (tokenText.startsWith("\"") && tokenText.endsWith("\"") && tokenText.length()>1 ) {
                tokenText = StringParser.parse(tokenText.substring(1,tokenText.length()-1)).trim();
            }
            try {
                File file = new File(tokenText).getCanonicalFile();
                File folder = new File(".").getCanonicalFile();
                if (!file.toString().startsWith(folder.toString())) {
                    throw new RuntimeException(plugin.translate("error_accessfile", file.getName()));
                }
                return new NBTContainerFileGZip(file);
            } catch (IOException e) {
                throw new RuntimeException("file " + tokenText + " not found", e);
            }
        }
        if ((object.startsWith("schematic:") && object.length() > 10)||(object.startsWith("sch:") && object.length() > 3)) {
            String tokenText = object.substring(object.indexOf(':')+1);
            if (tokenText.startsWith("\"") && tokenText.endsWith("\"") && tokenText.length()>1 ) {
                tokenText = StringParser.parse(tokenText.substring(1,tokenText.length()-1)).trim();
            }
            try {
                File schematicFolder = new File("plugins/WorldEdit/schematics").getCanonicalFile();
                File file = new File(schematicFolder, tokenText + ".schematic").getCanonicalFile();
                if (!file.toString().startsWith(schematicFolder.toString())) {
                    throw new RuntimeException(plugin.translate("error_accessfile", file.getName()));
                }
                return new NBTContainerFileGZip(file);
            } catch (IOException e) {
                throw new RuntimeException("schematic file " + tokenText + " not found", e);
            }
        }
        if (object.equals("compound") || object.equals("com")) {
            return new NBTContainerBase(new NBTTagCompound());
        }
        if (object.equals("list")) {
            return new NBTContainerBase(new NBTTagList());
        }
        if (object.equals("on") || object.equals("true")) {
            return new NBTContainerBase(new NBTTagByte((byte) 1));
        }
        if (object.equals("off") || object.equals("false")) {
            return new NBTContainerBase(new NBTTagByte((byte) 0));
        }
        if (object.equals("int[]")) {
            return new NBTContainerBase(new NBTTagIntArray());
        }
        if (object.equals("byte[]")) {
            return new NBTContainerBase(new NBTTagByteArray());
        }
        if (object.matches("(-?[0-9]+):(-?[0-9]+):(-?[0-9]+)(:.*)?")) {
            String[] t = object.split(":");
            int x = Integer.parseInt(t[0]);
            int y = Integer.parseInt(t[1]);
            int z = Integer.parseInt(t[2]);
            World w = null;
            String ww = "";
            if (t.length >= 4) ww = t[3];
            if (ww.isEmpty()) {
                CommandSender owner = caller.getOwner();
                if (owner instanceof BlockCommandSender) w = ((BlockCommandSender) owner).getBlock().getWorld();
                else if (owner instanceof Player) w = ((Player) owner).getWorld();
            } else {
                w = Bukkit.getWorld(t[3]);
            }
            if (w == null) {
                throw new RuntimeException(PowerNBT.plugin.translate("error_noworld", ww));
            }
            return new NBTContainerBlock(w.getBlockAt(x, y, z));
        }
        if (object.matches("chunk:(-?[0-9]+):(-?[0-9]+)(:.*)?")) {
            String[] t = object.substring(6).split(":");
            int x = Integer.parseInt(t[0]);
            int z = Integer.parseInt(t[1]);
            World w = null;
            String ww = "";
            if (t.length >= 3) ww = t[2];
            if (ww.isEmpty()) {
                CommandSender owner = caller.getOwner();
                if (owner instanceof BlockCommandSender) w = ((BlockCommandSender) owner).getBlock().getWorld();
                else if (owner instanceof Player) w = ((Player) owner).getWorld();
            } else {
                w = Bukkit.getWorld(t[2]);
            }
            if (w == null) {
                throw new RuntimeException(PowerNBT.plugin.translate("error_noworld", ww));
            }
            return new NBTContainerChunk(w.getChunkAt(x, z));
        }
        if (object.startsWith("@")) {
            String tokenText = object.substring(1);
            if (tokenText.startsWith("\"") && tokenText.endsWith("\"") && tokenText.length()>1 ) {
                tokenText = StringParser.parse(tokenText.substring(1,tokenText.length()-1)).trim();
            }
            if (!tokenText.contains(File.separator)) {
                File baseDir = (Bukkit.getWorlds().get(0)).getWorldFolder();
                File playerFile;
                try {
                    UUID uuid = Bukkit.getOfflinePlayer(tokenText).getUniqueId();
                    File playerDir = new File(baseDir, "playerdata");
                    playerFile = new File(playerDir, uuid+".dat");
                } catch (NoSuchMethodError ignored) { // no getUniqueId()
                    File playerDir = new File(baseDir, "players");
                    playerFile = new File(playerDir, tokenText + ".dat");
                }
                return new NBTContainerFileGZip(playerFile);
            }

        }
        if (object.startsWith("\"") && object.endsWith("\"") && object.length() > 1) {
            String s = StringParser.parse(object.substring(1, object.length() - 1));
            NBTType type = NBTType.STRING;
            if (param != null) type = NBTType.fromString(param);
            return new NBTContainerBase(type.parse(s));
        }
        if (object.matches("#-?[0-9a-fA-F]+")) {
            Long l = Long.parseLong(object.substring(1), 16);
            String s = l.toString();
            NBTType type = NBTType.INT;
            if (param != null) type = NBTType.fromString(param);
            return new NBTContainerBase(type.parse(s));
        }
        if (object.matches("b[0-1]+")) {
            if (param == null) return null;
            Long l = Long.parseLong(object.substring(1), 2);
            String s = l.toString();
            NBTType type = NBTType.fromString(param);
            return new NBTContainerBase(type.parse(s));
        }
        if (object.matches("-?[0-9]*")) {
            if (param == null) return null;
            NBTType type = NBTType.fromString(param);
            if(type.equals(NBTType.BYTEARRAY)) type = NBTType.BYTE;
            else if(type.equals(NBTType.INTARRAY)) type = NBTType.INT;
            return new NBTContainerBase(type.parse(object));
        }
        if (object.matches("NaN|-?Infinity")) {
            if (param == null) return null;
            NBTType type = NBTType.fromString(param);
            if(type.equals(NBTType.BYTEARRAY)) type = NBTType.BYTE;
            else if(type.equals(NBTType.INTARRAY)) type = NBTType.INT;
            return new NBTContainerBase(type.parse(object));
        }
        if (object.matches("-?[0-9]+\\.[0-9]*")) {
            if (param == null) return null;
            NBTType type = NBTType.fromString(param);
            if(type.equals(NBTType.BYTEARRAY)) type = NBTType.BYTE;
            else if(type.equals(NBTType.INTARRAY)) type = NBTType.INT;
            return new NBTContainerBase(type.parse(object));
        }
        if (object.equals("*") || object.equals("self") || object.equals("this")) {
            return null;
        }
        if (object.matches("\\[((-?[0-9]+|#-?[0-9a-fA-F]+)(,(?!\\])|(?=\\])))*\\]")) {
            if (param == null) {
                return null;
            } else {
                NBTType type = NBTType.fromString(param);
                if (type == NBTType.BYTE) type = NBTType.BYTEARRAY;
                else if (type == NBTType.INT) type = NBTType.INTARRAY;
                return new NBTContainerBase(type.parse(object));
            }
        }
        if (object.equals("hand") || object.equals("h")) {
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
            return new NBTContainerComplex(player,q);
        }
        if (object.startsWith("hand:") && object.length()>5 || object.startsWith("h:") && object.length() > 2) {
            String tokenText = object.substring(object.indexOf(':')+1);
            if (tokenText.startsWith("\"") && tokenText.endsWith("\"") && tokenText.length()>1) {
                tokenText = StringParser.parse(tokenText.substring(1,tokenText.length()-1)).trim();
            }
            Player player = Bukkit.getPlayer(tokenText);

            if (player == null) throw new RuntimeException(plugin.translate("error_playernotfound", tokenText));
            NBTContainerEntity container = new NBTContainerEntity(player);
            int pslot = player.getInventory().getHeldItemSlot();
            int ind = 0;
            int result = -1;
            NBTTagList inventory = ((NBTTagCompound)container.getCustomTag()).getList("Inventory");
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
            return new NBTContainerComplex(container,q);
        }
        if (object.startsWith("{") && object.endsWith("}") || object.startsWith("[") && object.endsWith("]") || object.matches("-?[0-9]*(\\.[0-9])?[fd]") || object.matches("-?[0-9]*[bsil]")) {
            Object mojangsonTag = null;
            try {
                mojangsonTag = NBTParser.parser("", object).parse();
            } catch (Exception ignored){}
            if (mojangsonTag != null) {
                return new NBTContainerBase(NBTBase.wrap(mojangsonTag));
            }
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
        } else if (objectFuture.matches("b[0-1]*")) {
            if (paramContainer == null){
                throw new RuntimeException(plugin.translate("error_undefinedtype", objectFuture));
            }
            Long val = Long.parseLong(objectFuture, 2);
            NBTType type = NBTType.fromBase(paramQuery.get(paramContainer.getCustomTag()));
            this.container = new NBTContainerBase(type.parse(val.toString()));
            this.query = emptyQuery;
            action.execute();
        } else if (objectFuture.matches("-?[0-9]*(.[0-9]*)?") || objectFuture.matches("NaN|-?Infinity")) {
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
