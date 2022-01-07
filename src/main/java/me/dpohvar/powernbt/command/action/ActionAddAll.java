package me.dpohvar.powernbt.command.action;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.api.NBTList;
import me.dpohvar.powernbt.api.NBTManager;
import me.dpohvar.powernbt.nbt.NBTContainer;
import me.dpohvar.powernbt.nbt.NBTType;
import me.dpohvar.powernbt.utils.Caller;
import me.dpohvar.powernbt.utils.NBTQuery;
import me.dpohvar.powernbt.utils.NBTViewer;

import java.util.Arrays;
import java.util.List;

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
        Object base1 = container1.getCustomTag(query1);
        NBTContainer container2 = arg2.getContainer();
        NBTQuery query2 = arg2.getQuery();
        Object base2 = container2.getCustomTag(query2);
        if (base1 == null) {
            base1 = NBTType.fromValue(base2).getDefault();
        }
        if (base1 instanceof NBTCompound cmp1 && base2 instanceof NBTCompound cmp2){
            cmp1.merge(cmp2);
            container1.setCustomTag(query1,cmp1);
            caller.send(PowerNBT.plugin.translate("success_add") + NBTViewer.getShortValueWithPrefix(cmp2, false));
        } else if (base1 instanceof NBTList list1 && base2 instanceof NBTList list2){
            list1.addAll(list2);
            container1.setCustomTag(query1,list1);
            caller.send(PowerNBT.plugin.translate("success_add") + NBTViewer.getShortValueWithPrefix(list2,false));
        } else if (base1 instanceof String s1 && base2 instanceof String s2){
            s1 += s2;
            container1.setCustomTag(query1,s1);
            caller.send(PowerNBT.plugin.translate("success_add") + NBTViewer.getShortValueWithPrefix(s2,false));
        } else if (base1 instanceof Number x1 && base2 instanceof Number x2){
            NBTType x1Type = NBTType.fromValue(x1);
            if(x1 instanceof Float || x1 instanceof Double){
                x1 = x1.doubleValue()+x2.doubleValue();
            } else {
                x1 = x1.longValue()+x2.longValue();
            }
            x1 = (Number) NBTManager.convertValue(x1, x1Type.type);
            container1.setCustomTag(query1, x1);
            caller.send(PowerNBT.plugin.translate("success_add") + NBTViewer.getShortValueWithPrefix(x2,false));
        } else if (base1.getClass().isArray() && base2.getClass().isArray()){
            NBTType baseType = NBTType.fromValue(base1).getBaseType();
            Object[] array1 = NBTManager.convertToObjectArrayOrNull(base1);
            Object[] array2 = NBTManager.convertToObjectArrayOrNull(base2);
            List<Object> list1 = Arrays.asList(array1);
            for (Object val : array2) {
                list1.add(NBTManager.convertValue(val, baseType.type));
            }
            Object result = NBTManager.convertValue(list1, baseType.type);
            container1.setCustomTag(query1,result);
            caller.send(PowerNBT.plugin.translate("success_add") + NBTViewer.getShortValueWithPrefix(base2,false));
        } else {
            caller.send(PowerNBT.plugin.translate("fail_add"));
        }
    }
}
