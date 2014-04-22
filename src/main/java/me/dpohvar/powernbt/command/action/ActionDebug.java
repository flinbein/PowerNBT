package me.dpohvar.powernbt.command.action;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.utils.Caller;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public class ActionDebug extends Action {

    private final Caller caller;
    private final String value;

    public ActionDebug(Caller caller, String value) {
        this.caller = caller;
        this.value = value;
    }

    @Override
    public void execute() {
        boolean debug;
        if (value == null || value.equals("toggle")) {
            debug = !PowerNBT.plugin.isDebug();
        } else {
            if (value.equals("on") || value.equals("enable") || value.equals("true")) {
                debug = true;
            } else if (value.equals("off") || value.equals("disable") || value.equals("false")) {
                debug = false;
            } else {
                throw new RuntimeException(plugin.translate("error_parsevalue", value));
            }
        }
        plugin.setDebug(debug);
        if (debug) caller.send(plugin.translate("success_debug_on"));
        else caller.send(plugin.translate("success_debug_off"));
    }
}
