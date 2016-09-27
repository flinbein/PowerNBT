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

    public CommandNBT(){
        super(false);
    }

    public CommandNBT(boolean silent){
        super(silent);
    }

    public static final HashSet<String> specialTokens = new HashSet<String>(
            Arrays.asList(
                    "=", "<",
                    "rm", "rem", "remove",
                    "ren", "rename",
                    "copy",
                    "&=", // a = a & b
                    "|=", // a = a | b
                    "^=", // a = a ^ b
                    "*=", // a = a * b
                    "~",
                    "paste",
                    "add","+=",
                    "cut",
                    "set", "select",
                    "as",
                    "view", "?",
                    "debug",
                    "cancel",
                    "swap", "<>",
                    ">",
                    ">>",
                    "<<",
                    "insert","ins",
                    "spawn"
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
        RuntimeException exceptionTMArgs = new RuntimeException(plugin.translate("error_toomanyarguments"));
        RuntimeException exceptioNEArgs = new RuntimeException(plugin.translate("error_notenougharguments"));
        if (action == null) {
            if (argsBefore.size() > 3) throw exceptionTMArgs;
            Action a = new ActionView(caller, argsBefore.poll(), argsBefore.poll(), argsBefore.poll());
            a.execute();
        } else if (action.equals("view") || action.equals("?")) {
            if (argsBefore.size() > 3) throw exceptionTMArgs;
            if (argsBefore.size() < 1) throw exceptioNEArgs;
            if (argsAfter.size() > 0) throw exceptionTMArgs;
            Action a = new ActionView(caller, argsBefore.poll(), argsBefore.poll(), argsBefore.poll());
            a.execute();
        } else if (action.equals("paste")) {
            if (argsBefore.size() > 2) throw exceptionTMArgs;
            if (argsBefore.size() < 1) throw exceptioNEArgs;
            if (argsAfter.size() > 1) throw exceptionTMArgs;
            Action a = new ActionEdit(caller, argsBefore.poll(), argsBefore.poll(), "buffer", argsAfter.poll());
            a.execute();
        } else if (action.equals("debug")) {
            if (argsBefore.size() > 0) throw exceptionTMArgs;
            if (argsAfter.size() > 1) throw exceptionTMArgs;
            Action a = new ActionDebug(caller, argsAfter.poll());
            a.execute();
        } else if (action.equals("cancel")) {
            if (argsBefore.size() > 0) throw exceptionTMArgs;
            if (argsAfter.size() > 0) throw exceptionTMArgs;
            Action a = new ActionCancel(caller);
            a.execute();
        } else if (action.equals("=") || action.equals("<")) {
            if (argsBefore.size() > 2) throw exceptionTMArgs;
            if (argsBefore.size() < 1) throw exceptioNEArgs;
            if (argsAfter.size() > 2) throw exceptionTMArgs;
            if (argsAfter.size() < 1) throw exceptioNEArgs;
            Action a = new ActionEdit(caller, argsBefore.poll(), argsBefore.poll(), argsAfter.poll(), argsAfter.poll());
            a.execute();
        } else if (action.equals(">")) {
            if (argsBefore.size() > 2) throw exceptionTMArgs;
            if (argsBefore.size() < 1) throw exceptioNEArgs;
            if (argsAfter.size() > 2) throw exceptionTMArgs;
            if (argsAfter.size() < 1) throw exceptioNEArgs;
            Action a = new ActionEditLast(caller, argsBefore.poll(), argsBefore.poll(), argsAfter.poll(), argsAfter.poll());
            a.execute();
        } else if (action.equals(">>")) {
            if (argsBefore.size() > 2) throw exceptionTMArgs;
            if (argsBefore.size() < 1) throw exceptioNEArgs;
            if (argsAfter.size() > 2) throw exceptionTMArgs;
            if (argsAfter.size() < 1) throw exceptioNEArgs;
            Action a = new ActionMove(caller, argsAfter.poll(), argsAfter.poll(), argsBefore.poll(), argsBefore.poll());
            a.execute();
        } else if (action.equals("<<")) {
            if (argsBefore.size() > 2) throw exceptionTMArgs;
            if (argsBefore.size() < 1) throw exceptioNEArgs;
            if (argsAfter.size() > 2) throw exceptionTMArgs;
            if (argsAfter.size() < 1) throw exceptioNEArgs;
            Action a = new ActionMoveLast(caller, argsAfter.poll(), argsAfter.poll(), argsBefore.poll(), argsBefore.poll());
            a.execute();
        } else if (action.equals("cut")) {
            if (argsBefore.size() > 2) throw exceptionTMArgs;
            if (argsBefore.size() < 1) throw exceptioNEArgs;
            if (argsAfter.size() > 0) throw exceptionTMArgs;
            Action a = new ActionCut(caller, argsBefore.poll(), argsBefore.poll());
            a.execute();
        } else if (action.equals("rm") || action.equals("rem") || action.equals("remove")) {
            if (argsBefore.size() > 2) throw exceptionTMArgs;
            if (argsBefore.size() < 1) throw exceptioNEArgs;
            if (argsAfter.size() > 0) throw exceptionTMArgs;
            Action a = new ActionRemove(caller, argsBefore.poll(), argsBefore.poll());
            a.execute();
        } else if (action.equals("ren") || action.equals("rename")) {
            if (argsBefore.size() > 2) throw exceptionTMArgs;
            if (argsBefore.size() < 2) throw exceptioNEArgs;
            if (argsAfter.size() != 1) throw exceptionTMArgs;
            Action a = new ActionRename(caller, argsBefore.poll(), argsBefore.poll(), argsAfter.poll());
            a.execute();
        } else if (action.equals("copy")) {
            if (argsBefore.size() > 2) throw exceptionTMArgs;
            if (argsBefore.size() < 1) throw exceptioNEArgs;
            if (argsAfter.size() > 0) throw exceptionTMArgs;
            Action a = new ActionCopy(caller, argsBefore.poll(), argsBefore.poll());
            a.execute();
        } else if (action.equals("select") || action.equals("set")) {
            if (argsBefore.size() > 1) throw exceptionTMArgs;
            if (argsBefore.size() < 1) throw exceptioNEArgs;
            if (argsAfter.size() > 2) throw exceptionTMArgs;
            Action a = new ActionSet(caller, argsBefore.poll(), argsAfter.poll(), argsAfter.poll());
            a.execute();
        } else if (action.equals("as")) {
            if (argsBefore.size() > 2) throw exceptionTMArgs;
            if (argsBefore.size() < 1) throw exceptioNEArgs;
            if (argsAfter.size() > 1) throw exceptionTMArgs;
            Action a = new ActionSet(caller, argsAfter.poll(), argsBefore.poll(), argsBefore.poll());
            a.execute();
        } else if (action.equals("swap") || action.equals("<>")) {
            if (argsBefore.size() > 2) throw exceptionTMArgs;
            if (argsBefore.size() < 1) throw exceptioNEArgs;
            if (argsAfter.size() > 2) throw exceptionTMArgs;
            if (argsAfter.size() < 1) throw exceptioNEArgs;
            Action a = new ActionSwap(caller, argsBefore.poll(), argsBefore.poll(), argsAfter.poll(), argsAfter.poll());
            a.execute();
        } else if (action.equals("add") || action.equals("+=")) {
            if (argsBefore.size() > 2) throw exceptionTMArgs;
            if (argsBefore.size() < 1) throw exceptioNEArgs;
            if (argsAfter.size() > 2) throw exceptionTMArgs;
            if (argsAfter.size() < 1) throw exceptioNEArgs;
            Action a = new ActionAddAll(caller, argsBefore.poll(), argsBefore.poll(), argsAfter.poll(), argsAfter.poll());
            a.execute();
        } else if (action.equals("insert") || action.equals("ins")) {
            if (argsBefore.size() > 2) throw exceptionTMArgs;
            if (argsBefore.size() < 1) throw exceptioNEArgs;
            if (argsAfter.size() > 3) throw exceptionTMArgs;
            if (argsBefore.size() < 2) throw exceptioNEArgs;
            Action a = new ActionInsert(caller, argsBefore.poll(), argsBefore.poll(), argsAfter.poll(), argsAfter.poll(), argsAfter.poll());
            a.execute();
        } else if (action.equals("&=")) {
            if (argsBefore.size() > 2) throw exceptionTMArgs;
            if (argsBefore.size() < 1) throw exceptioNEArgs;
            if (argsAfter.size() > 2) throw exceptionTMArgs;
            if (argsAfter.size() < 1) throw exceptioNEArgs;
            Action a = new ActionBitAnd(caller, argsBefore.poll(), argsBefore.poll(), argsAfter.poll(), argsAfter.poll());
            a.execute();
        } else if (action.equals("|=")) {
            if (argsBefore.size() > 2) throw exceptionTMArgs;
            if (argsBefore.size() < 1) throw exceptioNEArgs;
            if (argsAfter.size() > 2) throw exceptionTMArgs;
            if (argsAfter.size() < 1) throw exceptioNEArgs;
            Action a = new ActionBitOr(caller, argsBefore.poll(), argsBefore.poll(), argsAfter.poll(), argsAfter.poll());
            a.execute();
        } else if (action.equals("^=")) {
            if (argsBefore.size() > 2) throw exceptionTMArgs;
            if (argsBefore.size() < 1) throw exceptioNEArgs;
            if (argsAfter.size() > 2) throw exceptionTMArgs;
            if (argsAfter.size() < 1) throw exceptioNEArgs;
            Action a = new ActionBitXor(caller, argsBefore.poll(), argsBefore.poll(), argsAfter.poll(), argsAfter.poll());
            a.execute();
        } else if (action.equals("*=")) {
            if (argsBefore.size() > 2) throw exceptionTMArgs;
            if (argsBefore.size() < 1) throw exceptioNEArgs;
            if (argsAfter.size() > 2) throw exceptionTMArgs;
            if (argsAfter.size() < 1) throw exceptioNEArgs;
            Action a = new ActionMultiply(caller, argsBefore.poll(), argsBefore.poll(), argsAfter.poll(), argsAfter.poll());
            a.execute();
        } else if (action.equals("~")) {
            if (argsBefore.size() > 2) throw exceptionTMArgs;
            if (argsBefore.size() < 1) throw exceptioNEArgs;
            if (argsAfter.size() > 0) throw exceptionTMArgs;
            Action a = new ActionBitInverse(caller, argsBefore.poll(), argsBefore.poll());
            a.execute();
        } else if (action.equals("spawn")) {
            if (argsBefore.size() > 2) throw exceptionTMArgs;
            if (argsBefore.size() < 1) throw exceptioNEArgs;
            if (argsAfter.size() > 1) throw exceptionTMArgs;
            Action a = new ActionSpawn(caller, argsBefore.poll(), argsBefore.poll(), argsAfter.poll());
            a.execute();
        }
        return true;
    }

    public static NBTContainer getContainer(final Caller caller, String word, NBTType type) {
        return null;
    }

}


















