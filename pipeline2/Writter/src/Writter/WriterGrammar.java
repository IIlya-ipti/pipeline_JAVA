package Writter;


import Grammar.IGrammar;

import java.util.Arrays;

public class WriterGrammar implements IGrammar {
    final private static String whitespace = " ";
    protected enum config{
        TYPE(0);
        final public int param;
        config(int i){
            param = i;
        }
    }
    final protected static  String[] CONFIG = {
            "TYPE"
    };
    @Override
    public boolean is_invalid(String a) {
        return (Arrays.asList(CONFIG).contains(a));
    }

    @Override
    public String get_whitespace() {
        return whitespace;
    }
}
