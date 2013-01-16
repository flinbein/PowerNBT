package me.dpohvar.powernbt.command.action;

import me.dpohvar.powernbt.nbt.NBTContainer;
import me.dpohvar.powernbt.nbt.NBTQuery;
import me.dpohvar.powernbt.utils.Caller;

import java.util.ArrayList;
import java.util.List;

public class ActionView extends Action {

    private final Caller caller;
    private final Argument arg;
    private List<String> args = new ArrayList<String>();

    public ActionView(Caller caller, String object, String query) {
        this.caller = caller;
        this.arg = new Argument(caller, object, query);
    }

    public ActionView(Caller caller, String object, String query, List<String> args) {
        this(caller, object, query);
        this.args = args;
    }

    @Override
    public void execute() {
        if (arg.needPrepare()) {
            arg.prepare(this, null, null);
            return;
        }
        NBTContainer container = arg.getContainer();
        NBTQuery query = arg.getQuery();
        caller.send(getNBTView(container.getTag(query), args));
    }
}
