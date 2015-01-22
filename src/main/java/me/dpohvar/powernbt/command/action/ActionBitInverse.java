package me.dpohvar.powernbt.command.action;

import me.dpohvar.powernbt.nbt.NBTBase;
import me.dpohvar.powernbt.nbt.NBTContainer;
import me.dpohvar.powernbt.nbt.NBTTagNumeric;
import me.dpohvar.powernbt.utils.Caller;
import me.dpohvar.powernbt.utils.NBTQuery;
import me.dpohvar.powernbt.utils.NBTViewer;

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
        NBTBase base1 = container1.getCustomTag(query1);
        if (!(base1 instanceof NBTTagNumeric)){
            throw new RuntimeException(plugin.translate("error_null"));
        }
        long baseValue = ((Number)((NBTTagNumeric)base1).get()).longValue();
        ((NBTTagNumeric)base1).setNumber( ~baseValue );
        container1.setCustomTag(query1, base1);
        caller.send(plugin.translate("success_edit") + NBTViewer.getShortValueWithPrefix(base1,false));

    }
}
