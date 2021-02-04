package net.kdt.pojavlaunch.fragments;

import android.os.*;

import androidx.annotation.Nullable;
import android.view.*;
import android.widget.*;

import net.kdt.pojavlaunch.*;

import android.graphics.*;

import androidx.fragment.app.Fragment;

public class ConsoleFragment extends Fragment
{
	public TextView consoleView;
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.lmaintab_consolelog, container, false);

		consoleView = (TextView) view.findViewById(R.id.lmaintabconsoleLogTextView);
		consoleView.setTypeface(Typeface.MONOSPACE);
		consoleView.setHint(this.getText(R.string.main_nolog));
		
		return view;
    }


	@Override
	public void onResume()
	{
		super.onResume();
		consoleView = (TextView) getView().findViewById(R.id.lmaintabconsoleLogTextView);
	}
	
	public void putLog(String str) {
		if (consoleView == null) {
			consoleView = (TextView) getView().findViewById(R.id.lmaintabconsoleLogTextView);
		}
		
		consoleView.append(str);
	}
}
