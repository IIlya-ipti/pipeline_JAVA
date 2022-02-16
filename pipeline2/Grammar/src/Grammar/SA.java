package Grammar;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class SA {
    IGrammar iGrammar = null;
    public void setGrammar(IGrammar igrammar){
        iGrammar = igrammar;
    }
    public Config get_config(String config_name) throws IOException {
        if(iGrammar == null){
            return null;
        }
        Config config = new Config();
        BufferedReader file = new BufferedReader(new FileReader(config_name));
        String[] stream_out;
        while (file.ready()) {
            stream_out = file.readLine().trim().replaceAll("\\s+"," ").split(iGrammar.get_whitespace());
            if(iGrammar.is_invalid(stream_out[0])){
                config.Add(stream_out);
            }
        }
        return config;
    }
}
