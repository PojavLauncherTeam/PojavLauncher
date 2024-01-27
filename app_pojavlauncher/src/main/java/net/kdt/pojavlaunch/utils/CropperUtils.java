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

import net.kdt.pojavlaunch.PojavApplication;
import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
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
            if (context == null || result == null) return;
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
        View finishProgressBar = dialog.findViewById(R.id.crop_dialog_progressbar);
        assert cropImageView != null;
        assert finishProgressBar != null;
        bindViews(dialog, cropImageView);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v->{
            dialog.dismiss();
            // I chose 70 dp here because it resolves to 192x192 on my device
            // (which has a typical screen density of 395 dpi)
            cropperListener.onCropped(cropImageView.crop((int) Tools.dpToPx(70)));
        });
        PojavApplication.sExecutorService.execute(()->{
            try {
                loadBehaviour(cropImageView, contentResolver, selectedUri);
                Tools.runOnUiThread(()->finishProgressBar.setVisibility(View.GONE));
            }catch (Exception e){ Tools.runOnUiThread(()->{
                cropperListener.onFailed(e);
                dialog.dismiss();
            });}
        });

    }


    private static void loadBehaviour(CropperView cropImageView,
                                      ContentResolver contentResolver,
                                      Uri selectedUri) throws Exception {
        try (InputStream inputStream = contentResolver.openInputStream(selectedUri)) {
            if(inputStream == null) return; // The provider has crashed, there is no point in trying again.
            try {
                BitmapRegionDecoder regionDecoder = BitmapRegionDecoder.newInstance(inputStream, false);
                RegionDecoderCropBehaviour cropBehaviour = new RegionDecoderCropBehaviour(cropImageView);
                cropBehaviour.loadRegionDecoder(regionDecoder);
                finishViewSetup(cropImageView, cropBehaviour);
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
            finishViewSetup(cropImageView,cropBehaviour);
        }
    }


    private static void finishViewSetup(CropperView cropImageView, CropperBehaviour cropBehaviour) {
        Tools.runOnUiThread(()->{
            cropImageView.setCropperBehaviour(cropBehaviour);
            cropImageView.requestLayout();
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
            imageCropperView.resetTransforms()
        );
    }

    @SuppressWarnings("unchecked")
    public static void startCropper(ActivityResultLauncher<?> resultLauncher) {
        ActivityResultLauncher<String[]> realResultLauncher =
                (ActivityResultLauncher<String[]>) resultLauncher;
        realResultLauncher.launch(new String[]{"image/*"});
    }
    public interface CropperListener {
        void onCropped(Bitmap contentBitmap);
        void onFailed(Exception exception);
    }
}
