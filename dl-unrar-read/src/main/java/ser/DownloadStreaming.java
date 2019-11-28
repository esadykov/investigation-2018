package ser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;

public class DownloadStreaming {
    private static final Logger logger = LoggerFactory.getLogger(DownloadStreaming.class);

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void main(String[] args) throws Exception {

        String DIST_FOLDER = "downloaded";
        URL DOWNLOAD_FROM = new URL("http://data.nalog.ru/Public/Downloads/20190506/fias_delta_xml.rar");
//        URL DOWNLOAD_FROM = new URL("http://data.nalog.ru/Public/Downloads/20190506/fias_xml.rar");

        File destinationFolder = new File(DIST_FOLDER);
        if (!destinationFolder.exists()) {
            logger.info("create unpacked folder: {}", destinationFolder.mkdir());
        }
        final File file = new File(DIST_FOLDER, "some_fias.rar");
        try {
            if (file.exists())
                file.delete();
            file.createNewFile();
        } catch (IOException e) {
            logger.error("can not create file {}", file.getAbsolutePath(), e);
        }

        long c = 0;
        do {
            try (OutputStream fos = new FileOutputStream(file); InputStream fis = DOWNLOAD_FROM.openStream()) {
                while (fis.available() > 0) {
                    fos.write(fis.read());
                    c++;
                    if (c % (1024 * 1024) == 0)
                        logger.info("{} mb downloaded", c / (1024 * 1024));
                }
                logger.info("{} b downloaded", c);

            } catch (Exception e) {
                logger.error(e.getMessage());
            }

        } while (c == 0);
    }
}
