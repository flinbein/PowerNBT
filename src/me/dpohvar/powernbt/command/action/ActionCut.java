package me.dpohvar.powernbt.command.action;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.utils.Caller;
import me.dpohvar.powernbt.utils.nbt.NBTContainer;
import me.dpohvar.powernbt.utils.nbt.NBTQuery;
import me.dpohvar.powernbt.utils.versionfix.XNBTBase;

public class ActionCut extends Action {

    private final Caller caller;
    private final Argument arg;

    public ActionCut(Caller caller, String o1, String q1) {
        this.caller = caller;
        this.arg = new Argument(caller, o1, q1);
    }

    @Override
    public void execute() {
        if (arg.needPrepare()) {
            arg.prepare(this, null, null);
            return;
        }
        NBTContainer container = arg.getContainer();
        NBTQuery query = arg.getQuery();
        XNBTBase base = container.getBase(query);
        if (base == null) throw new RuntimeException(PowerNBT.plugin.translate("error_null"));
        caller.setRootBase(base);
        caller.send(PowerNBT.plugin.translate("success_cut") + getNBTShortView(base, null));
        arg.getContainer().removeBase(arg.getQuery());
    }
}
