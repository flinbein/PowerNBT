package me.dpohvar.powernbt.command.action;

import me.dpohvar.powernbt.utils.Caller;

public class ActionBitOr extends ActionBiLong {

    public ActionBitOr(Caller caller, String o1, String q1, String o2, String q2) {
        super(caller, o1, q1, o2, q2);
    }

    @Override
    long operation(long arg1, long arg2) {
        return arg1 | arg2;
    }
}
