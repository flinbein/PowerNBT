package me.dpohvar.powernbt.command.action;

import me.dpohvar.powernbt.exception.NBTTagNotFound;
import me.dpohvar.powernbt.nbt.*;
import org.bukkit.ChatColor;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public abstract class Action {

    abstract public void execute() throws Exception;

}
