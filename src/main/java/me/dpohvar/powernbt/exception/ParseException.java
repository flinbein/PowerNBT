package me.dpohvar.powernbt.exception;

/**
 * Created with IntelliJ IDEA.
 * User: DPOH-VAR
 * Date: 25.06.13
 * Time: 2:12
 */
public class ParseException extends SourceException {

    public ParseException(String string, int row, int col, String reason) {
        super(string, row, col, reason);
    }

    @Override
    public String getMessage() {
        String msg, reason;
        if (getCause() == null) {
            msg = super.getMessage();
            reason = "";
        } else {
            msg = getCause().getMessage();
            reason = getCause().getClass().getSimpleName();

        }
        return reason + " at [" + (row + 1) + ':' + (col + 1) + "]\n" + getErrorString() + (msg == null ? "" : '\n' + msg);
    }
}
