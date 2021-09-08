package net.kdt.pojavlaunch;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import net.kdt.pojavlaunch.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.StandardCopyOption;

/**
 * An activity dedicated to importing control files.
 */
public class ImportControlActivity extends Activity {

    private Uri uriData;

    private EditText editText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Check if the intent is valid.
        if (getIntent() == null) finishAndRemoveTask();
        getUriData();
        if(uriData == null) finishAndRemoveTask();

        Tools.initContextConstants(getApplicationContext());

        setContentView(R.layout.import_control_layout);
        editText = findViewById(R.id.editText_import_control_file_name);
        //Set the name of the file in the editText.
        String editTextString = uriData.toString().replaceAll("%..", "/");
        editTextString = editTextString.substring(editTextString.lastIndexOf('/') + 1);
        editText.setText(trimFileName(editTextString));

    }

    /**
     * Start the import.
     * @param view the view which called the function
     */
    public void startImport(View view) {
        String fileName = trimFileName(editText.getText().toString());
        //Step 1 check for suffixes.
        if(!isFileNameValid(fileName)){
            Toast.makeText(this, "Invalid name or file already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getApplicationContext(), "Starting importation", Toast.LENGTH_SHORT).show();

        new Thread(() -> {
            importControlFile(fileName);
            runOnUiThread(() -> {
                Toast.makeText(getApplicationContext(), "Importation finished", Toast.LENGTH_SHORT).show();
                finishAndRemoveTask();
            });
        }).start();
    }

    /**
     * Copy a the file from the Intent data with a provided name
     * into the controlmap folder.
     * @param fileName The file name to use.
     * @return whether the file was successfully imported
     */
    private boolean importControlFile(String fileName){
        InputStream is;
        try {
            is = getContentResolver().openInputStream(uriData);

            OutputStream os = new FileOutputStream(Tools.CTRLMAP_PATH + "/" + fileName + ".json");
            byte[] buffer = new byte[1024];
            while(is.read(buffer) != -1)
                os.write(buffer);

            os.close();
            is.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Tell if the clean version of the filename is valid.
     * @param fileName the string to test
     * @return whether the filename is valid
     */
    private static boolean isFileNameValid(String fileName){
        fileName = trimFileName(fileName);

        if(fileName.isEmpty()) return false;
        if (FileUtils.exists(Tools.CTRLMAP_PATH + "/" + fileName + ".json")) return false;

        return true;
    }

    /**
     * Remove or undesirable chars from the string
     * @param fileName The string to trim
     * @return The trimmed string
     */
    private static String trimFileName(String fileName){
        return fileName
                .replace(".json", "")
                .replaceAll("%..", "/")
                .replace("/", "")
                .replace("\\", "")
                .trim();
    }

    /**
     * Tries to get an Uri from the various sources
     */
    private void getUriData(){
        uriData = getIntent().getData();
        if(uriData != null) return;
        try {
            uriData = getIntent().getClipData().getItemAt(0).getUri();
        }catch (Exception ignored){}
    }

}
