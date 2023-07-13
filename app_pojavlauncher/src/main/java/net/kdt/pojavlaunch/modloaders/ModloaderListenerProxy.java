package net.kdt.pojavlaunch.modloaders;

import java.io.File;

public class ModloaderListenerProxy implements ModloaderDownloadListener {
    public static final int PROXY_RESULT_NONE = -1;
    public static final int PROXY_RESULT_FINISHED = 0;
    public static final int PROXY_RESULT_NOT_AVAILABLE = 1;
    public static final int PROXY_RESULT_ERROR = 2;
    private ModloaderDownloadListener mDestinationListener;
    private Object mProxyResultObject;
    private int mProxyResult = PROXY_RESULT_NONE;

    @Override
    public synchronized void onDownloadFinished(File downloadedFile) {
        if(mDestinationListener != null) {
            mDestinationListener.onDownloadFinished(downloadedFile);
        }else{
            mProxyResult = PROXY_RESULT_FINISHED;
            mProxyResultObject = downloadedFile;
        }
    }

    @Override
    public synchronized void onDataNotAvailable() {
        if(mDestinationListener != null) {
            mDestinationListener.onDataNotAvailable();
        }else{
            mProxyResult = PROXY_RESULT_NOT_AVAILABLE;
            mProxyResultObject = null;
        }
    }

    @Override
    public synchronized void onDownloadError(Exception e) {
        if(mDestinationListener != null) {
            mDestinationListener.onDownloadError(e);
        }else {
            mProxyResult = PROXY_RESULT_ERROR;
            mProxyResultObject = e;
        }
    }

    public synchronized void attachListener(ModloaderDownloadListener listener) {
        switch(mProxyResult) {
            case PROXY_RESULT_FINISHED:
                listener.onDownloadFinished((File) mProxyResultObject);
                break;
            case PROXY_RESULT_NOT_AVAILABLE:
                listener.onDataNotAvailable();
                break;
            case PROXY_RESULT_ERROR:
                listener.onDownloadError((Exception) mProxyResultObject);
                break;
        }
        mDestinationListener = listener;
    }
    public synchronized void detachListener() {
        mDestinationListener = null;
    }
}
