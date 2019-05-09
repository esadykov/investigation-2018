package ser;

import com.github.junrar.Archive;
import com.github.junrar.ExtractDestination;
import com.github.junrar.Junrar;
import com.github.junrar.LocalFolderExtractor;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DownloadUnrarReadStreaming {
    private static final Logger logger = LoggerFactory.getLogger(DownloadUnrarReadStreaming.class);
    public static void main(String[] args) throws Exception {

        String FILE_NAME = "downloaded.rar";
        String DIST_FOLDER = "unpacked";
        long EXPECTED_FILE_SIZE = 18044431L;
        URL DOWNLOAD_FROM = new URL("http://data.nalog.ru/Public/Downloads/20190506/fias_delta_xml.rar");

/*        ReadableByteChannel readableByteChannel = Channels.newChannel(DOWNLOAD_FROM.openStream());

        FileOutputStream fileOutputStream = new FileOutputStream(FILE_NAME);

        logger.info("start download");
        fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        logger.info("finish download");

        Path filePath = Paths.get(FILE_NAME);
        FileChannel imageFileChannel = FileChannel.open(filePath);

        long actualFileSize = imageFileChannel.size();
        if (actualFileSize != EXPECTED_FILE_SIZE)
            throw new Exception("Incorrect file size: " + actualFileSize);
        else
            logger.info("file size is correct: {}", actualFileSize);
*/

        File destinationFolder = new File(DIST_FOLDER);
        if (!destinationFolder.exists()) {
            logger.info("create unpacked folder: {}", destinationFolder.mkdir());
        }
        Junrar.extract(DOWNLOAD_FROM.openStream(), destinationFolder);

        //Junrar.validateDestinationPath(destinationFolder);

        final Archive arch;

        try {
            arch = new Archive(DOWNLOAD_FROM.openStream());
        } catch (final Exception e) {
            logger.error("Error on create Archive", e);
            throw e;
        }

        LocalFolderExtractor lfe = new LocalFolderExtractor(destinationFolder);


        if (arch.isEncrypted()) {
            logger.warn("archive is encrypted cannot extract");
            arch.close();
            return;
        }

        final List<File> extractedFiles = new ArrayList<File>();
        try{
            for(final FileHeader fh : arch ) {
                try {
                    final File file = tryToExtract(lfe, arch, fh);
                    if (file != null) {
                        extractedFiles.add(file);
                    }
                } catch (final IOException e) {
                    logger.error("error extracting the file", e);
                    throw e;
                } catch (final RarException e) {
                    logger.error("error extraction the file", e);
                    throw e;
                }
            }
        }finally {
            arch.close();
        }
        logger.info("extracted files: {}", extractedFiles);

        logger.info("delete unpacked: {}", deleteFolder(destinationFolder));
    }


    private static File tryToExtract(
            final ExtractDestination destination,
            final Archive arch,
            final FileHeader fileHeader
    ) throws IOException, RarException {
        final String fileNameString = fileHeader.getFileNameString();
        if (fileHeader.isEncrypted()) {
            logger.warn("file is encrypted cannot extract: "+ fileNameString);
            return null;
        }
        logger.info("extracting: " + fileNameString);
        if (fileHeader.isDirectory()) {
            return destination.createDirectory(fileHeader);
        } else {
            return destination.extract(arch, fileHeader);
        }
    }

    private static boolean deleteFolder(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteFolder(file);
            }
        }
        return directoryToBeDeleted.delete();
    }
}
