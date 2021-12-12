package Composition;

import Manager.*;
import com.java_polytech.pipeline_interfaces.*;


public class Main  {
    public static void main(String[] args) {
        // потоки
        // в статическом методе SINTAX синтаксический и СЕМАНТИЧЕСКИЙ разор
        RC RC_valid;
        if(args.length == 0){
            System.out.println("config is not specified");
            return;
        }
        System.out.println("Config is " + args[0]);
        Manager manager = new Manager();
        RC_valid = manager.SETUP_MANAGER(args[0]);

        if(!RC_valid.isSuccess()){
            System.out.println(RC_valid.info);
            return;
        }

        RC_valid = manager.Run();
        if (!RC_valid.isSuccess()) {
            System.out.println(RC_valid.info);
        }

    }
}
