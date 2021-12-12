package Writter;

import com.java_polytech.pipeline_interfaces.*;

import java.io.IOException;
import java.io.OutputStream;

public class Writter implements IWriter {
    private OutputStream output = null;
    public RC setOutputStream(OutputStream stream){
        this.output = stream;
        return RC.RC_SUCCESS;
    }
    public RC consume(byte[] data)  {
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

    public RC setConfig(String cfgFileName) {
        return RC.RC_SUCCESS;
    }
}


