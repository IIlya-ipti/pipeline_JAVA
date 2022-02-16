package Executer;

import com.java_polytech.pipeline_interfaces.*;
import Grammar.*;
import functions.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Executer extends ExecuterGrammar implements IExecutor{
    private byte KEY;
    private final Set<TYPE> TYPES = new HashSet<>();
    private TYPE type = null;
    public byte[] array = null;
    private IConsumer iConsumer = null;
    private IProvider iprovider = null;
    private IMediator imediator1 = null;
    private IMediator imediator2 = null;

    private TYPE intersectionType(TYPE[] array1){
        for(TYPE type1 : array1){
            if(TYPES.contains(type1)){
                type = type1;
                return  type1;
            }
        }
        return null;
    }
    public RC consume()
    {
        if(type == null){
            return RC.RC_EXECUTOR_TYPES_INTERSECTION_EMPTY_ERROR;
        }
        try {
            switch (type) {
                case CHAR_ARRAY: {
                    array = functions.CHAR_TO_BYTE((char[]) imediator1.getData());
                    break;
                }
                case BYTE_ARRAY: {
                    array = (byte[]) imediator1.getData();
                    break;
                }
                case INT_ARRAY: {
                    array = functions.INTEGER_ARRAY_TO_BYTE_ARRAY((int[]) imediator1.getData());
                    break;
                }
            }
        }catch(ArrayIndexOutOfBoundsException e){
            return RC.RC_READER_CONFIG_SEMANTIC_ERROR;
        }
        if(array != null) {
            for (int i = 0; i < array.length; i++) {
                array[i] ^= KEY;
            }
        }
        return iConsumer.consume();
    }

    @Override
    public RC setConsumer(IConsumer consume) {
        iConsumer = consume;
        consume.setProvider(this);
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
        imediator2 = new Mediator();
        return imediator2;
    }

    @Override
    public RC setConfig(String cfgFileName) {
        SA sa = new SA();
        sa.setGrammar(new ExecuterGrammar());
        Config SYNTAX_ARRAY;
        System.out.println("Executer massage:");
        try {
            SYNTAX_ARRAY = sa.get_config(cfgFileName);
            KEY = Byte.parseByte((String) SYNTAX_ARRAY.Search(CONFIG[config.KEY.param]).get_array()[0].get_KEY());
            System.out.println("KEY: " + KEY);
            Config types = SYNTAX_ARRAY.Search(CONFIG[config.TYPE.param]);
            for(int i = 0;i < types.get_length();i++) {
                TYPE c = functions.string_to_type((String) types.get_array()[i].get_KEY());
                if(c != null) {
                    TYPES.add(c);
                }
            }
            System.out.println("TYPES: " + TYPES);
        }
        catch (IOException e) {
            return RC.RC_EXECUTOR_CONFIG_FILE_ERROR;
        }
        catch (NullPointerException |NumberFormatException e){
            return  RC.RC_EXECUTOR_CONFIG_SEMANTIC_ERROR;
        }
        return RC.RC_SUCCESS;
    }

    @Override
    public RC setProvider(IProvider iProvider) {
        iprovider = iProvider;
        try {
            imediator1 = iprovider.getMediator(intersectionType(iprovider.getOutputTypes()));
        }catch (NullPointerException e){
            return RC.RC_MANAGER_CONFIG_SEMANTIC_ERROR;
        }
        return RC.RC_SUCCESS;
    }

}
