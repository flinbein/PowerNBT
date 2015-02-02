package me.dpohvar.powernbt.command.action;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.exception.NBTTagNotFound;
import me.dpohvar.powernbt.exception.NBTTagUnexpectedType;
import me.dpohvar.powernbt.nbt.*;
import me.dpohvar.powernbt.utils.Caller;
import me.dpohvar.powernbt.utils.NBTQuery;
import me.dpohvar.powernbt.utils.NBTViewer;

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
        NBTBase base1 = container1.getCustomTag(query1);
        NBTContainer container2 = arg2.getContainer();
        NBTQuery query2 = arg2.getQuery();
        NBTBase base2 = container2.getCustomTag(query2);
        if (base1 == null) {
            if (base2 instanceof NBTTagCompound) base1 = new NBTTagCompound();
            if (base2 instanceof NBTTagList) base1 = new NBTTagList();
            if (base2 instanceof NBTTagByteArray) base1 = new NBTTagByteArray();
            if (base2 instanceof NBTTagIntArray) base1 = new NBTTagIntArray();
            if (base2 instanceof NBTTagNumeric) {
                base1 = base2.clone();
                ((NBTTagNumeric)base1).setNumber(0);
            }
        }
        if (base1 instanceof NBTTagCompound && base2 instanceof NBTTagCompound){
            NBTTagCompound tag1 = (NBTTagCompound) base1;
            NBTTagCompound tag2 = (NBTTagCompound) base2;
            NBTCompound cmp1 = NBTCompound.forNBT(base1.getHandle());
            NBTCompound cmp2 = NBTCompound.forNBT(base2.getHandle());
            cmp1.merge(cmp2);
            container1.setCustomTag(query1,tag1);
            caller.send(PowerNBT.plugin.translate("success_add") + NBTViewer.getShortValueWithPrefix(tag2, false));
        } else if (base1 instanceof NBTTagList && base2 instanceof NBTTagList){
            NBTTagList list1 = (NBTTagList) base1;
            NBTTagList list2 = (NBTTagList) base2;
            list1.addAll(list2);
            container1.setCustomTag(query1,list1);
            caller.send(PowerNBT.plugin.translate("success_add") + NBTViewer.getShortValueWithPrefix(list2,false));
        } else if (base1 instanceof NBTTagNumericArray && base2 instanceof NBTTagNumericArray){
            NBTTagNumericArray list1 = (NBTTagNumericArray) base1;
            NBTTagNumericArray list2 = (NBTTagNumericArray) base2;
            list1.addAll(list2);
            container1.setCustomTag(query1,list1);
            caller.send(PowerNBT.plugin.translate("success_add") + NBTViewer.getShortValueWithPrefix(list2,false));
        } else if (base1 instanceof NBTTagString && base2 instanceof NBTTagString){
            NBTTagString s1 = (NBTTagString) base1;
            NBTTagString s2 = (NBTTagString) base2;
            s1.set(s1.get().concat(s2.get()));
            container1.setCustomTag(query1,s1);
            caller.send(PowerNBT.plugin.translate("success_add") + NBTViewer.getShortValueWithPrefix(s2,false));
        } else if (base1 instanceof NBTTagNumeric && base2 instanceof NBTTagNumeric){
            NBTTagNumeric n1 = (NBTTagNumeric) base1.clone();
            NBTTagNumeric n2 = (NBTTagNumeric) base2;
            Number x1 = (Number)n1.get();
            Number x2 = (Number)n2.get();
            if(x1 instanceof Float || x1 instanceof Double){
                x1 = x1.doubleValue()+x2.doubleValue();
            } else {
                x1 = x1.longValue()+x2.longValue();
            }
            n1.setNumber(x1);
            container1.setCustomTag(query1, n1);
            caller.send(PowerNBT.plugin.translate("success_add") + NBTViewer.getShortValueWithPrefix(n2,false));
        } else {
            caller.send(PowerNBT.plugin.translate("fail_add"));
        }
    }
}
