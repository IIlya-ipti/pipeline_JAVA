package functions;

import java.nio.ByteBuffer;
import com.java_polytech.pipeline_interfaces.*;

public class functions {
    static public byte[] CHAR_TO_BYTE(char[] ar){
        if(ar == null) return null;
        return new String(ar).getBytes();
    }
    static public char[] BYTE_TO_CHAR(byte[] ar){
        if(ar == null)return null;
        return new String(ar).toCharArray();
    }
    static public int[] BYTE_ARRAY_TO_INTEGER_ARRAY(byte[] bytes){
        if(bytes == null) return null;
        final int i1 = (bytes.length + 3) / 4;
        if((i1 * 4 - bytes.length)> 0) {
            System.out.println("DIFFERENCE OF INTEGER : " + (i1 * 4 - bytes.length) + " bytes ");
        }
        int[] ints = new int[(bytes.length + 3)/4];
        for(int i = 0;i < ints.length;i++){
            ints[i] = ByteBuffer.wrap(new byte[]{bytes[i*4],bytes[i*4 + 1],bytes[i*4 + 2],bytes[i*4 + 3]}).getInt();
        }
        return ints;
    }
    static public byte[] INTEGER_ARRAY_TO_BYTE_ARRAY(int[] ints){
        if(ints == null)return null;
        byte[] bytes = new byte[ints.length * 4];
        for(int i = 0;i < ints.length;i++){
            byte []p = ByteBuffer.allocate(4).putInt(ints[i]).array();
            bytes[4*i] = p[0];
            bytes[4*i + 1] = p[1];
            bytes[4*i + 2] = p[2];
            bytes[4*i + 3] = p[3];
        }
        return bytes;
    }
    static public TYPE string_to_type(String name){
        if(name  == null) return null;
        if(name.equals(TYPE.INT_ARRAY.toString())){
            return TYPE.INT_ARRAY;
        }
        if(name.equals(TYPE.BYTE_ARRAY.toString())){
            return TYPE.BYTE_ARRAY;
        }
        if(name.equals(TYPE.CHAR_ARRAY.toString())){
            return TYPE.CHAR_ARRAY;
        }
        return null;
    }
}
