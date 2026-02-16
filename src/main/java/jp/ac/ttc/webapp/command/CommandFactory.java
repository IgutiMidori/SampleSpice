package jp.ac.ttc.webapp.command;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import jp.ac.ttc.webapp.context.RequestContext;

public class CommandFactory {

    public static AbstractCommand getCommand(RequestContext rc) {
        AbstractCommand command = null;
        Properties prop = new Properties();

        try {
            InputStream is = CommandFactory.class.getClassLoader().getResourceAsStream("command.properties"); 
            if(is == null) {
                throw new FileNotFoundException("property file 'command.properties' not found in the classpath");
            }
            prop.load(is);

            String name = prop.getProperty(rc.getCommandPath());
            Class<?> c = Class.forName(name);

            command = (AbstractCommand)c.getDeclaredConstructor().newInstance();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return command;
    }
}
