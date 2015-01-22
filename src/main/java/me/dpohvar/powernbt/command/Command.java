package me.dpohvar.powernbt.command;

import me.dpohvar.powernbt.utils.Caller;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.LinkedList;
import java.util.logging.Level;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public abstract class Command implements CommandExecutor {

    private final boolean silent;

    public Command(boolean silent){
        this.silent = silent;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        Caller caller = plugin.getCaller(sender);
        caller.setSilent(silent);
        try {
            LinkedList<String> words = new LinkedList<String>();
            for (String s : plugin.getTokenizer().tokenize(StringUtils.join(args, ' ')).values()) {
                words.add(s);
            }
            return command(caller, words);
        } catch (Throwable t) {
            caller.handleException(t);
            if (plugin.isDebug()) {
                plugin.getLogger().log(Level.WARNING, "Exception on command: "+label, t);
            }
            return true;
        }
    }

    abstract protected boolean command(Caller caller, LinkedList<String> words) throws Throwable;
}
