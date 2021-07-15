package net.kdt.pojavlaunch.multirt;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.kdt.pojavlaunch.R;

import java.util.List;

public class RTSpinnerAdapter implements SpinnerAdapter {
    final Context ctx;
    List<MultiRTUtils.Runtime> runtimes;
    public RTSpinnerAdapter(@NonNull Context context, List<MultiRTUtils.Runtime> runtimes) {
        this.runtimes = runtimes;
        MultiRTUtils.Runtime runtime = new MultiRTUtils.Runtime("<Default>");
        runtime.versionString = "";
        this.runtimes.add(runtime);
        ctx = context;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return runtimes.size();
    }

    @Override
    public Object getItem(int position) {
        return runtimes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return runtimes.get(position).name.hashCode();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView!=null?
                convertView:
                LayoutInflater.from(ctx).inflate(R.layout.multirt_recyclable_view,parent,false);

        MultiRTUtils.Runtime rt = runtimes.get(position);

        final TextView javaVersionView = v.findViewById(R.id.multirt_view_java_version);
        final TextView fullJavaVersionView = v.findViewById(R.id.multirt_view_java_version_full);
        v.findViewById(R.id.multirt_view_removebtn).setVisibility(View.GONE);
        v.findViewById(R.id.multirt_view_setdefaultbtn).setVisibility(View.GONE);

        if(rt.versionString != null) {
            javaVersionView.setText(ctx.getString(R.string.multirt_java_ver, rt.name, rt.javaVersion));
            fullJavaVersionView.setText(rt.versionString);
        }else{
            javaVersionView.setText(rt.name);
            fullJavaVersionView.setText(R.string.multirt_runtime_corrupt);
        }
        return v;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return runtimes.isEmpty();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position,convertView,parent);
    }

}
