package bb_framework.utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Logging {

    private static final String LOG_FILE_PATH = "C:\\tmp\\logs\\bb.log";

    private Logger log;


    public Logging() {
        log = Logger.getGlobal();
        File tmp = new File(LOG_FILE_PATH);
        if(!(tmp.exists())){
            tmp.getParentFile().mkdir();
        }
        FileHandler fh = null;
        try {
            fh = new FileHandler(LOG_FILE_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.addHandler(fh);
        fh.setFormatter(new SimpleFormatter());
        log.setUseParentHandlers(false);
    }

    public void Log(String message, Level level){
        log.log(level,message);
    }

}
