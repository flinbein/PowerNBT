package me.dpohvar.powernbt.command.action;

import me.dpohvar.powernbt.utils.Caller;
import me.dpohvar.powernbt.utils.nbt.NBTContainer;
import me.dpohvar.powernbt.utils.nbt.NBTQuery;

public class ActionView extends Action {

    private final Caller caller;
    private final Argument arg;

    public ActionView(Caller caller, String object, String query) {
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
        caller.send(getNBTView(container.getBase(query)));
    }
}
