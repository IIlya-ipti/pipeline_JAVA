package reader;


import Grammar.IGrammar;
import Grammar.SA;
import Grammar.Config;

import com.java_polytech.pipeline_interfaces.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class Reader extends ReaderGrammar implements IReader{
    private InputStream stream = null;
    IConsumer iConsumer = null;
    private int BUFFER_SIZE;
    public RC setInputStream(InputStream inputstream) {
            stream = inputstream;
            return RC.RC_SUCCESS;
    }
    public RC setConsumer(IConsumer consume)  {
        iConsumer = consume;
        return RC.RC_SUCCESS;
    }

    public RC run(){
        byte[] input = new byte[BUFFER_SIZE] ;
        int length = 1;
        while(length > 0) {
            try {
                length = stream.read(input);
            } catch (IOException e) {
                return RC.RC_READER_FAILED_TO_READ;
            }
            try {
                if(length > -1) {
                    iConsumer.consume(Arrays.copyOf(input, length));
                }else{
                    iConsumer.consume(null);
                }
            } catch (NegativeArraySizeException e) {
                return RC.RC_READER_CONFIG_FILE_ERROR;
            }
        }
        System.out.println("file is read");
        return RC.RC_SUCCESS;
    }
    public RC setConfig(String cfgFileName) {
        SA sa = new SA();
        sa.setGrammar(new ReaderGrammar());
        System.out.println("Reader massage:");
        try {
            Config SYNTAX_ARRAY = sa.get_config(cfgFileName);
            BUFFER_SIZE = Integer.parseInt((String) SYNTAX_ARRAY.Search(CONFIG[config.BUFFER_SIZE.param]).get_array()[0].get_KEY());
            if(BUFFER_SIZE <= 0){
                return RC.RC_READER_CONFIG_SEMANTIC_ERROR;
            }
            System.out.println("BUFFER_SIZE: " + BUFFER_SIZE);
        }
        catch (NullPointerException | IOException e) {
            return RC.RC_READER_CONFIG_FILE_ERROR;
        }
        catch (NumberFormatException e){
            return  RC.RC_READER_CONFIG_SEMANTIC_ERROR;
        }
        return RC.RC_SUCCESS;
    }
}
