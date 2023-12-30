package net.kdt.pojavlaunch.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ToggleButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.imgcropper.BitmapCropBehaviour;
import net.kdt.pojavlaunch.imgcropper.CropperBehaviour;
import net.kdt.pojavlaunch.imgcropper.CropperView;
import net.kdt.pojavlaunch.imgcropper.RegionDecoderCropBehaviour;

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
        ContentResolver contentResolver = context.getContentResolver();
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.cropper_title);
        builder.setView(R.layout.dialog_cropper);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setNegativeButton(android.R.string.cancel, null);
        AlertDialog dialog = builder.show();
        CropperView cropImageView = dialog.findViewById(R.id.crop_dialog_view);
        assert cropImageView != null;
        try {
            try (InputStream inputStream = contentResolver.openInputStream(selectedUri)) {
                if(inputStream == null) return; // The provider has crashed, there is no point in trying again.
                try {
                    BitmapRegionDecoder regionDecoder = BitmapRegionDecoder.newInstance(inputStream, false);
                    RegionDecoderCropBehaviour cropBehaviour = new RegionDecoderCropBehaviour(cropImageView);
                    cropBehaviour.loadRegionDecoder(regionDecoder);
                    finishViewSetup(dialog, cropImageView, cropBehaviour, cropperListener);
                    return;
                }catch (IOException e) {
                    // Catch IOE here to detect the case when BitmapRegionDecoder does not support this image format.
                    // If it does not, we will just have to load the bitmap in full resolution using BitmapFactory.
                    Log.w("CropperUtils", "Failed to load image into BitmapRegionDecoder", e);
                }
            }
            // We can safely re-open the stream here as ACTION_OPEN_DOCUMENT grants us long-term access
            // to the file that we have picked.
            try (InputStream inputStream = contentResolver.openInputStream(selectedUri)) {
                if(inputStream == null) return;
                Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);
                BitmapCropBehaviour cropBehaviour = new BitmapCropBehaviour(cropImageView);
                cropBehaviour.loadBitmap(originalBitmap);
                finishViewSetup(dialog, cropImageView, cropBehaviour, cropperListener);
            }
        }catch (Exception e){
            cropperListener.onFailed(e);
            dialog.dismiss();
        }
    }


    private static void finishViewSetup(AlertDialog dialog,
                                 CropperView cropImageView,
                                 CropperBehaviour cropBehaviour,
                                 CropperListener cropperListener) {
        cropImageView.cropperBehaviour = cropBehaviour;
        cropImageView.requestLayout();
        bindViews(dialog, cropImageView);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v->{
            dialog.dismiss();
            cropperListener.onCropped(cropImageView.crop(256));
        });
    }

    private static void bindViews(AlertDialog alertDialog, CropperView imageCropperView) {
        ToggleButton horizontalLock = alertDialog.findViewById(R.id.crop_dialog_hlock);
        ToggleButton verticalLock = alertDialog.findViewById(R.id.crop_dialog_vlock);
        View reset = alertDialog.findViewById(R.id.crop_dialog_reset);
        assert horizontalLock != null;
        assert verticalLock != null;
        assert reset != null;
        horizontalLock.setOnClickListener(v->
                imageCropperView.horizontalLock = horizontalLock.isChecked()
        );
        verticalLock.setOnClickListener(v->
                imageCropperView.verticalLock = verticalLock.isChecked()
        );
        reset.setOnClickListener(v->
            imageCropperView.cropperBehaviour.resetTransforms()
        );
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
