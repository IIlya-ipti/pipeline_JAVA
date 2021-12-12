package Executer;


import Grammar.IGrammar;

import java.util.Arrays;

public class ExecuterGrammar implements IGrammar {
    final static protected long BUFFER_SIZE = 2_147_483_648L;
    final private static String whitespace = " ";
    protected enum config{
        KEY(0);
        final public int param;
        config(int i){
            param = i;
        }
    }
    final protected static  String[] CONFIG = {
            "KEY"
    };
    public boolean is_invalid(String a) {
        return (Arrays.asList(CONFIG).contains(a));
    }
    public String get_whitespace() {
            return whitespace;
    }
}
