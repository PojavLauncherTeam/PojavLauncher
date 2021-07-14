package net.kdt.pojavlaunch.multirt;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;

import java.io.IOException;
import java.util.List;

public class RTRecyclerViewAdapter extends RecyclerView.Adapter {
    MultiRTConfigDialog dialog;
    public RTRecyclerViewAdapter(MultiRTConfigDialog dialog) {
        this.dialog = dialog;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View recyclableView = LayoutInflater.from(parent.getContext()).inflate(R.layout.multirt_recyclable_view,parent,false);
        return new RTViewHolder(recyclableView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final List<MultiRTUtils.Runtime> runtimes = MultiRTUtils.getRuntimes();
        ((RTViewHolder)holder).bindRuntime(runtimes.get(position));
    }

    @Override
    public int getItemCount() {
        return MultiRTUtils.getRuntimes().size();
    }
    public class RTViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final TextView javaVersionView;
        final TextView fullJavaVersionView;
        final ColorStateList defaultColors;
        final Context ctx;
        MultiRTUtils.Runtime currentRuntime;
        public RTViewHolder(View itemView) {
            super(itemView);
            javaVersionView = itemView.findViewById(R.id.multirt_view_java_version);
            fullJavaVersionView = itemView.findViewById(R.id.multirt_view_java_version_full);
            itemView.findViewById(R.id.multirt_view_removebtn).setOnClickListener(this);
            defaultColors =  fullJavaVersionView.getTextColors();
            ctx = itemView.getContext();
        }
        public void bindRuntime(MultiRTUtils.Runtime rt) {
            currentRuntime = rt;
            if(rt.versionString != null) {
                javaVersionView.setText(ctx.getString(R.string.multirt_java_ver, rt.name, rt.javaVersion));
                fullJavaVersionView.setText(rt.versionString);
                fullJavaVersionView.setTextColor(defaultColors);

            }else{
                javaVersionView.setText(rt.name);
                fullJavaVersionView.setText(R.string.multirt_runtime_corrupt);
                fullJavaVersionView.setTextColor(Color.RED);
            }
        }

        @Override
        public void onClick(View v) {
                if (currentRuntime != null) {
                    final ProgressDialog barrier = new ProgressDialog(ctx);
                    barrier.setMessage(ctx.getString(R.string.global_waiting));
                    barrier.setProgressStyle(barrier.STYLE_SPINNER);
                    barrier.setCancelable(false);
                    barrier.show();
                    Thread t = new Thread(()->{
                        try {
                            MultiRTUtils.removeRuntimeNamed(currentRuntime.name);
                        }catch (IOException e) {
                            Tools.showError(itemView.getContext(),e);
                        }
                        v.post(() ->{
                            barrier.dismiss();
                            RTRecyclerViewAdapter.this.notifyDataSetChanged();
                            dialog.dialog.show();
                        });
                    });
                    t.start();
                }
        }
    }
}
