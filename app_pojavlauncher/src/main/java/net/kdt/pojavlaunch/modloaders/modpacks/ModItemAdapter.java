package net.kdt.pojavlaunch.modloaders.modpacks;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kdt.SimpleArrayAdapter;

import net.kdt.pojavlaunch.PojavApplication;
import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.modloaders.modpacks.api.ModpackApi;
import net.kdt.pojavlaunch.modloaders.modpacks.imagecache.ImageReceiver;
import net.kdt.pojavlaunch.modloaders.modpacks.imagecache.ModIconCache;
import net.kdt.pojavlaunch.modloaders.modpacks.models.ModDetail;
import net.kdt.pojavlaunch.modloaders.modpacks.models.ModItem;

import java.util.Arrays;

public class ModItemAdapter extends RecyclerView.Adapter<ModItemAdapter.ViewHolder> {
    private static final ModItem[] MOD_ITEMS_EMPTY = new ModItem[0];
    private final ModIconCache mIconCache = new ModIconCache();
    private ModItem[] mModItems;
    private final ModpackApi mModpackApi;

    /**
     * Basic viewholder with expension capabilities
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        private ModDetail mModDetail = null;
        private ModItem mModItem = null;
        private final TextView mTitle, mDescription;
        private View mExtendedLayout;
        private Spinner mExtendedSpinner;
        private Button mExtendedButton;
        private final ImageView mIconView;
        private Bitmap mThumbnailBitmap;
        private ImageReceiver mImageReceiver;
        public ViewHolder(View view) {
            super(view);

            view.setOnClickListener(v -> {
                if(!hasExtended()){
                    // Inflate the ViewStub
                    mExtendedLayout = ((ViewStub)v.findViewById(R.id.mod_limited_state_stub)).inflate();
                    mExtendedButton = mExtendedLayout.findViewById(R.id.mod_extended_select_version_button);
                    mExtendedSpinner = mExtendedLayout.findViewById(R.id.mod_extended_version_spinner);

                    mExtendedButton.setOnClickListener(v1 -> {
                        mModpackApi.handleInstallation(
                                mModDetail,
                                mExtendedSpinner.getSelectedItemPosition());

                        //TODO do something !
                    });
                } else {
                    if(isExtended()) mExtendedLayout.setVisibility(View.GONE);
                    else mExtendedLayout.setVisibility(View.VISIBLE);
                }

                if(isExtended() && mModDetail == null) {
                    PojavApplication.sExecutorService.execute(() -> {
                        mModDetail = mModpackApi.getModDetails(mModItem);
                        System.out.println(mModDetail);
                        Tools.runOnUiThread(() -> setStateDetailed(mModDetail));
                    });
                }
            });

            // Define click listener for the ViewHolder's View
            mTitle = view.findViewById(R.id.mod_title_textview);
            mDescription = view.findViewById(R.id.mod_body_textview);
            mIconView = view.findViewById(R.id.mod_thumbnail_imageview);
        }

        /** Display basic info about the moditem */
        public void setStateLimited(ModItem item) {
            mModDetail = null;
            if(mThumbnailBitmap != null) {
                mIconView.setImageBitmap(null);
                mThumbnailBitmap.recycle();
            }
            if(mImageReceiver != null) {
                mIconCache.cancelImage(mImageReceiver);
            }

            mModItem = item;
            // here the previous reference to the image receiver will disappear
            mImageReceiver = bm->{
                mImageReceiver = null;
                mThumbnailBitmap = bm;
                mIconView.setImageBitmap(bm);
            };
            mIconCache.getImage(mImageReceiver, mModItem.getIconCacheTag(), mModItem.imageUrl);
            mTitle.setText(item.title);
            mDescription.setText(item.description);

            if(hasExtended()){
                mExtendedLayout.setVisibility(View.GONE);
            }
        }

        /** Display extended info/interaction about a modpack */
        private void setStateDetailed(ModDetail detailedItem) {
            mExtendedLayout.setVisibility(View.VISIBLE);
            mExtendedSpinner.setAdapter(new SimpleArrayAdapter<>(Arrays.asList(detailedItem.versionNames)));
        }

        private boolean hasExtended(){
            return mExtendedLayout != null;
        }

        private boolean isExtended(){
            return hasExtended() && mExtendedLayout.getVisibility() == View.VISIBLE;
        }
    }


    public ModItemAdapter(ModpackApi api) {
        mModpackApi = api;
        mModItems = new ModItem[]{};
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setModItems(ModItem[] items, String targetMcVersion){
        // TODO: Use targetMcVersion to affect default selected modpack version
        if(items != null) mModItems = items;
        else mModItems = MOD_ITEMS_EMPTY;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.view_mod, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.setStateLimited(mModItems[position]);
    }

    @Override
    public int getItemCount() {
        return mModItems.length;
    }
}
