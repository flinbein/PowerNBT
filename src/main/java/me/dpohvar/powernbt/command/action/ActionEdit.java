package me.dpohvar.powernbt.command.action;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.exception.NBTTagNotFound;
import me.dpohvar.powernbt.exception.NBTTagUnexpectedType;
import me.dpohvar.powernbt.nbt.NBTBase;
import me.dpohvar.powernbt.nbt.NBTContainer;
import me.dpohvar.powernbt.utils.NBTQuery;
import me.dpohvar.powernbt.utils.Caller;
import me.dpohvar.powernbt.utils.NBTViewer;

public class ActionEdit extends Action {

    private final Caller caller;
    private final Argument arg1;
    private final Argument arg2;

    public ActionEdit(Caller caller, String o1, String q1, String o2, String q2) {
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
        NBTContainer container = arg1.getContainer();
        NBTQuery query = arg1.getQuery();
        if (arg2.needPrepare()) {
            arg2.prepare(this, container, query);
            return;
        }
        NBTBase base = arg2.getContainer().getCustomTag(arg2.getQuery());
        if (base == null) throw new RuntimeException(PowerNBT.plugin.translate("error_null"));
        try{
            container.setCustomTag(query, base);
            caller.send(PowerNBT.plugin.translate("success_edit") + NBTViewer.getShortValueWithPrefix(base, false));
        } catch (Exception e){
            throw new RuntimeException( PowerNBT.plugin.translate("fail_edit", query.toString()) , e);
        }
    }
}
