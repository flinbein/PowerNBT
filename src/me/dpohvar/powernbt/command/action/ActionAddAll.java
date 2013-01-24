package me.dpohvar.powernbt.command.action;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.nbt.*;
import me.dpohvar.powernbt.utils.Caller;

import java.util.Map;

public class ActionAddAll extends Action {

    private final Caller caller;
    private final Argument arg1;
    private final Argument arg2;

    public ActionAddAll(Caller caller, String o1, String q1, String o2, String q2) {
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
        NBTBase base1 = container1.getTag(query1);
        NBTContainer container2 = arg2.getContainer();
        NBTQuery query2 = arg2.getQuery();
        NBTBase base2 = container2.getTag(query2);
        if (base1 instanceof NBTTagCompound && base2 instanceof NBTTagCompound){
            NBTTagCompound tag1 = (NBTTagCompound) base1;
            NBTTagCompound tag2 = (NBTTagCompound) base2;
            tag1.add(tag2.asMap());
            container1.setTag(query1,tag1);
            caller.send(PowerNBT.plugin.translate("success_add") + getNBTShortView(tag2, null));
        } else if (base1 instanceof NBTTagList && base2 instanceof NBTTagList){
            NBTTagList list1 = (NBTTagList) base1;
            NBTTagList list2 = (NBTTagList) base2;
            list1.addAll(list2.asList());
            container1.setTag(query1,list1);
            caller.send(PowerNBT.plugin.translate("success_add") + getNBTShortView(list2, null));
        } else if (base1 instanceof NBTTagNumericArray && base2 instanceof NBTTagNumericArray){
            NBTTagNumericArray list1 = (NBTTagNumericArray) base1;
            NBTTagNumericArray list2 = (NBTTagNumericArray) base2;
            list1.addAll(list2);
            container1.setTag(query1,list1);
            caller.send(PowerNBT.plugin.translate("success_add") + getNBTShortView(list2, null));
        } else if (base1 instanceof NBTTagString && base2 instanceof NBTTagString){
            NBTTagString s1 = (NBTTagString) base1;
            NBTTagString s2 = (NBTTagString) base2;
            s1.set(s1.get().concat(s2.get()));
            container1.setTag(query1,s1);
            caller.send(PowerNBT.plugin.translate("success_add") + getNBTShortView(s2, null));
        } else {
            caller.send(PowerNBT.plugin.translate("fail_add"));
        }
    }
}
