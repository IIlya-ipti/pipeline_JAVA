package Writter;

import Grammar.Config;
import Grammar.SA;
import com.java_polytech.pipeline_interfaces.*;
import functions.functions;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Writter extends WriterGrammar implements IWriter {
    private OutputStream output = null;
    private IProvider iprovider = null;
    private TYPE type = null;
    private byte[] data = null;
    private final Set<TYPE> TYPES = new HashSet<>();
    private IMediator imediator = null;

    private TYPE intersectionType(TYPE[] array1){
        for(TYPE type1 : array1){
            if(TYPES.contains(type1)){
                type = type1;
                return  type1;
            }
        }
        return null;
    }

    @Override
    public RC setOutputStream(OutputStream stream){
        this.output = stream;
        return RC.RC_SUCCESS;
    }

    @Override
    public RC setConfig(String cfgFileName) {
        SA sa = new SA();
        sa.setGrammar(new WriterGrammar());
        Config SYNTAX_ARRAY;
        System.out.println("Writer massage : ");
        try {
            SYNTAX_ARRAY = sa.get_config(cfgFileName);
            Config types = SYNTAX_ARRAY.Search(CONFIG[config.TYPE.param]);
            for(int i = 0;i < types.get_length();i++) {
                TYPE c = functions.string_to_type((String) types.get_array()[i].get_KEY());
                if(c != null) {
                    TYPES.add(c);
                }
            }
            System.out.println("TYPES: " + TYPES);
        }
        catch (NullPointerException | IOException e) {
            return RC.RC_WRITER_CONFIG_FILE_ERROR;
        }
        catch (NumberFormatException e){
            return  RC.RC_WRITER_CONFIG_SEMANTIC_ERROR;
        }
        return RC.RC_SUCCESS;
    }

    @Override
    public RC setProvider(IProvider iProvider) {
        iprovider = iProvider;
        try {
            imediator = iprovider.getMediator(intersectionType(iprovider.getOutputTypes()));
        }catch (NullPointerException e){
            return RC.RC_MANAGER_CONFIG_SEMANTIC_ERROR;
        }
        return RC.RC_SUCCESS;
    }


    @Override
    public RC consume() {
        if(type == null){
            return  RC.RC_WRITER_TYPES_INTERSECTION_EMPTY_ERROR;
        }
        try {
            switch (type) {
                case CHAR_ARRAY: {
                    data = functions.CHAR_TO_BYTE((char[]) imediator.getData());
                    break;
                }
                case BYTE_ARRAY: {
                    data = (byte[]) imediator.getData();
                    break;
                }
                case INT_ARRAY: {
                    data = functions.INTEGER_ARRAY_TO_BYTE_ARRAY((int[]) imediator.getData());
                    break;
                }
            }
        }catch(ArrayIndexOutOfBoundsException e){
            return RC.RC_READER_CONFIG_SEMANTIC_ERROR;
        }
        try {
            if(data == null){
                output.close();
                return RC.RC_SUCCESS;
            }
            output.write(data);
        }catch (Exception e) {
            return RC.RC_WRITER_FAILED_TO_WRITE;
        }
        return RC.RC_SUCCESS;
    }
}


