package Manager;


import Grammar.*;
import com.java_polytech.pipeline_interfaces.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Manager extends ManagerGrammar {
    final static HashMap<String, Config> CONFIG = new HashMap<>();
    private IWriter WRITE = null;
    private IReader READ = null;
    private IExecutor[] EXECUTORS = null;

    private RC readClass(Object name_class, Object path, Object name,Map<String,Class<?>> Executers) {
        /*
        * function for semantic parsing of a class (Writer,Executer,Reader)
        * PARAM[IN]
        * name_class - name of class
        * path - location of class
        * name - ID name
        * Executers - array of exuters classes
        * PARAM[OUT]
        * Executers
        * Write
        * Read
        */
        URLClassLoader cl;
        Class<?> cls;
        try {
            cl = URLClassLoader.newInstance(new URL[]{new URL("jar:file:" + path + "!/")});
        } catch (MalformedURLException e) {
            return RC.RC_MANAGER_CONFIG_GRAMMAR_ERROR;
        }
        try {
            cls = cl.loadClass(name_class.toString());
        } catch (ClassNotFoundException e) {
            return new RC(RC.RCWho.UNKNOWN,RC.RCType.CODE_FAILED_TO_READ,"INVALID " + name_class + " class");
        }

        if (!cls.isInterface()) {
            try {
                if (IReader.class.isAssignableFrom(cls)) {
                    System.out.println("find reader :" + cls.getName()+ " name: " + name);
                    READ = (IReader) cls.newInstance();
                }
            } catch (InstantiationException | IllegalAccessException e) {
                return RC.RC_MANAGER_INVALID_READER_CLASS;
            }
            try {
                if (IWriter.class.isAssignableFrom(cls)) {
                    System.out.println("find writter:" + cls.getName()+ " name: " + name);
                    WRITE = (IWriter) cls.newInstance();
                }
            }catch (InstantiationException | IllegalAccessException e) {
                return RC.RC_MANAGER_INVALID_WRITER_CLASS;
            }
            if (IExecutor.class.isAssignableFrom(cls)) {
                System.out.println("find executer:" + cls.getName() + " name: " + name);
                Executers.put((String) name, cls);

            }
        }
        return RC.RC_SUCCESS;

    }
    private RC SEMANTIC_READ_CLASS(String name_of_classes,Map<String,Class<?>> Executers) {
        /*
        * function for semantic analysis
        * PARAM[IN]
        * name_of_classes - ID of CONFIG for semantic analysis classes
        * PARAM[OUT]
        * Writer - class WRITER (initialized)
        * Read   - class READER (initialized)
        */
        RC RC_valid = RC.RC_SUCCESS;
        try {
            for (Config name_class : CONFIG.get(name_of_classes).get_array()) {
                for (Config path_class : name_class.get_array()) {
                    for (Config name : path_class.get_array()) {
                        RC_valid = readClass(name_class.get_KEY(), path_class.get_KEY(), name.get_KEY(),Executers);
                        if (!RC_valid.isSuccess()) {
                            return RC_valid;
                        }
                    }
                }
            }
        }catch (Exception e) {
            return RC.RC_MANAGER_CONFIG_SEMANTIC_ERROR;
        }
        return RC_valid;
    }
    private RC READ_CLASS(){
        /*
        * stage of semantic analysis program
        * PARAM[IN]:
        * PARAM[OUT]:
        * Executers - array of initialized executer object
        * Read - initialized reader class
        */
        HashMap<String,Class<?>> Executers = new HashMap<>();



        RC RC_valid;
        RC_valid = SEMANTIC_READ_CLASS(CONFIG_GRAMMAR[config.EXECUTERS.param],Executers);
        if(!RC_valid.isSuccess()){
            return RC_valid;
        }
        RC_valid = SEMANTIC_READ_CLASS(CONFIG_GRAMMAR[config.WRITER_CLASS.param],null);
        if(!RC_valid.isSuccess()){
            return RC_valid;
        }
        RC_valid = SEMANTIC_READ_CLASS(CONFIG_GRAMMAR[config.READER_CLASS.param],null);
        if(!RC_valid.isSuccess()){
            return RC_valid;
        }
        RC_valid = SET_POSITION_EXECUTER_OBJECTS(Executers);
        return RC_valid;
    }
    private RC SEMANTIC(Config SYNTAX_ARRAY){
        /*
        * stage of semantic analysis program
        */
        if(SYNTAX_ARRAY ==  null) {return RC.RC_MANAGER_CONFIG_SEMANTIC_ERROR;}
        try {
            Config keys;
            for (String s : CONFIG_GRAMMAR) {
                keys = SYNTAX_ARRAY.Search(s);
                CONFIG.put((String) keys.get_KEY(), keys);
            }
        }catch (RuntimeException e){
            return RC.RC_MANAGER_CONFIG_SEMANTIC_ERROR;
        }
        return RC.RC_SUCCESS;
    }
    private RC SET_STREAM_INPUT() {
        /*
        * Initialization input stream of READER
        * PARAM[IN/OUT]
        * READ
        */
        try{
            System.out.println((String) CONFIG.get(CONFIG_GRAMMAR[config.INPUT.param]).get_array()[0].get_KEY());
            FileInputStream stream_input = new FileInputStream((String) CONFIG.get(CONFIG_GRAMMAR[config.INPUT.param]).get_array()[0].get_KEY());
            String name_reader = (String) CONFIG.get(CONFIG_GRAMMAR[config.READER_CLASS.param]).get_array()[0].get_array()[0].get_array()[0].get_KEY();
            READ.setInputStream(stream_input);
            return READ.setConfig((String) CONFIG.get(CONFIG_GRAMMAR[config.CONFIG.param]).Search(name_reader).get_array()[0].get_KEY());
        } catch (FileNotFoundException  e) {
            return RC.RC_MANAGER_INVALID_INPUT_FILE;
        } catch (NumberFormatException | NullPointerException e){
            return RC.RC_MANAGER_CONFIG_SEMANTIC_ERROR;
        }
    }
    private RC SET_STREAM_OUTPUT(){
        /*
        * Initialization output stream of WRITER
        * PARAM[IN/OUT]
        * WRITE
        */
        try {
            FileOutputStream stream_out = new FileOutputStream((String) CONFIG.get(CONFIG_GRAMMAR[config.OUTPUT.param]).get_array()[0].get_KEY());
            WRITE.setOutputStream(stream_out);
        }catch (FileNotFoundException e){
            return RC.RC_MANAGER_INVALID_OUTPUT_FILE;
        }catch (NullPointerException e ){
            return RC.RC_MANAGER_INVALID_WRITER_CLASS;
        }
        return RC.RC_SUCCESS;
    }
    private RC SET_POSITION_EXECUTER_OBJECTS(Map<String,Class<?>> Executers){
        /*
        * function for create positional array
        * PARAM[OUT]
        * position_executer_objects - positional array
        */
        // position array
        RC  RC_valid;
        String[] position_array = CONFIG.get(CONFIG_GRAMMAR[config.POSITION.param]).key_set();
        Arrays.sort(position_array);

        // Position_array to Position of executers array
        EXECUTORS = new IExecutor[position_array.length];
        String name;
        try {
            for (int i = 0; i < position_array.length; i++) {
                name = (String) CONFIG.get(CONFIG_GRAMMAR[config.POSITION.param]).Search(position_array[i]).get_array()[0].get_KEY() ;
                EXECUTORS[i] = (IExecutor) Executers.get(name).newInstance();
                RC_valid= EXECUTORS[i].setConfig((String) CONFIG.get(CONFIG_GRAMMAR[config.CONFIG.param]).Search(name).get_array()[0].get_KEY());
               if(!RC_valid.isSuccess()){
                   return RC_valid;
               }
            }
        }catch (NullPointerException e ){
            return RC.RC_MANAGER_CONFIG_SEMANTIC_ERROR;
        }
        catch (InstantiationException | IllegalAccessException e) {
            return RC.RC_MANAGER_INVALID_EXECUTOR_CLASS;
        }
        return RC.RC_SUCCESS;
    }
    private RC SINTAX(String config_name){
        /*
        * syntactic analysis of (config_name)-file
        * PARAM[in]
        * config_name - name of config file
        * PARAM[out]
        * CONFIG - array of the parsing result
        */
        Config SYNTAX_ARRAY;
        if(config_name == null){
            return RC.RC_MANAGER_CONFIG_GRAMMAR_ERROR;
        }
        SA sa = new SA();
        sa.setGrammar(new ManagerGrammar());
        try {
            SYNTAX_ARRAY = sa.get_config(config_name);
        }catch (Exception e){
            return RC.RC_MANAGER_CONFIG_GRAMMAR_ERROR;
        }
        return SEMANTIC(SYNTAX_ARRAY);
    }
    public RC SETUP_MANAGER(String config_name){
        /*
        SETUP MANAGER
        */
        RC rc;
        rc = SINTAX(config_name);
        if(!rc.isSuccess()){
            return rc;
        }
        rc = READ_CLASS();
        if(!rc.isSuccess()){
            return rc;
        }
        rc = SET_STREAM_INPUT();
        if(!rc.isSuccess()){
            return rc;
        }
        rc = SET_STREAM_OUTPUT();
        if(!rc.isSuccess()) {
            return rc;
        }
        return rc;
    }
    public RC Run() {
        RC RC_valid;
        if(EXECUTORS.length == 0){
            RC_valid = READ.setConsumer(WRITE);
            if(!RC_valid.isSuccess()){
                return RC_valid;
            }
        }else {
            RC_valid = READ.setConsumer(EXECUTORS[0]);
            if(!RC_valid.isSuccess()){
                return RC_valid;
            }
            for(int i = 0;i < EXECUTORS.length - 1;i++){
                RC_valid = EXECUTORS[i].setConsumer(EXECUTORS[i + 1]);
                if(!RC_valid.isSuccess()){
                    return RC_valid;
                }
            }
            RC_valid = EXECUTORS[EXECUTORS.length - 1].setConsumer(WRITE);
            if(!RC_valid.isSuccess()){
                return RC_valid;
            }
        }
        READ.run();
        return RC.RC_SUCCESS;
    }
}

