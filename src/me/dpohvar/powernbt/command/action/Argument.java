package me.dpohvar.powernbt.command.action;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.utils.Caller;
import me.dpohvar.powernbt.utils.StringParser;
import me.dpohvar.powernbt.utils.TempListener;
import me.dpohvar.powernbt.utils.nbt.*;
import me.dpohvar.powernbt.utils.versionfix.XNBTBase;
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
import java.util.LinkedList;

import static me.dpohvar.powernbt.PowerNBT.plugin;
import static me.dpohvar.powernbt.utils.versionfix.StaticValues.*;
import static me.dpohvar.powernbt.utils.versionfix.VersionFix.getNew;
import static me.dpohvar.powernbt.utils.versionfix.VersionFix.getShell;

public class Argument {

    static final NBTQuery emptyQuery = new NBTQuery();

    private final Caller caller;
    private NBTContainer container;
    private NBTQuery query = null;
    private String objectFuture = null;
    private String queryFuture = null;

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
            NBTType t = NBTType.fromBase(c.getObject());
            if (t.tagable) query = new NBTQuery(param);
            else query = emptyQuery;
        } else {
            query = new NBTQuery(param);
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
        } else if (object.startsWith("$") && object.length() > 1) {
            if (object.contains(File.separator)) throw new RuntimeException("invalid file name");
            File folder = plugin.getNBTFilesFolder();
            File file = new File(folder, object.substring(1) + ".nbt");
            return new NBTContainerFile(file);
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
            return new NBTContainerBase(getShell(XNBTBase.class, getNew(classNBTTagCompound, noInput)));
        } else if (object.equals("list")) {
            return new NBTContainerBase(getShell(XNBTBase.class, getNew(classNBTTagList, noInput)));
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
        } else if (object.matches("-?[0-9]+(.[0-9]*)?")) {
            if (param == null) {
                return null;
            } else {
                NBTType type = NBTType.fromString(param);
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
                        query = new NBTQuery(queryFuture);
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
                        query = new NBTQuery(queryFuture);
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
                            NBTType t = NBTType.fromBase(c.getObject());

                            if (t.tagable) {
                                if (p2 != null) container = new NBTContainerComplex(container, new NBTQuery(p2));
                                query = new NBTQuery(queryFuture);
                            } else query = emptyQuery;
                            action.execute();
                        } else {
                            if (p2 != null) container = new NBTContainerComplex(container, new NBTQuery(p2));
                            query = new NBTQuery(queryFuture);
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
            this.query = new NBTQuery(queryFuture);
            action.execute();
        } else if (objectFuture.matches("-?[0-9]*(.[0-9]*)?")) {
            if (paramContainer == null)
                throw new RuntimeException(plugin.translate("error_undefinedtype", objectFuture));
            NBTType type = NBTType.fromBase(paramContainer.getBase(paramQuery));
            this.container = new NBTContainerBase(type.parse(objectFuture));
            this.query = emptyQuery;
            action.execute();
        } else {
            throw new RuntimeException("future object type ignored");
        }
    }

}
