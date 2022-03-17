package net.kdt.pojavlaunch.fragments;

import android.os.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.*;
import android.widget.*;

import net.kdt.pojavlaunch.*;

import android.graphics.*;

import androidx.fragment.app.Fragment;

public class ConsoleFragment extends Fragment {
	public TextView mConsoleView;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_console_log, container, false);

		mConsoleView = (TextView) view.findViewById(R.id.lmaintabconsoleLogTextView);
		mConsoleView.setTypeface(Typeface.MONOSPACE);
		mConsoleView.setHint(this.getText(R.string.main_nolog));
		
		return view;
    }


	@Override
	public void onResume() {
		super.onResume();
		mConsoleView = (TextView) getView().findViewById(R.id.lmaintabconsoleLogTextView);
	}
	
	public void putLog(String str) {
		if (mConsoleView == null) {
			mConsoleView = (TextView) getView().findViewById(R.id.lmaintabconsoleLogTextView);
		}
		
		mConsoleView.append(str);
	}
}
