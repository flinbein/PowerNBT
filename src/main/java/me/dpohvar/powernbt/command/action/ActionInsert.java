package me.dpohvar.powernbt.command.action;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.api.NBTList;
import me.dpohvar.powernbt.api.NBTManager;
import me.dpohvar.powernbt.nbt.NBTContainer;
import me.dpohvar.powernbt.nbt.NBTType;
import me.dpohvar.powernbt.utils.Caller;
import me.dpohvar.powernbt.utils.query.NBTQuery;

import java.util.Arrays;
import java.util.List;

public class ActionInsert extends Action {

    private final Caller caller;
    private final Argument arg1;
    private final Argument arg2;
    private final int pos;

    public ActionInsert(Caller caller, String o1, String q1, String pos, String o2, String q2) {
        this.caller = caller;
        this.pos = Integer.parseInt(pos);
        if(this.pos<0) caller.send(PowerNBT.plugin.translate("error_index",this.pos));
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
        if (base1 instanceof NBTList list){
            list.add(pos,base2);
            container1.setCustomTag(query1,list);
            caller.sendValue(PowerNBT.plugin.translate("success_insert"), base2, false, false);
        } else if (base1 != null && base1.getClass().isArray() && base2 instanceof Number num){
            byte type = NBTType.fromValue(base1).type;
            Object[] array = NBTManager.convertToObjectArrayOrNull(base1);
            List<Object> list = Arrays.asList(array);
            list.add(pos, num);
            Object result = NBTManager.convertValue(list, type);
            container1.setCustomTag(query1, result);
            caller.sendValue(PowerNBT.plugin.translate("success_add"), num, false, false);
        } else {
            caller.send(PowerNBT.plugin.translate("fail_insert"));
        }
    }
}
