package net.kdt.pojavlaunch;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.gson.JsonSyntaxException;
import com.kdt.pickafile.FileListView;
import com.kdt.pickafile.FileSelectedListener;

import net.kdt.pojavlaunch.customcontrols.ControlData;
import net.kdt.pojavlaunch.customcontrols.ControlDrawerData;
import net.kdt.pojavlaunch.customcontrols.ControlLayout;
import net.kdt.pojavlaunch.customcontrols.Exitable;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;

import java.io.File;
import java.io.IOException;


public class CustomControlsActivity extends BaseActivity implements Exitable {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerNavigationView;
	private ControlLayout mControlLayout;
	private static String sSelectedName = "new_control";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_custom_controls);

		mControlLayout = findViewById(R.id.customctrl_controllayout);
		mDrawerLayout = findViewById(R.id.customctrl_drawerlayout);
		mDrawerNavigationView = findViewById(R.id.customctrl_navigation_view);
		View mPullDrawerButton = findViewById(R.id.drawer_button);

		mPullDrawerButton.setOnClickListener(v -> mDrawerLayout.openDrawer(mDrawerNavigationView));
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

		mDrawerNavigationView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.menu_customcontrol_customactivity)));
		mDrawerNavigationView.setOnItemClickListener((parent, view, position, id) -> {
			switch(position) {
				case 0: mControlLayout.addControlButton(new ControlData("New")); break;
				case 1: mControlLayout.addDrawer(new ControlDrawerData()); break;
				//case 2: mControlLayout.addJoystickButton(new ControlData()); break;
				case 2: load(mControlLayout); break;
				case 3: save( mControlLayout, this); break;
				case 4: dialogSelectDefaultCtrl(mControlLayout); break;
				case 5: // Saving the currently shown control
					try {
						Uri contentUri = DocumentsContract.buildDocumentUri(getString(R.string.storageProviderAuthorities), doSaveCtrl(sSelectedName, mControlLayout));

						Intent shareIntent = new Intent();
						shareIntent.setAction(Intent.ACTION_SEND);
						shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
						shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
						shareIntent.setType("application/json");
						startActivity(shareIntent);

						Intent sendIntent = Intent.createChooser(shareIntent, sSelectedName);
						startActivity(sendIntent);
					}catch (Exception e) {
						Tools.showError(this, e);
					}
					break;
			}
			mDrawerLayout.closeDrawers();
		});
		mControlLayout.setModifiable(true);

		loadControl(LauncherPreferences.PREF_DEFAULTCTRL_PATH, mControlLayout);
	}

	@Override
	public void onBackPressed() {
		mControlLayout.askToExit(this);
	}

	public static void dialogSelectDefaultCtrl(final ControlLayout layout) {
		AlertDialog.Builder builder = new AlertDialog.Builder(layout.getContext());
		builder.setTitle(R.string.customctrl_selectdefault);
		builder.setPositiveButton(android.R.string.cancel, null);

		final AlertDialog dialog = builder.create();
		FileListView flv = new FileListView(dialog, "json");
		flv.lockPathAt(new File(Tools.CTRLMAP_PATH));
		flv.setFileSelectedListener(new FileSelectedListener(){

			@Override
			public void onFileSelected(File file, String path) {
				setDefaultControlJson(path,layout);
				dialog.dismiss();
			}
		});
		dialog.setView(flv);
		dialog.show();
	}


	public static void save(final ControlLayout layout, final Exitable exitListener) {
		final Context ctx = layout.getContext();
		final EditText edit = new EditText(ctx);
		edit.setSingleLine();
		edit.setText(sSelectedName);

		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(R.string.global_save);
		builder.setView(edit);
		builder.setPositiveButton(android.R.string.ok, null);
		builder.setNegativeButton(android.R.string.cancel, null);
		if(exitListener != null) builder.setNeutralButton(R.string.global_save_and_exit, null);
		final AlertDialog dialog = builder.create();
		dialog.setOnShowListener(dialogInterface -> {
			dialog.getButton(AlertDialog.BUTTON_POSITIVE)
					.setOnClickListener(new OnClickExitListener(dialog, edit, layout, null));
			if(exitListener != null) dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
					.setOnClickListener(new OnClickExitListener(dialog, edit, layout, exitListener));
		});
		dialog.show();

	}

	static class OnClickExitListener implements View.OnClickListener {
		private final AlertDialog mDialog;
		private final EditText mEditText;
		private final ControlLayout mLayout;
		private final Exitable mListener;

		public OnClickExitListener(AlertDialog mDialog, EditText mEditText, ControlLayout mLayout, Exitable mListener) {
			this.mDialog = mDialog;
			this.mEditText = mEditText;
			this.mLayout = mLayout;
			this.mListener = mListener;
		}

		@Override
		public void onClick(View v) {
			Context context = v.getContext();
			if (mEditText.getText().toString().isEmpty()) {
				mEditText.setError(context.getString(R.string.global_error_field_empty));
				return;
			}
			try {
				String jsonPath = doSaveCtrl(mEditText.getText().toString(),mLayout);
				Toast.makeText(context, context.getString(R.string.global_save) + ": " + jsonPath, Toast.LENGTH_SHORT).show();
				mDialog.dismiss();
				if(mListener != null) mListener.exitEditor();
			} catch (Throwable th) {
				Tools.showError(context, th, mListener != null);
			}
		}
	}

	public static void showExitDialog(Context context, Exitable exitListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.customctrl_editor_exit_title);
		builder.setMessage(R.string.customctrl_editor_exit_msg);
		builder.setPositiveButton(R.string.global_yes, (d,w)->exitListener.exitEditor());
		builder.setNegativeButton(R.string.global_no, (d,w)->{});
		builder.show();
	}

	public static void load(final ControlLayout layout) {
		AlertDialog.Builder builder = new AlertDialog.Builder(layout.getContext());
		builder.setTitle(R.string.global_load);
		builder.setPositiveButton(android.R.string.cancel, null);

		final AlertDialog dialog = builder.create();
		FileListView flv = new FileListView(dialog, "json");
		if(Build.VERSION.SDK_INT < 29)flv.listFileAt(new File(Tools.CTRLMAP_PATH));
		else flv.lockPathAt(new File(Tools.CTRLMAP_PATH));
		flv.setFileSelectedListener(new FileSelectedListener(){

			@Override
			public void onFileSelected(File file, String path) {
				loadControl(path,layout);
				dialog.dismiss();
			}
		});
		dialog.setView(flv);
		dialog.show();
	}

	private static void setDefaultControlJson(String path,ControlLayout ctrlLayout) {
		// Load before save to make sure control is not error
		try {
			ctrlLayout.loadLayout(path);
			LauncherPreferences.DEFAULT_PREF.edit().putString("defaultCtrl", path).apply();
			LauncherPreferences.PREF_DEFAULTCTRL_PATH = path;
		} catch (IOException| JsonSyntaxException exception) {
			Tools.showError(ctrlLayout.getContext(), exception);
		}
	}

	private static String doSaveCtrl(String name, final ControlLayout layout) throws Exception {
		String jsonPath = Tools.CTRLMAP_PATH + "/" + name + ".json";
		layout.saveLayout(jsonPath);

		return jsonPath;
	}

	private static void loadControl(String path,ControlLayout layout) {
		try {
			layout.loadLayout(path);
			sSelectedName = path.replace(Tools.CTRLMAP_PATH, ".");
			// Remove `.json`
			sSelectedName = sSelectedName.substring(0, sSelectedName.length() - 5);
		} catch (Exception e) {
			Tools.showError(layout.getContext(), e);
		}
	}

	@Override
	public void exitEditor() {
		super.onBackPressed();
	}
}
