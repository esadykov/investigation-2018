package ser;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.impl.InputStreamVolumeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DownloadUnrarReadStreaming {
    private static final Logger logger = LoggerFactory.getLogger(DownloadUnrarReadStreaming.class);

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void main(String[] args) throws Exception {

        String DIST_FOLDER = "unpacked";
//        URL DOWNLOAD_FROM = new URL("http://data.nalog.ru/Public/Downloads/20190506/fias_delta_xml.rar");
//        URL DOWNLOAD_FROM = new URL("http://data.nalog.ru/Public/Downloads/20190506/fias_xml.rar");
        URL DOWNLOAD_FROM = new URL("https://data.nalog.ru/Public/Downloads/20191021/fias_xml.rar");
//        File DOWNLOAD_FROM = new File("C:\\my\\investigation-2018\\unpacked\\12unpacked.rar");
//        File DOWNLOAD_FROM = new File("C:\\Users\\esadykov\\Downloads\\fias_delta_xml.rar");


        File destinationFolder = new File(DIST_FOLDER);
        if (!destinationFolder.exists()) {
            logger.info("create unpacked folder: {}", destinationFolder.mkdir());
        }

        final ExecutorService executor = Executors.newFixedThreadPool(6);
        try {
            InputStream fis = null;
            while (fis == null) {
                try {
                    fis = DOWNLOAD_FROM.openStream();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
            final InputStream is = fis;
            try {
                logger.info("new archive wrapper begin");

                Archive newArchive = new Archive(new InputStreamVolumeManager(is/*new FileInputStream(fiasFile*/), null,
                        (fileHeader, archive) -> {
                            if (fileHeader.getFileNameString() == null)
                                return;

                            try {
                                logger.info("new file handler {} begin", fileHeader.getFileNameString());
                                final File file = new File(DIST_FOLDER, fileHeader.getFileNameString());
                                try {
                                    if (file.exists())
                                        file.delete();
                                    file.createNewFile();
                                } catch (IOException e) {
                                    logger.error("can not create file {}", file.getAbsolutePath(), e);
                                }

                                try {
                                    executor.execute(() -> {
                                        try {
                                            FileInputStream fileInputStream = new FileInputStream(file);
                                            int b;
                                            int cnt = 0;
                                            do {
                                                b = fileInputStream.read();
                                                if (b != -1) {
                                                    cnt++;
                                                    if (cnt % 1024 == 0) {
                                                        logger.info("file {} read {} kb", fileHeader.getFileNameString(), cnt / 1024);
                                                    }
                                                } else {
                                                    TimeUnit.SECONDS.sleep(1);
                                                }
                                            } while (cnt < fileHeader.getFullUnpackSize());
                                            logger.info("file {} read complete", fileHeader.getFileNameString());
                                        } catch (Exception e) {
                                            logger.error("extracting file error", e);
                                        }
                                    });
                                } catch (Exception e) {
                                    logger.error("error on run executor", e);
                                    throw e;
                                }

                                logger.info("extract {} begin", fileHeader.getFileNameString());
                                archive.extractFile(fileHeader, new FileOutputStream(file, false));
                            } catch (RarException | FileNotFoundException e) {
                                logger.error("error on download stream extract", e);
                            }
                        });

                logger.info("archive contains {} file headers", newArchive.getFileHeaders().size());
            } catch (Exception e) {
                logger.error("error on new Archive", e);
            }

        } finally {
            executor.shutdown();
        }
    }
}
