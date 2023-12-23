package net.kdt.pojavlaunch.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.TypedValue;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.imgcropper.ImageCropperView;

import java.io.IOException;
import java.io.InputStream;

public class CropperUtils {
    public static ActivityResultLauncher<?> registerCropper(Fragment fragment, final CropperListener cropperListener) {
        return fragment.registerForActivityResult(new ActivityResultContracts.OpenDocument(), (result)->{
            Context context = fragment.getContext();
            if (context == null) return;
            openCropperDialog(context, result, cropperListener);
        });
    }

    private static void openCropperDialog(Context context, Uri selectedUri,
                                          final CropperListener cropperListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.cropper_title);
        builder.setView(R.layout.dialog_cropper);
        // The default cropper options
        //cropImageView.setPadding(padding, padding, padding, 0);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setNegativeButton(android.R.string.cancel, null);
        AlertDialog dialog = builder.show();
        ImageCropperView cropImageView = dialog.findViewById(R.id.crop_dialog_view);
        try (InputStream inputStream = context.getContentResolver().openInputStream(selectedUri)){
            cropImageView.loadBitmap(BitmapFactory.decodeStream(inputStream));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v->{
                dialog.dismiss();
                cropperListener.onCropped(cropImageView.crop(256));
            });
        }catch (IOException e){
            cropperListener.onFailed(e);
            dialog.dismiss();
        }
    }


    private static int getDialogPadding(Context context) {
        TypedValue typedPadding = new TypedValue();
        if(context.getTheme().resolveAttribute(R.attr.dialogPreferredPadding, typedPadding, true)) {
            return TypedValue.complexToDimensionPixelSize(
                    typedPadding.data,
                    context.getResources().getDisplayMetrics()
            );
        }else {
            return 0;
        }
    }

    @SuppressWarnings("unchecked")
    public static void startCropper(ActivityResultLauncher<?> resultLauncher, Context context) {
        ActivityResultLauncher<String[]> realResultLauncher =
                (ActivityResultLauncher<String[]>) resultLauncher;
        realResultLauncher.launch(new String[]{"image/*"});
    }
    public interface CropperListener {
        void onCropped(Bitmap contentBitmap);
        void onFailed(Exception exception);
    }
}
