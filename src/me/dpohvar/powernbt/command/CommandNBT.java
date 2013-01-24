package me.dpohvar.powernbt.command;

import me.dpohvar.powernbt.command.action.*;
import me.dpohvar.powernbt.nbt.NBTContainer;
import me.dpohvar.powernbt.nbt.NBTType;
import me.dpohvar.powernbt.utils.Caller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;

import static me.dpohvar.powernbt.PowerNBT.plugin;


public class CommandNBT extends Command {
    public static final HashSet<String> specialTokens = new HashSet<String>(
            Arrays.asList(
                    "=", "<",
                    "rem", "remove",
                    "ren", "rename",
                    "copy",
                    "paste",
                    "add","+=",
                    "cut",
                    "set", "select",
                    "as",
                    "view", "?",
                    "debug",
                    "swap", "<>",
                    ">",
                    ">>",
                    "<<",
                    "insert","ins"
            )
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
            if (argsBefore.size() > 2) throw exceptionArgs;
            Action a = new ActionView(caller, argsBefore.poll(), argsBefore.poll());
            a.execute();
        } else if (action.equals("view") || action.equals("?")) {
            if (argsBefore.size() > 2) throw exceptionArgs;
            Action a = new ActionView(caller, argsBefore.poll(), argsBefore.poll(), argsAfter);
            a.execute();
        } else if (action.equals("paste")) {
            if (argsBefore.size() > 2) throw exceptionArgs;
            if (argsAfter.size() > 1) throw exceptionArgs;
            Action a = new ActionEdit(caller, argsBefore.poll(), argsBefore.poll(), "buffer", argsAfter.poll());
            a.execute();
        } else if (action.equals("debug")) {
            if (argsBefore.size() > 0) throw exceptionArgs;
            if (argsAfter.size() > 1) throw exceptionArgs;
            Action a = new ActionDebug(caller, argsAfter.poll());
            a.execute();
        } else if (action.equals("=") || action.equals("<")) {
            if (argsBefore.size() > 2) throw exceptionArgs;
            if (argsAfter.size() > 2) throw exceptionArgs;
            Action a = new ActionEdit(caller, argsBefore.poll(), argsBefore.poll(), argsAfter.poll(), argsAfter.poll());
            a.execute();
        } else if (action.equals(">")) {
            if (argsBefore.size() > 2) throw exceptionArgs;
            if (argsAfter.size() > 2) throw exceptionArgs;
            Action a = new ActionEdit(caller, argsAfter.poll(), argsAfter.poll(), argsBefore.poll(), argsBefore.poll());
            a.execute();
        } else if (action.equals("paste")) {
            if (argsBefore.size() > 2) throw exceptionArgs;
            if (argsAfter.size() > 1) throw exceptionArgs;
            Action a = new ActionEdit(caller, argsBefore.poll(), argsBefore.poll(), "buffer", argsAfter.poll());
            a.execute();
        } else if (action.equals(">>")) {
            if (argsBefore.size() > 2) throw exceptionArgs;
            if (argsAfter.size() > 2) throw exceptionArgs;
            Action a = new ActionMove(caller, argsAfter.poll(), argsAfter.poll(), argsBefore.poll(), argsBefore.poll());
            a.execute();
        } else if (action.equals("<<")) {
            if (argsBefore.size() > 2) throw exceptionArgs;
            if (argsAfter.size() > 2) throw exceptionArgs;
            Action a = new ActionMove(caller, argsBefore.poll(), argsBefore.poll(), argsAfter.poll(), argsAfter.poll());
            a.execute();
        } else if (action.equals("cut")) {
            if (argsBefore.size() > 2) throw exceptionArgs;
            if (argsAfter.size() > 0) throw exceptionArgs;
            Action a = new ActionCut(caller, argsBefore.poll(), argsBefore.poll());
            a.execute();
        } else if (action.equals("rem") || action.equals("remove")) {
            if (argsBefore.size() > 2) throw exceptionArgs;
            if (argsAfter.size() > 0) throw exceptionArgs;
            Action a = new ActionRemove(caller, argsBefore.poll(), argsBefore.poll());
            a.execute();
        } else if (action.equals("ren") || action.equals("rename")) {
            if (argsBefore.size() > 2) throw exceptionArgs;
            if (argsAfter.size() != 1) throw exceptionArgs;
            Action a = new ActionRename(caller, argsBefore.poll(), argsBefore.poll(), argsAfter.poll());
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
        } else if (action.equals("as")) {
            if (argsBefore.size() > 2) throw exceptionArgs;
            if (argsAfter.size() > 1) throw exceptionArgs;
            Action a = new ActionSet(caller, argsAfter.poll(), argsBefore.poll(), argsBefore.poll());
            a.execute();
        } else if (action.equals("swap") || action.equals("<>")) {
            if (argsBefore.size() > 2) throw exceptionArgs;
            if (argsAfter.size() > 2) throw exceptionArgs;
            Action a = new ActionSwap(caller, argsBefore.poll(), argsBefore.poll(), argsAfter.poll(), argsAfter.poll());
            a.execute();
        } else if (action.equals("add") || action.equals("+=")) {
            if (argsBefore.size() > 2) throw exceptionArgs;
            if (argsAfter.size() > 2) throw exceptionArgs;
            Action a = new ActionAddAll(caller, argsBefore.poll(), argsBefore.poll(), argsAfter.poll(), argsAfter.poll());
            a.execute();
        } else if (action.equals("insert") || action.equals("ins")) {
            if (argsBefore.size() > 2) throw exceptionArgs;
            if (argsAfter.size() > 3) throw exceptionArgs;
            Action a = new ActionInsert(caller, argsBefore.poll(), argsBefore.poll(), argsAfter.poll(), argsAfter.poll(), argsAfter.poll());
            a.execute();
        }
        return true;
    }

    public static NBTContainer getContainer(final Caller caller, String word, NBTType type) {

        return null;
    }

}


















