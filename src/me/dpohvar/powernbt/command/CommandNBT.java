package me.dpohvar.powernbt.command;

import me.dpohvar.powernbt.command.action.*;
import me.dpohvar.powernbt.utils.Caller;
import me.dpohvar.powernbt.utils.nbt.NBTContainer;
import me.dpohvar.powernbt.utils.nbt.NBTType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;

import static me.dpohvar.powernbt.PowerNBT.plugin;


public class CommandNBT extends Command {
    private static HashSet<String> specialTokens = new HashSet<String>(
            Arrays.asList("=", "+=", "rem", "remove", "copy", "set", "select")
    );

    @Override
    public boolean command(final Caller caller, LinkedList<String> words) throws Throwable {
        if (words.size() == 0) return false;
        LinkedList<String> argsBefore = new LinkedList<String>();
        LinkedList<String> argsAfter = new LinkedList<String>();
        String action = null;
        for (String t : words) {
            if (specialTokens.contains(t)) {
                action = t;
            } else {
                if (action == null) argsBefore.add(t);
                else argsAfter.add(t);
            }
        }
        RuntimeException exceptionArgs = new RuntimeException(plugin.translate("error_toomanyarguments"));
        if (action == null) {
            if (argsAfter.size() > 2) throw exceptionArgs;
            Action a = new ActionView(caller, words.poll(), words.poll());
            a.execute();
        } else if (action.equals("=")) {
            if (argsBefore.size() > 2) throw exceptionArgs;
            if (argsAfter.size() > 2) throw exceptionArgs;
            Action a = new ActionEdit(caller, argsBefore.poll(), argsBefore.poll(), argsAfter.poll(), argsAfter.poll());
            a.execute();
        } else if (action.equals("rem") || action.equals("remove")) {
            if (argsBefore.size() > 2) throw exceptionArgs;
            if (argsAfter.size() > 0) throw exceptionArgs;
            Action a = new ActionRemove(caller, argsBefore.poll(), argsBefore.poll());
            a.execute();
        } else if (action.equals("copy")) {
            if (argsBefore.size() > 2) throw exceptionArgs;
            if (argsAfter.size() > 0) throw exceptionArgs;
            Action a = new ActionCopy(caller, argsBefore.poll(), argsBefore.poll());
            a.execute();
        } else if (action.equals("select") || action.equals("set")) {
            if (argsBefore.size() > 1) throw exceptionArgs;
            if (argsAfter.size() > 2) throw exceptionArgs;
            Action a = new ActionSet(caller, argsBefore.poll(), argsAfter.poll(), argsAfter.poll());
            a.execute();
        }
        return true;
    }

    public static NBTContainer getContainer(final Caller caller, String word, NBTType type) {

        return null;
    }

}


















