package net.kdt.pojavlaunch.progresskeeper;

import static net.kdt.pojavlaunch.Tools.BYTE_TO_MB;

import net.kdt.pojavlaunch.Tools;

public class DownloaderProgressWrapper implements Tools.DownloaderFeedback {

    private final int mProgressString;
    private final String mProgressRecord;
    public String extraString = null;

    /**
     * A simple wrapper to send the downloader progress to ProgressKeeper
     * @param progressString the string that will be used in the progress reporter
     * @param progressRecord the record for ProgressKeeper
     */
    public DownloaderProgressWrapper(int progressString, String progressRecord) {
        this.mProgressString = progressString;
        this.mProgressRecord = progressRecord;
    }

    @Override
    public void updateProgress(int curr, int max) {
        Object[] va;
        if(extraString != null)  {
            va = new Object[3];
            va[0] = extraString;
            va[1] = curr/BYTE_TO_MB;
            va[2] = max/BYTE_TO_MB;
        }
        else {
            va = new Object[2];
            va[0] = curr/BYTE_TO_MB;
            va[1] = max/BYTE_TO_MB;
        }
        // the allocations are fine because thats how java implements variadic arguments in bytecode: an array of whatever
        ProgressKeeper.submitProgress(mProgressRecord, (int) Math.max((float)curr/max*100,0), mProgressString, va);
    }
}
