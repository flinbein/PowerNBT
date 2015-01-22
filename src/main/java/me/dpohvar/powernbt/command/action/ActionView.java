package me.dpohvar.powernbt.command.action;

import me.dpohvar.powernbt.nbt.NBTContainer;
import me.dpohvar.powernbt.utils.NBTQuery;
import me.dpohvar.powernbt.utils.Caller;
import me.dpohvar.powernbt.utils.NBTViewer;

public class ActionView extends Action {

    private final Caller caller;
    private final Argument arg;
    private String[] args;

    private ActionView(Caller caller, String object, String query) {
        this.caller = caller;
        this.arg = new Argument(caller, object, query);
    }

    public ActionView(Caller caller, String object, String query, String args) {
        this(caller, object, query);
        if(args!=null) this.args = args.split(",|\\.|-");
    }

    @Override
    public void execute() throws Exception {
        if (arg.needPrepare()) {
            arg.prepare(this, null, null);
            return;
        }
        NBTContainer container = arg.getContainer();
        NBTQuery query = arg.getQuery();
        int start=-1, end=-1;
        boolean hex = false;
        boolean bin = false;
        if(args!=null) {
            for(String s:args){
                if (s.equalsIgnoreCase("hex")||s.equalsIgnoreCase("h")) hex=true;
                if (s.equalsIgnoreCase("bin")||s.equalsIgnoreCase("b")) bin=true;
                else if (s.equalsIgnoreCase("full")||s.equalsIgnoreCase("f")||s.equalsIgnoreCase("all")||s.equalsIgnoreCase("a")) {
                    start=0; end=Integer.MAX_VALUE;
                }
                else if (s.matches("[0-9]+")){
                    if(end==-1) end = Integer.parseInt(s);
                    else start = Integer.parseInt(s);
                }
            }
        }
        if(start==-1) start=0;
        if(end==-1) end=0;
        if(start>end){int t=start; start=end; end=t;}
        String answer = NBTViewer.getFullValue(container.getCustomTag(query), start, end, hex, bin);
        caller.send(answer);
    }
}









