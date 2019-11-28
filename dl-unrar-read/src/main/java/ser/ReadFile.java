package ser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class ReadFile {
    private static final Logger logger = LoggerFactory.getLogger(ReadFile.class);

    public static void main(String[] args) {
        try (InputStream is = new FileInputStream(new File("test.txt"))) {
            do {
                logger.info("{}", is.read());
                TimeUnit.SECONDS.sleep(1);
            } while (true);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
