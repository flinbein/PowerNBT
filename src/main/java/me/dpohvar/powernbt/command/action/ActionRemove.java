package me.dpohvar.powernbt.command.action;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.nbt.NBTContainer;
import me.dpohvar.powernbt.utils.Caller;
import me.dpohvar.powernbt.utils.query.NBTQuery;

public class ActionRemove extends Action {

    private final Caller caller;
    private final Argument arg;
    private final String param;

    public ActionRemove(Caller caller, String object, String query, String param) {
        this.caller = caller;
        this.arg = new Argument(caller, object, query);
        this.param = param;
    }

    @Override
    public void execute() throws Exception {
        if (arg.needPrepare()) {
            arg.prepare(this, null, null);
            return;
        }
        NBTContainer<?> container = arg.getContainer();
        NBTQuery query = arg.getQuery();
        Object base = null;
        try {
            base = container.getCustomTag(query);
        } catch (Exception ignored){}
        container.removeCustomTag(query);
        if (param != null) {
            new ActionView(caller, arg, param).execute();
        } else {
            caller.sendValue(PowerNBT.plugin.translate("success_removed"), base, false, false);
        }
    }
}
