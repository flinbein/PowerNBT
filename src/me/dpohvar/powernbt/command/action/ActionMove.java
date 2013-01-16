package me.dpohvar.powernbt.command.action;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.nbt.NBTBase;
import me.dpohvar.powernbt.nbt.NBTContainer;
import me.dpohvar.powernbt.nbt.NBTQuery;
import me.dpohvar.powernbt.utils.Caller;

public class ActionMove extends Action {

    private final Caller caller;
    private final Argument arg1;
    private final Argument arg2;

    public ActionMove(Caller caller, String o1, String q1, String o2, String q2) {
        this.caller = caller;
        this.arg1 = new Argument(caller, o1, q1);
        this.arg2 = new Argument(caller, o2, q2);
    }

    @Override
    public void execute() {
        if (arg1.needPrepare()) {
            arg1.prepare(this, null, null);
            return;
        }
        NBTContainer container = arg1.getContainer();
        NBTQuery query = arg1.getQuery();
        if (arg2.needPrepare()) {
            arg2.prepare(this, container, query);
            return;
        }
        NBTBase base = arg2.getContainer().getTag(arg2.getQuery());
        if (base == null) throw new RuntimeException(PowerNBT.plugin.translate("error_null"));
        boolean result = container.setTag(query, base);
        if (!result) {
            throw new RuntimeException(PowerNBT.plugin.translate("fail_move", query.getQuery()));
        }
        caller.send(PowerNBT.plugin.translate("success_move") + getNBTShortView(base, null));
        arg2.getContainer().removeTag(arg2.getQuery());
    }
}
