package ser;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class SleepingInputStream extends InputStream {
    private InputStream is;
    private final long sleepMilliseconds;
    private final long sleepEachBytes;
    private long counter = 0;

    public SleepingInputStream(InputStream is, long sleepMilliseconds, long sleepEachBytes) {
        this.is = is;
        this.sleepMilliseconds = sleepMilliseconds;
        this.sleepEachBytes = sleepEachBytes;
    }

    @Override
    public int read() throws IOException {
        counter++;
        if (counter % sleepEachBytes == 0) {
            try {
                TimeUnit.MILLISECONDS.sleep(sleepMilliseconds);
            } catch (InterruptedException e) {
                //
            }
        }
        return is.read();
    }
}
