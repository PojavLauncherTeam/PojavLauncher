package net.kdt.pojavlaunch.multirt;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import net.kdt.pojavlaunch.Architecture;
import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.extra.ExtraCore;
import net.kdt.pojavlaunch.extra.ExtraListener;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;

import java.io.IOException;
import java.util.List;

public class RTRecyclerViewAdapter extends RecyclerView.Adapter<RTRecyclerViewAdapter.RTViewHolder> {

    MultiRTConfigDialog mConfigDialog;
    public RTRecyclerViewAdapter(MultiRTConfigDialog dialog) {
        this.mConfigDialog = dialog;
    }

    @NonNull
    @Override
    public RTViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View recyclableView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multirt_runtime,parent,false);
        return new RTViewHolder(recyclableView);
    }

    @Override
    public void onBindViewHolder(@NonNull RTViewHolder holder, int position) {
        final List<Runtime> runtimes = MultiRTUtils.getRuntimes();
        holder.bindRuntime(runtimes.get(position),position);
    }

    public boolean isDefaultRuntime(MultiRTUtils.Runtime rt) {
        return LauncherPreferences.PREF_DEFAULT_RUNTIME.equals(rt.name);
    }

    public void setDefault(MultiRTUtils.Runtime rt){
        LauncherPreferences.PREF_DEFAULT_RUNTIME = rt.name;
        LauncherPreferences.DEFAULT_PREF.edit().putString("defaultRuntime",LauncherPreferences.PREF_DEFAULT_RUNTIME).apply();
        RTRecyclerViewAdapter.this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return MultiRTUtils.getRuntimes().size();
    }
	
    public class RTViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final TextView javaVersionView;
        final TextView fullJavaVersionView;
        final ColorStateList defaultColors;
        final Button setDefaultButton;
        final Context ctx;
        MultiRTUtils.Runtime currentRuntime;
        int currentPosition;

        public RTViewHolder(View itemView) {
            super(itemView);
            mJavaVersionTextView = itemView.findViewById(R.id.multirt_view_java_version);
            mFullJavaVersionTextView = itemView.findViewById(R.id.multirt_view_java_version_full);
            mSetDefaultButton = itemView.findViewById(R.id.multirt_view_setdefaultbtn);
            mSetDefaultButton.setOnClickListener(this);
            mDefaultColors =  mFullJavaVersionTextView.getTextColors();
            mContext = itemView.getContext();
            itemView.findViewById(R.id.multirt_view_removebtn).setOnClickListener(this);

            setDefaultButton = itemView.findViewById(R.id.multirt_view_setdefaultbtn);
            setDefaultButton.setOnClickListener(this);
            defaultColors =  fullJavaVersionView.getTextColors();
            ctx = itemView.getContext();
        }

        public void bindRuntime(MultiRTUtils.Runtime rt, int pos) {
            currentRuntime = rt;
            currentPosition = pos;
            if(rt.versionString != null && Tools.DEVICE_ARCHITECTURE == Architecture.archAsInt(rt.arch)) {
                javaVersionView.setText(ctx.getString(R.string.multirt_java_ver, rt.name, rt.javaVersion));
                fullJavaVersionView.setText(rt.versionString);
                fullJavaVersionView.setTextColor(defaultColors);
                setDefaultButton.setVisibility(View.VISIBLE);
                boolean default_ = isDefaultRuntime(rt);
                setDefaultButton.setEnabled(!default_);
                setDefaultButton.setText(default_?R.string.multirt_config_setdefault_already:R.string.multirt_config_setdefault);
            }else{
                if(rt.versionString == null){
                    fullJavaVersionView.setText(R.string.multirt_runtime_corrupt);
                }else{
                    fullJavaVersionView.setText(ctx.getString(R.string.multirt_runtime_incompatiblearch, rt.arch));
                }
                javaVersionView.setText(rt.name);

                Integer runtime_status = (Integer) ExtraCore.getValue("runtime_status");
                if (runtime_status == null || runtime_status == 0){
                    fullJavaVersionView.setText(R.string.multirt_runtime_corrupt);
                }else{
                    fullJavaVersionView.setText("runtime is being installed");
                }
				
                fullJavaVersionView.setTextColor(Color.RED);
                setDefaultButton.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.multirt_view_removebtn) {
                if (mCurrentRuntime == null) return;

                if(MultiRTUtils.getRuntimes().size() < 2 && mSetDefaultButton.isShown()) {
                    AlertDialog.Builder bldr = new AlertDialog.Builder(mContext);
                    bldr.setTitle(R.string.global_error);
                    bldr.setMessage(R.string.multirt_config_removeerror_last);
                    bldr.setPositiveButton(android.R.string.ok,(adapter, which)->adapter.dismiss());
                    bldr.show();
                    return;
                }

                final ProgressDialog barrier = new ProgressDialog(mContext);
                barrier.setMessage(mContext.getString(R.string.global_waiting));
                barrier.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                barrier.setCancelable(false);
                barrier.show();
                Thread t = new Thread(() -> {
                    try {
                        MultiRTUtils.removeRuntimeNamed(mCurrentRuntime.name);
                    } catch (IOException e) {
                        Tools.showError(itemView.getContext(), e);
                    }
                    view.post(() -> {
                        if(isDefaultRuntime(mCurrentRuntime)) setDefault(MultiRTUtils.getRuntimes().get(0));
                        barrier.dismiss();
                        RTRecyclerViewAdapter.this.notifyDataSetChanged();
                        mConfigDialog.mDialog.show();
                    });
                });
                t.start();

            }else if(view.getId() == R.id.multirt_view_setdefaultbtn) {
                if(mCurrentRuntime != null) {
                    setDefault(mCurrentRuntime);
                    RTRecyclerViewAdapter.this.notifyDataSetChanged();
                }
            }
        }

        public void bindRuntime(Runtime runtime, int pos) {
            mCurrentRuntime = runtime;
            mCurrentPosition = pos;
            if(runtime.versionString != null && Tools.DEVICE_ARCHITECTURE == Architecture.archAsInt(runtime.arch)) {
                mJavaVersionTextView.setText(mContext.getString(R.string.multirt_java_ver, runtime.name, runtime.javaVersion));
                mFullJavaVersionTextView.setText(runtime.versionString);
                mFullJavaVersionTextView.setTextColor(mDefaultColors);
                mSetDefaultButton.setVisibility(View.VISIBLE);
                boolean defaultRuntime = isDefaultRuntime(runtime);
                mSetDefaultButton.setEnabled(!defaultRuntime);
                mSetDefaultButton.setText(defaultRuntime ? R.string.multirt_config_setdefault_already:R.string.multirt_config_setdefault);
                return;
            }

            // Problematic runtime moment
            if(runtime.versionString == null){
                mFullJavaVersionTextView.setText(R.string.multirt_runtime_corrupt);
            }else{
                mFullJavaVersionTextView.setText(mContext.getString(R.string.multirt_runtime_incompatiblearch, runtime.arch));
            }
            mJavaVersionTextView.setText(runtime.name);
            mFullJavaVersionTextView.setTextColor(Color.RED);
            mSetDefaultButton.setVisibility(View.GONE);
        }
    }
}
