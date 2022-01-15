package me.dpohvar.powernbt.command.action;

import me.dpohvar.powernbt.api.NBTManager;
import me.dpohvar.powernbt.nbt.NBTContainer;
import me.dpohvar.powernbt.nbt.NBTType;
import me.dpohvar.powernbt.utils.Caller;
import me.dpohvar.powernbt.utils.query.NBTQuery;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public class ActionBitInverse extends Action {

    private final Caller caller;
    private final Argument arg1;

    public ActionBitInverse(Caller caller, String o1, String q1) {
        this.caller = caller;
        this.arg1 = new Argument(caller, o1, q1);
    }

    @Override
    public void execute() throws Exception {
        if (arg1.needPrepare()) {
            arg1.prepare(this, null, null);
            return;
        }
        NBTContainer container1 = arg1.getContainer();
        NBTQuery query1 = arg1.getQuery();
        Object base1 = container1.getCustomTag(query1);
        Object result;
        if (base1 instanceof Number num) {
            result = NBTManager.convertValue(~(num.longValue()), NBTType.fromValue(base1).type);
        } else if (base1 instanceof Boolean bool) {
            result = !bool;
        } else {
            throw new RuntimeException(plugin.translate("error_null"));
        }
        container1.setCustomTag(query1, result);
        caller.sendValue(plugin.translate("success_edit"), result, false, false);

    }
}
