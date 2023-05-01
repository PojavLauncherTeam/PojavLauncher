package net.kdt.pojavlaunch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import net.kdt.pojavlaunch.prefs.LauncherPreferences;

import java.io.File;
import java.io.IOException;


public class CustomControlsActivity extends BaseActivity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerNavigationView;
	private ControlLayout mControlLayout;

	public boolean isModified = false;
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
				case 3: save(false, mControlLayout); break;
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
		mControlLayout.setActivity(this);
		mControlLayout.setModifiable(true);

		loadControl(LauncherPreferences.PREF_DEFAULTCTRL_PATH, mControlLayout);
	}

	@Override
	public void onBackPressed() {
		if (!isModified) {
			setResult(Activity.RESULT_OK, new Intent());
			super.onBackPressed();
			return;
		}

		save(true, mControlLayout);
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


	public static void save(final boolean exit, final ControlLayout layout) {
		final Context ctx = layout.getContext();
		final EditText edit = new EditText(ctx);
		edit.setSingleLine();
		edit.setText(sSelectedName);

		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(R.string.global_save);
		builder.setView(edit);
		builder.setPositiveButton(android.R.string.ok, null);
		builder.setNegativeButton(android.R.string.cancel, null);
		if (exit) {
			builder.setNeutralButton(R.string.mcn_exit_call, (p1, p2) -> {
				layout.setModifiable(false);
				if(ctx instanceof MainActivity) {
					((MainActivity) ctx).leaveCustomControls();
				}else{
					((CustomControlsActivity) ctx).isModified = false;
					((Activity)ctx).onBackPressed();
				}
			});
		}
		final AlertDialog dialog = builder.create();
		dialog.setOnShowListener(dialogInterface -> {

			Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
			button.setOnClickListener(view -> {
				if (edit.getText().toString().isEmpty()) {
					edit.setError(ctx.getResources().getString(R.string.global_error_field_empty));
					return;
				}

				try {
					String jsonPath = doSaveCtrl(edit.getText().toString(),layout);
					Toast.makeText(ctx, ctx.getString(R.string.global_save) + ": " + jsonPath, Toast.LENGTH_SHORT).show();

					dialog.dismiss();
					if (!exit) return;

					if(ctx instanceof MainActivity) {
						((MainActivity) ctx).leaveCustomControls();
					}else{
						((Activity)ctx).onBackPressed();
					}
				} catch (Throwable th) {
					Tools.showError(ctx, th, exit);
				}

			});
		});
		dialog.show();

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
}
