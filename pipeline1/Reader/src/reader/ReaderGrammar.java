package reader;

import  Grammar.*;
import java.util.Arrays;

public class ReaderGrammar implements IGrammar{
    final static protected long BUFFER_SIZE = 2_147_483_648L;
    final private static String whitespace = " ";
    protected enum config{
        BUFFER_SIZE(0);
        final public int param;
        config(int i){
            param = i;
        }
    }
    final protected static  String[] CONFIG = {
            "BUFFER_SIZE"
    };
    public boolean is_invalid(String a) {
        return (Arrays.asList(CONFIG).contains(a));
    }

    public String get_whitespace() {
        return whitespace;
    }
}
