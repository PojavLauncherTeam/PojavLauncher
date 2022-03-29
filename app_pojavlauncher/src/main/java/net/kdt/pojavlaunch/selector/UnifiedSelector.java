package net.kdt.pojavlaunch.selector;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.kdt.pickafile.FileListView;
import com.kdt.pickafile.FileSelectedListener;

import java.io.File;
import java.io.FileInputStream;

/**
 * A unified file selector, supporting both new SAF selection and old dialog selection.
 */
public class UnifiedSelector {
    ActivityResultLauncher<String> mSelectorLauncher;
    UnifiedSelectorCallback mSelectorCallback;
    AlertDialog mSelectorDialog;
    String mFileNameSuffix;
    /**
     * Creates and configures this <code>UnifiedSelector</code>, using the desired <code>AppCompatActivity</code>
     * @param activity the desired AppCompatActivity
     */
    public UnifiedSelector(AppCompatActivity activity) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mSelectorLauncher = activity.registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
                if(mSelectorCallback != null && result != null) {
                    try {
                        ContentResolver contentResolver = activity.getContentResolver();
                        String fileName = getNameFromURI(contentResolver,result);
                        Log.i("UnifiedSelector","fName[SAF]: "+fileName);
                        if(fileName.endsWith(mFileNameSuffix)) {
                            mSelectorCallback.onSelected(contentResolver.openInputStream(result), fileName);
                        }
                    } catch (Throwable th) {
                        mSelectorCallback.onError(th);
                    }
                }else Log.e("UnifiedSelector","Smells like there's something NULLed here");
            });

        }else{
            AlertDialog.Builder bldr = new AlertDialog.Builder(activity);
            bldr.setCancelable(false);
            mSelectorDialog = bldr.create();
        }
    }

    /**
     * Sets the selection callback that notifies the user about a successful selection ar about an error
     * @param callback the selection callback
     */
    public void setSelectionCallback(UnifiedSelectorCallback callback) {
        mSelectorCallback = callback;
    }

    /**
     * Opens the selector with the desired file suffix. Note that on API level 19 and higher,
     * the selector will attempt to automatically guess the MIME type from the suffix, so please don't add dots
     * into the suffix.
     * @param fileSuffix the desired file suffix (without the dot)
     */
    public void openSelector(String fileSuffix) {
        mFileNameSuffix = fileSuffix;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mFileNameSuffix);
            if (mimeType == null) mimeType = "*/*";
            mSelectorLauncher.launch(mimeType);
        }else{
            FileListView fileListView = new FileListView(mSelectorDialog, mFileNameSuffix);
            fileListView.setFileSelectedListener(new FileSelectedListener() {
                @Override
                public void onFileSelected(File file, String path) {
                    try {
                        Log.i("UnifiedSelector","fName[REG_NAT]: "+file.getName());
                        FileInputStream fis = new FileInputStream(file);
                        mSelectorCallback.onSelected(fis, file.getName());
                    }catch (Throwable th) {
                        mSelectorCallback.onError(th);
                    }
                }
            });
            fileListView.listFileAt(Environment.getExternalStorageDirectory().getAbsolutePath());
            mSelectorDialog.setView(fileListView);
            mSelectorDialog.show();
        }
    }
    public static String getNameFromURI(ContentResolver resolver, Uri uri) {
        Cursor c = resolver.query(uri, null, null, null, null);
        c.moveToFirst();
        int columnIndex = c.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        String name;
        if(columnIndex != -1) {
            name = c.getString(columnIndex);
        }else{
            name = uri.getLastPathSegment();
        }
        c.close();
        return name;
    }
}
