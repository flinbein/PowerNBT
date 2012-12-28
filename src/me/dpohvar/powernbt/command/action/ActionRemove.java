package me.dpohvar.powernbt.command.action;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.utils.Caller;
import me.dpohvar.powernbt.utils.nbt.NBTContainer;
import me.dpohvar.powernbt.utils.nbt.NBTQuery;
import me.dpohvar.powernbt.utils.versionfix.XNBTBase;

public class ActionRemove extends Action {

    private final Caller caller;
    private final Argument arg;

    public ActionRemove(Caller caller, String object, String query) {
        this.caller = caller;
        this.arg = new Argument(caller, object, query);
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
        container.removeBase(query);
        caller.send(PowerNBT.plugin.translate("success_removed") + getNBTShortView(base, null));
    }
}
