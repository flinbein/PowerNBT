package me.dpohvar.powernbt.command.action;

import me.dpohvar.powernbt.api.NBTManager;
import me.dpohvar.powernbt.nbt.NBTContainer;
import me.dpohvar.powernbt.nbt.NBTType;
import me.dpohvar.powernbt.utils.Caller;
import me.dpohvar.powernbt.utils.query.NBTQuery;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public abstract class ActionBiLong extends Action {

    private final Caller caller;
    private final Argument arg1;
    private final Argument arg2;

    public ActionBiLong(Caller caller, String o1, String q1, String o2, String q2) {
        this.caller = caller;
        this.arg1 = new Argument(caller, o1, q1);
        this.arg2 = new Argument(caller, o2, q2);
    }

    @Override
    public void execute() throws Exception {
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
        Object base1 = container1.getCustomTag(query1);
        NBTContainer container2 = arg2.getContainer();
        NBTQuery query2 = arg2.getQuery();
        Object base2 = container2.getCustomTag(query2);
        if (!(base2 instanceof Number)){
            throw new RuntimeException(plugin.translate("error_null"));
        }
        if (base1 == null) {
            base1 = NBTType.fromValue(base2).getDefaultValue();
        }
        long baseValue = ((Number)base1).longValue();
        long argValue = ((Number)base2).longValue();
        Object result = NBTManager.convertValue(operation(baseValue, argValue), NBTType.fromValue(base1).type);
        container1.setCustomTag(query1, result);
        caller.sendValue(plugin.translate("success_edit"), result, false, false);

    }

    abstract long operation(long arg1, long arg2);
}
