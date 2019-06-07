package utils;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
class ConfigurationTest {

    @Test
    void getValue() {
        Random random = new Random();
        long randVal = random.nextLong();
        String key = "test" + randVal;
        String val = "val " + randVal;
        String actualVal = null;
        final String pathname = Config.class.getClassLoader().getResource("app.properties").getPath();
        log.debug(pathname);
        try (FileWriter fw = new FileWriter(new File(pathname), true)) {
            final String lineToWrite = key + " = " + val + System.getProperty("line.separator");
            fw.write(lineToWrite);
            fw.flush();
            actualVal = Config.getValue(key);
        } catch (IOException e) {
            log.error(e);
        }
        assertEquals(val, actualVal);
    }
}
