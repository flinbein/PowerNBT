package me.dpohvar.powernbt.command.action;

import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.api.NBTManager;
import me.dpohvar.powernbt.nbt.NBTContainer;
import me.dpohvar.powernbt.utils.Caller;
import me.dpohvar.powernbt.utils.query.NBTQuery;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import java.util.Map;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public class ActionSpawn extends Action {

    private final Caller caller;
    private final Argument arg;
    private String worldParam;

    private ActionSpawn(Caller caller, String object, String query) {
        this.caller = caller;
        this.arg = new Argument(caller, object, query);
    }

    public ActionSpawn(Caller caller, String object, String query, String worldParam) {
        this(caller, object, query);
        this.worldParam = worldParam;
    }

    @Override
    public void execute() throws Exception {
        if (arg.needPrepare()) {
            arg.prepare(this, null, null);
            return;
        }
        NBTContainer container = arg.getContainer();
        NBTQuery query = arg.getQuery();
        World world;
        if (worldParam == null) {
            CommandSender owner = caller.getOwner();
            if (owner instanceof Entity) world = ((Entity) owner).getWorld();
            else if (owner instanceof BlockCommandSender) world = ((BlockCommandSender) owner).getBlock().getWorld();
            else throw new RuntimeException(plugin.translate("error_noplayer"));
        } else {
            world = Bukkit.getWorld(worldParam);
            if (world == null) throw new RuntimeException(plugin.translate("error_noworld", worldParam));
        }
        Object base = container.getCustomTag(query);

        NBTCompound compound = null;
        if (base instanceof NBTCompound c) compound = c;
        else if (base instanceof Map map) compound = new NBTCompound(map);

        if (compound != null) {
            Entity entity = NBTManager.getInstance().spawnEntity(compound, world);
            if (entity != null) {
                caller.send(plugin.translate("success_spawn", entity.getName()));
                return;
            }
        }
        caller.send(plugin.translate("error_parsevalue", ""));
    }
}









