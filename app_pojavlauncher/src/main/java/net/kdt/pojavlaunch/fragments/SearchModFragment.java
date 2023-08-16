package net.kdt.pojavlaunch.fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.kdt.pojavlaunch.PojavApplication;
import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.modloaders.modpacks.ModItemAdapter;
import net.kdt.pojavlaunch.modloaders.modpacks.SelfReferencingFuture;
import net.kdt.pojavlaunch.modloaders.modpacks.api.CommonApi;
import net.kdt.pojavlaunch.modloaders.modpacks.api.CurseforgeApi;
import net.kdt.pojavlaunch.modloaders.modpacks.api.ModpackApi;
import net.kdt.pojavlaunch.modloaders.modpacks.api.ModrinthApi;
import net.kdt.pojavlaunch.modloaders.modpacks.models.ModItem;
import net.kdt.pojavlaunch.modloaders.modpacks.models.SearchFilters;
import net.kdt.pojavlaunch.profiles.VersionSelectorDialog;

import java.util.Arrays;
import java.util.concurrent.Future;

public class SearchModFragment extends Fragment {

    public static final String TAG = "SearchModFragment";
    private TextView mSelectedVersion;
    private Button mSelectVersionButton;
    private EditText mSearchEditText;
    private RecyclerView mRecyclerview;
    private ModItemAdapter mModItemAdapter;
    private ProgressBar mSearchProgressBar;
    private Future<?> mSearchFuture;
    private TextView mStatusTextView;
    private ColorStateList mDefaultTextColor;

    private ModpackApi modpackApi;

    private SearchFilters mSearchFilters;



    public SearchModFragment(){
        super(R.layout.fragment_mod_search);
        modpackApi = new CommonApi();
        mSearchFilters = new SearchFilters();
        mSearchFilters.isModpack = true;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // You can only access resources after attaching to current context
        mModItemAdapter = new ModItemAdapter(getResources(), modpackApi);

        mSearchEditText = view.findViewById(R.id.search_mod_edittext);
        mSearchProgressBar = view.findViewById(R.id.search_mod_progressbar);
        mSelectedVersion = view.findViewById(R.id.search_mod_selected_mc_version_textview);
        mSelectVersionButton = view.findViewById(R.id.search_mod_mc_version_button);
        mRecyclerview = view.findViewById(R.id.search_mod_list);
        mStatusTextView = view.findViewById(R.id.search_mod_status_text);

        mDefaultTextColor = mStatusTextView.getTextColors();

        mRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerview.setAdapter(mModItemAdapter);

        // Setup the expendable list behavior
        mSelectVersionButton.setOnClickListener(v -> VersionSelectorDialog.open(v.getContext(), true, (id, snapshot)->{
            mSelectedVersion.setText(id);
            mSearchFilters.mcVersion = id;
        }));

        mSearchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if(mSearchFuture != null && !mSearchFuture.isCancelled()) {
                mSearchFuture.cancel(true);
            }
            mSearchProgressBar.setVisibility(View.VISIBLE);
            mSearchFilters.name = mSearchEditText.getText().toString();
            mSearchFuture = new SelfReferencingFuture(new SearchModTask(mSearchFilters))
                    .startOnExecutor(PojavApplication.sExecutorService);
            return true;
        });
    }

    class SearchModTask implements SelfReferencingFuture.FutureInterface {

        private final SearchFilters mTaskFilters;
        SearchModTask(SearchFilters mSearchFilters) {
            this.mTaskFilters = mSearchFilters;
        }

        @Override
        public void run(Future<?> myFuture) {
            ModItem[] items = modpackApi.searchMod(mTaskFilters);
            Log.d(SearchModFragment.class.toString(), Arrays.toString(items));
            Tools.runOnUiThread(() -> {
                ModItem[] localItems = items;
                if(myFuture.isCancelled()) return;
                mSearchProgressBar.setVisibility(View.GONE);
                if(localItems == null) {
                    mStatusTextView.setVisibility(View.VISIBLE);
                    mStatusTextView.setTextColor(Color.RED);
                    mStatusTextView.setText(R.string.search_modpack_error);
                }else if(localItems.length == 0) {
                    mStatusTextView.setVisibility(View.VISIBLE);
                    mStatusTextView.setTextColor(mDefaultTextColor);
                    mStatusTextView.setText(R.string.search_modpack_no_result);
                    localItems = null;
                }else{
                    mStatusTextView.setVisibility(View.GONE);
                }
                mModItemAdapter.setModItems(localItems, mSelectedVersion.getText().toString());
            });
        }
    }
}
