package me.dpohvar.powernbt.command;

import me.dpohvar.powernbt.utils.Caller;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.LinkedList;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public abstract class Command implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        Caller caller = plugin.getCaller(sender);
        try {
            LinkedList<String> words = new LinkedList<String>();
            for (String s : plugin.getTokenizer().tokenize(StringUtils.join(args, ' ')).values()) {
                words.add(s);
            }
            return command(caller, words);
        } catch (Throwable t) {
            caller.handleException(t);
            return true;
        }
    }

    abstract public boolean command(Caller caller, LinkedList<String> words) throws Throwable;
}
