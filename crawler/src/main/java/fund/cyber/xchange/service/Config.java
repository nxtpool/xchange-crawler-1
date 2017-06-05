package fund.cyber.xchange.service;

import fund.cyber.xchange.Main;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Properties;

@Component
public class Config extends Properties {

    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    public Config() {
        super();
        FileInputStream file = null;
        String path = path();
        try {
            try {
                file = new FileInputStream(path);
                load(file);
                file.close();
            } catch (FileNotFoundException e) {
                loadDefault();
                saveDefault();
            }
        } catch (IOException e) {
            logger.error("Load properties file failed.", e);
        } finally {
            IOUtils.closeQuietly(file);
        }
    }

    public int getPort() {
        return Integer.parseInt(getProperty("rest.port", "0"));
    }

    private String path() {
        File jarPath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        return jarPath.getParent() + "/crawler.properties";
    }

    private void loadDefault() {
        InputStream defaultProperties = getClass().getResourceAsStream("/crawler.properties");
        try {
            load(defaultProperties);
        } catch (IOException e) {
            logger.error("Load default properties failed.", e);
        } finally {
            IOUtils.closeQuietly(defaultProperties);
        }
    }

    private void saveDefault() {
        String path = path();
        InputStream defaultProperties = getClass().getResourceAsStream("/crawler.properties");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            IOUtils.copy(defaultProperties,out);
        } catch (IOException e) {
            logger.error("Save default properties to file failed.", e);
        } finally {
            IOUtils.closeQuietly(defaultProperties);
            IOUtils.closeQuietly(out);
        }
    }

}
