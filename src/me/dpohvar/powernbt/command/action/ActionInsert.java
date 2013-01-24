package me.dpohvar.powernbt.command.action;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.nbt.*;
import me.dpohvar.powernbt.utils.Caller;

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
        NBTBase base1 = container1.getTag(query1);
        NBTContainer container2 = arg2.getContainer();
        NBTQuery query2 = arg2.getQuery();
        NBTBase base2 = container2.getTag(query2);
        if (base1 instanceof NBTTagList){
            NBTTagList tag1 = (NBTTagList) base1;
            tag1.add(pos,base2);
            container1.setTag(query1,tag1);
            caller.send(PowerNBT.plugin.translate("success_insert") + getNBTShortView(base2, null));
        } else if (base1 instanceof NBTTagNumericArray && base2 instanceof NBTTagNumeric){
            NBTTagNumericArray list1 = (NBTTagNumericArray) base1;
            NBTTagNumeric num = (NBTTagNumeric) base2;
            list1.add(pos,num.get());
            container1.setTag(query1,list1);
            caller.send(PowerNBT.plugin.translate("success_add") + getNBTShortView(num, null));
        } else {
            caller.send(PowerNBT.plugin.translate("fail_insert"));
        }
    }
}
