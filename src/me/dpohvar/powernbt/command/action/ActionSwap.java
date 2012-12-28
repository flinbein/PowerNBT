package me.dpohvar.powernbt.command.action;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.utils.Caller;
import me.dpohvar.powernbt.utils.nbt.NBTContainer;
import me.dpohvar.powernbt.utils.nbt.NBTQuery;
import me.dpohvar.powernbt.utils.versionfix.XNBTBase;

public class ActionSwap extends Action {

    private final Caller caller;
    private final Argument arg1;
    private final Argument arg2;

    public ActionSwap(Caller caller, String o1, String q1, String o2, String q2) {
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
        NBTContainer container1 = arg1.getContainer();
        NBTQuery query1 = arg1.getQuery();
        if (arg2.needPrepare()) {
            arg2.prepare(this, container1, query1);
            return;
        }
        NBTContainer container2 = arg2.getContainer();
        NBTQuery query2 = arg2.getQuery();
        XNBTBase base1 = container1.getBase(query1);
        XNBTBase base2 = container2.getBase(query2);
        if (base1 == null && base2 == null) {
            caller.send(PowerNBT.plugin.translate("success_swap_null"));
            return;
        }
        if (base2 == null) container1.removeBase(query1);
        else container1.setBase(query1, base2);
        if (base1 == null) container2.removeBase(query2);
        else container2.setBase(query2, base1);
        caller.send(PowerNBT.plugin.translate("success_swap"));
    }
}
