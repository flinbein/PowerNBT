package me.dpohvar.powernbt.command.action;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.nbt.NBTContainer;
import me.dpohvar.powernbt.nbt.NBTContainerComplex;
import me.dpohvar.powernbt.nbt.NBTContainerVariable;
import me.dpohvar.powernbt.utils.NBTQuery;
import me.dpohvar.powernbt.utils.Caller;

public class ActionSet extends Action {

    private final Caller caller;
    private final Argument arg1;
    private final Argument arg2;

    public ActionSet(Caller caller, String o1, String o2, String q2) {
        this.caller = caller;
        if (o2 == null && q2 == null) o2 = "*";
        this.arg1 = new Argument(caller, o1, null);
        this.arg2 = new Argument(caller, o2, q2);
    }

    @Override
    public void execute() throws Exception {
        if (arg1.needPrepare()) {
            arg1.prepare(this, null, null);
            return;
        }
        NBTContainer containerVar = arg1.getContainer();
        if (!(containerVar instanceof NBTContainerVariable)) {
            throw new RuntimeException(PowerNBT.plugin.translate("error_variablerequired"));
        }
        NBTContainerVariable variable = (NBTContainerVariable) containerVar;
        if (arg2.needPrepare()) {
            arg2.prepare(this, containerVar, null);
            return;
        }
        NBTContainer container = arg2.getContainer();
        NBTQuery query = arg2.getQuery();
        if (query != null && !query.isEmpty()) {
            container = new NBTContainerComplex(container, query);
        }
        variable.setContainer(container);
        caller.send(PowerNBT.plugin.translate("success_select", container.toString(), variable.getVariableName()));
    }
}
