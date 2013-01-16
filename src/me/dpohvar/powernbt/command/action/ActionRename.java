package me.dpohvar.powernbt.command.action;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.nbt.NBTBase;
import me.dpohvar.powernbt.nbt.NBTContainer;
import me.dpohvar.powernbt.nbt.NBTQuery;
import me.dpohvar.powernbt.utils.Caller;

import java.util.List;

public class ActionRename extends Action {

    private final Caller caller;
    private final Argument arg1;
    private final String name;
    private final NBTQuery query2;

    public ActionRename(Caller caller, String o1, String q1, String name) {
        this.caller = caller;
        this.arg1 = new Argument(caller, o1, q1);
        this.name = name;
        this.query2 = NBTQuery.fromString(name);
    }

    @Override
    public void execute() {
        if (arg1.needPrepare()) {
            arg1.prepare(this, null, null);
            return;
        }
        NBTContainer container = arg1.getContainer();
        NBTQuery query = arg1.getQuery();
        List<Object> v = query.getParent().getValues();
        v.addAll(query2.getValues());
        NBTQuery newQuery = new NBTQuery(v);
        NBTBase base = container.getTag(query);
        if (base == null) {
            caller.send(PowerNBT.plugin.translate("fail_rename"));
        } else {
            container.removeTag(query);
            container.setTag(newQuery, base);
            caller.send(PowerNBT.plugin.translate("success_rename", name) + getNBTShortView(base, null));
        }
    }
}














