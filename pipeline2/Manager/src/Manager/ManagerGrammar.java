package Manager;

import Grammar.IGrammar;

import java.util.Arrays;
import java.util.Objects;

import java.util.Arrays;
import java.util.Objects;

public class ManagerGrammar implements IGrammar {
    final protected static  String[] CONFIG_GRAMMAR = {
            "INPUT","OUTPUT","WRITER_CLASS","CONFIG","READER_CLASS","EXECUTERS","POSITION"
    };
    final private static String whitespace = " "; // разделитель
    protected enum config {
        INPUT(0),
        OUTPUT(1),
        WRITER_CLASS(2),
        CONFIG(3),
        READER_CLASS(4),
        EXECUTERS(5),
        POSITION(6);
        public final int param;
        config(int i){
            param = i;
        }
    }
    public boolean is_invalid(String a) {
        return (Arrays.asList(CONFIG_GRAMMAR).contains(a));
    }
    public String get_whitespace() {
        return whitespace;
    }
}
