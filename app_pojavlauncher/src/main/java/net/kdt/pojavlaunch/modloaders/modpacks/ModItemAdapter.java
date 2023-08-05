package net.kdt.pojavlaunch.modloaders.modpacks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kdt.SimpleArrayAdapter;

import net.kdt.pojavlaunch.PojavApplication;
import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.modloaders.modpacks.api.ModpackApi;
import net.kdt.pojavlaunch.modloaders.modpacks.models.ModDetail;
import net.kdt.pojavlaunch.modloaders.modpacks.models.ModItem;

import java.util.Arrays;

public class ModItemAdapter extends RecyclerView.Adapter<ModItemAdapter.ViewHolder> {

    private ModItem[] mModItems;
    private final ModpackApi mModpackApi;
    private String mTargetMcVersion;

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
        public ViewHolder(View view) {
            super(view);

            view.setOnClickListener(v -> {
                if(!hasExtended()){
                    // Inflate the ViewStub
                    mExtendedLayout = ((ViewStub)v.findViewById(R.id.mod_limited_state_stub)).inflate();
                    mExtendedButton = mExtendedLayout.findViewById(R.id.mod_extended_select_version_button);
                    mExtendedSpinner = mExtendedLayout.findViewById(R.id.mod_extended_version_spinner);

                    mExtendedButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mModpackApi.handleInstallation(
                                    mModDetail,
                                    mModDetail.versionUrls[mExtendedSpinner.getSelectedItemPosition()],
                                    mTargetMcVersion);

                            //TODO do something !
                        }
                    });
                } else {
                    if(isExtended()) mExtendedLayout.setVisibility(View.GONE);
                    else mExtendedLayout.setVisibility(View.VISIBLE);
                }

                if(isExtended() && mModDetail == null) {
                    PojavApplication.sExecutorService.execute(() -> {
                        mModDetail = mModpackApi.getModDetails(mModItem, mTargetMcVersion);
                        System.out.println(mModDetail);
                        Tools.runOnUiThread(() -> setStateDetailed(mModDetail));
                    });
                }
            });

            // Define click listener for the ViewHolder's View
            mTitle = view.findViewById(R.id.mod_title_textview);
            mDescription = view.findViewById(R.id.mod_body_textview);
        }

        /** Display basic info about the moditem */
        public void setStateLimited(ModItem item) {
            mModDetail = null;

            mModItem = item;
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

    public void setModItems(ModItem[] items, String targetMcVersion){
        mModItems = items;
        notifyDataSetChanged();
        mTargetMcVersion = targetMcVersion;
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
