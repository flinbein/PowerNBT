package me.dpohvar.powernbt.command.action;

import me.dpohvar.powernbt.nbt.NBTBase;
import me.dpohvar.powernbt.nbt.NBTContainer;
import me.dpohvar.powernbt.nbt.NBTTagNumeric;
import me.dpohvar.powernbt.utils.Caller;
import me.dpohvar.powernbt.utils.NBTQuery;
import me.dpohvar.powernbt.utils.NBTViewer;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public class ActionMultiply extends Action {

    private final Caller caller;
    private final Argument arg1;
    private final Argument arg2;

    public ActionMultiply(Caller caller, String o1, String q1, String o2, String q2) {
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
        if (!(base2 instanceof NBTTagNumeric)){
            throw new RuntimeException(plugin.translate("error_null"));
        }
        if (base1 == null) {
            base1 = base2.clone();
            ((NBTTagNumeric)base1).setNumber(0);
        }
        Number result;
        Number number1 = (Number)((NBTTagNumeric)base1).get();
        Number number2 = (Number)((NBTTagNumeric)base2).get();
        if (number1 instanceof Float || number1 instanceof Double || number2 instanceof Float || number2 instanceof Double){
            result = number1.doubleValue() * number2.doubleValue();
        } else {
            result = number1.longValue() * number2.longValue();
        }

        ((NBTTagNumeric)base1).setNumber( result );
        container1.setCustomTag(query1, base1);
        caller.send(plugin.translate("success_edit") + NBTViewer.getShortValueWithPrefix(base1,false));

    }
}
