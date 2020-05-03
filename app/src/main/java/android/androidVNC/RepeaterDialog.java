/**
 * 
 */
package android.androidVNC;

import android.app.*;
import android.os.*;
import android.text.*;
import android.view.*;
import android.widget.*;
import net.kdt.pojavlaunch.*;

/**
 * @author Michael A. MacDonald
 *
 */
class RepeaterDialog extends Dialog {
	private EditText _repeaterId;
	androidVNC _configurationDialog;

	RepeaterDialog(androidVNC context) {
		super(context);
		setOwnerActivity((Activity)context);
		_configurationDialog = context;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Dialog#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.repeater_dialog_title);
	
		setContentView(R.layout.repeater_dialog);
		_repeaterId=(EditText)findViewById(R.id.textRepeaterInfo);
		((TextView)findViewById(R.id.textRepeaterCaption)).setText(Html.fromHtml(getContext().getString(R.string.repeater_caption)));
		((Button)findViewById(R.id.buttonSaveRepeater)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				_configurationDialog.updateRepeaterInfo(true, _repeaterId.getText().toString());
				dismiss();
			}
		});
		((Button)findViewById(R.id.buttonClearRepeater)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				_configurationDialog.updateRepeaterInfo(false, "");
				dismiss();
			}
		});
	}
}
