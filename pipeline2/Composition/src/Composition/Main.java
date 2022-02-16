package Composition;

import Manager.*;
import com.java_polytech.pipeline_interfaces.*;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;


public class Main  {
    public static final Logger logger = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) throws IOException {
        // потоки
        // в статическом методе SINTAX синтаксический и СЕМАНТИЧЕСКИЙ разор
        RC RC_valid;
        logger.addHandler(new FileHandler("%h/Java_2_log_Zolotukhin_Ilya_%g.log"));
        if(args.length == 0){
            logger.info("config is not specified");
            return;
        }
        System.out.println("Config is " + args[0]);
        Manager manager = new Manager();
        RC_valid = manager.SETUP_MANAGER(args[0]);
        if(!RC_valid.isSuccess()){
            logger.info(RC_valid.info);
            return;
        }
        RC_valid = manager.Run();
        if (!RC_valid.isSuccess()) {
            logger.info(RC_valid.info);
        }

    }
}
