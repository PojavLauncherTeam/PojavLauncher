package net.kdt.pojavlaunch.mcfragments;

import android.os.*;
import android.support.annotation.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import net.kdt.pojavlaunch.*;
import android.util.*;
import android.graphics.*;

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
	public void onActivityCreated(Bundle p1)
	{
		super.onActivityCreated(p1);
		/*
		new Thread(new Runnable(){
			@Override
			public void run()
			{
				while (true) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {}
					
					final String popLog = ConsoleFragmentHelper.popLog();
					if (!popLog.isEmpty()) {
						getActivity().runOnUiThread(new Runnable(){
							@Override
							public void run()
							{
								consoleView.append(popLog);
								System.out.println("OKAY? " + popLog);
							}
						});
					}
				}
			}
		}).start();
		*/
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
