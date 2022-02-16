package Executer;

import  com.java_polytech.pipeline_interfaces.*;
import Grammar.*;

import java.io.IOException;

public class Executer extends ExecuterGrammar implements IExecutor{
    private byte KEY;
    private IConsumer iConsumer = null;
    public RC consume(byte[] data)
    {
        if(data != null) {
            for (int i = 0; i < data.length; i++) {
                data[i] ^= KEY;
            }
        }
        iConsumer.consume(data);
        return RC.RC_SUCCESS;
    }
    public RC setConsumer(IConsumer consume) {
        iConsumer = consume;
        return RC.RC_SUCCESS;
    }

    public RC setConfig(String cfgFileName) {
        SA sa = new SA();
        sa.setGrammar(new ExecuterGrammar());
        Config SYNTAX_ARRAY;
        System.out.println("Executer massage:");
        try {
            SYNTAX_ARRAY = sa.get_config(cfgFileName);
            KEY = Byte.parseByte((String) SYNTAX_ARRAY.Search(CONFIG[config.KEY.param]).get_array()[0].get_KEY());
            System.out.println("KEY: " + KEY);
        }
        catch (NullPointerException | IOException e) {
            return RC.RC_EXECUTOR_CONFIG_FILE_ERROR;
        }
        catch (NumberFormatException e){
            return  RC.RC_EXECUTOR_CONFIG_SEMANTIC_ERROR;
        }
        return RC.RC_SUCCESS;
    }
}
