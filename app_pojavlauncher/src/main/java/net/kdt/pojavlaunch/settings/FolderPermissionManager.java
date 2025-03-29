package net.kdt.pojavlaunch.settings;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.UriPermission;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;

import com.fcl.plugin.mobileglues.utils.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FolderPermissionManager {
    private final Context context;

    public FolderPermissionManager(Context context) {
        this.context = context;
    }

    /**
     * @return Obtain the list of Uris that have been granted the read/write permission
     */
    public List<Uri> getGrantedFolderUris() {
        List<Uri> uriList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();

        for (UriPermission permission : contentResolver.getPersistedUriPermissions()) {
            if (permission.isReadPermission() && permission.isWritePermission()) {
                uriList.add(permission.getUri());
            }
        }
        return uriList;
    }

    /**
     * Clear existing "Use this folder" authorization
     */
    public void clearAllPermissions() {
        ContentResolver contentResolver = context.getContentResolver();
        List<UriPermission> persistedUriPermissions = contentResolver.getPersistedUriPermissions();
        for (UriPermission permission : persistedUriPermissions) {
            contentResolver.releasePersistableUriPermission(
                    permission.getUri(),
                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            );
        }
    }

    public File getFileByUri(Uri uri) {
        if (!"com.android.externalstorage.documents".equals(uri.getAuthority())) {
            return null;
        }

        String docId;
        if (DocumentsContract.isTreeUri(uri)) {
            docId = DocumentsContract.getTreeDocumentId(uri);
        } else {
            docId = DocumentsContract.getDocumentId(uri);
        }

        String[] split = docId.split(":");
        if (split.length < 2) return null;

        String type = split[0];
        String relativePath = split[1];

        File baseDir;
        if ("primary".equalsIgnoreCase(type)) {
            baseDir = Environment.getExternalStorageDirectory();
        } else {
            return null;
        }

        return new File(baseDir, relativePath);
    }

    public boolean isUriMatchingFilePath(Uri uri, File file) {
        File expectedFile = getFileByUri(uri);
        if (expectedFile == null) return false;
        return Objects.equals(expectedFile.getAbsolutePath(), file.getAbsolutePath());
    }

    public Uri getMGFolderUri() {
        List<Uri> grantedFolderUris = getGrantedFolderUris();
        File MGFolder = new File(Constants.MG_DIRECTORY);

        for (Uri uri : grantedFolderUris) {
            if (isUriMatchingFilePath(uri, MGFolder)) {
                return uri;
            }
        }

        return null;
    }
}
