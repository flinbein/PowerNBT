package me.dpohvar.powernbt.command.action;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.exception.NBTTagNotFound;
import me.dpohvar.powernbt.nbt.NBTBase;
import me.dpohvar.powernbt.nbt.NBTContainer;
import me.dpohvar.powernbt.utils.NBTQuery;
import me.dpohvar.powernbt.utils.Caller;
import me.dpohvar.powernbt.utils.NBTViewer;

public class ActionCut extends Action {

    private final Caller caller;
    private final Argument arg;

    public ActionCut(Caller caller, String o1, String q1) {
        this.caller = caller;
        this.arg = new Argument(caller, o1, q1);
    }

    @Override
    public void execute() throws Exception {
        if (arg.needPrepare()) {
            arg.prepare(this, null, null);
            return;
        }
        NBTContainer container = arg.getContainer();
        NBTQuery query = arg.getQuery();
        NBTBase base = container.getCustomTag(query);
        if (base == null) throw new RuntimeException(PowerNBT.plugin.translate("error_null"));
        caller.setCustomTag(base);
        caller.send(PowerNBT.plugin.translate("success_cut") + NBTViewer.getShortValueWithPrefix(base, false));
        arg.getContainer().removeTag(arg.getQuery());
    }
}
