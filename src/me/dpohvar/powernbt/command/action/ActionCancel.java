package me.dpohvar.powernbt.command.action;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.utils.Caller;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public class ActionCancel extends Action {

    private final Caller caller;

    public ActionCancel(Caller caller) {
        this.caller = caller;
    }

    @Override
    public void execute() {
        caller.hold(null,null);
        caller.send(plugin.translate("selection_cancel"));
    }
}
