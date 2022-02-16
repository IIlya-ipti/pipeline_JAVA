package reader;


import Grammar.SA;
import Grammar.Config;
import functions.*;

import com.java_polytech.pipeline_interfaces.*;


import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class Reader extends ReaderGrammar implements IReader{
    private InputStream stream = null;
    private int BUFFER_SIZE;
    private final Set<TYPE> TYPES = new HashSet<>();
    private byte[] array = null;
    private IMediator imediator = null;
    private IConsumer iConsumer = null;

    public RC setInputStream(InputStream inputstream) {
            stream = inputstream;
            return RC.RC_SUCCESS;
    }
    public RC setConsumer(IConsumer consume)  {
        iConsumer = consume;
        iConsumer.setProvider(this);
        return RC.RC_SUCCESS;
    }

    @Override
    public TYPE[] getOutputTypes() {
        return TYPES.toArray(new TYPE[0]);
    }

    @Override
    public IMediator getMediator(TYPE type) {
        class Mediator implements IMediator {
            @Override
            public Object getData() {
                switch (type) {
                    case BYTE_ARRAY:
                        if(array != null) {
                            return Arrays.copyOf(array, array.length);
                        }
                        return null;
                    case CHAR_ARRAY:
                        return functions.BYTE_TO_CHAR(array);
                    case INT_ARRAY:
                        return functions.BYTE_ARRAY_TO_INTEGER_ARRAY(array);
                }
                return null;
            }
        }
        imediator = new Mediator();
        return imediator;
    }

    public RC run(){
        byte[] bytes = new byte[BUFFER_SIZE] ;
        int length = 1;
        RC RC_type;
        while(length > 0) {
            try {
                length = stream.read(bytes);
            } catch (IOException e) {
                return RC.RC_READER_FAILED_TO_READ;
            }
            try {
                if(length > -1) {
                    array = Arrays.copyOf(bytes,length);
                }else{
                    array = null;
                }
                RC_type = iConsumer.consume();
                if(!RC_type.isSuccess()){
                    return RC_type;
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
            Config types = SYNTAX_ARRAY.Search(CONFIG[config.TYPE.param]);
            for(int i = 0;i < types.get_length();i++) {
                TYPE c = functions.string_to_type((String) types.get_array()[i].get_KEY());
                if(c != null) {
                    TYPES.add(c);
                }
            }
            System.out.println("TYPES " + TYPES);
        }
        catch ( IOException e) {
            return RC.RC_READER_CONFIG_FILE_ERROR;
        }
        catch (NullPointerException | NumberFormatException e){
            return  RC.RC_READER_CONFIG_SEMANTIC_ERROR;
        }
        return RC.RC_SUCCESS;
    }
}
