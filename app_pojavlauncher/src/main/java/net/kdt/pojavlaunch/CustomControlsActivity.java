package net.kdt.pojavlaunch;

import android.content.*;
import android.os.*;

import androidx.appcompat.app.*;
import androidx.preference.*;
import android.view.*;
import android.widget.*;

import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.kdt.pickafile.*;
import java.io.*;

import net.kdt.pojavlaunch.prefs.*;
import net.kdt.pojavlaunch.customcontrols.*;



public class CustomControlsActivity extends BaseActivity
{
	private DrawerLayout drawerLayout;
    private NavigationView navDrawer;
	private ControlLayout ctrlLayout;
    
	private SharedPreferences mPref;

	public boolean isModified = false;
	private String selectedName = "new_control";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.control_mapping);

		mPref = PreferenceManager.getDefaultSharedPreferences(this);

		ctrlLayout = (ControlLayout) findViewById(R.id.customctrl_controllayout);

		// Menu
		drawerLayout = (DrawerLayout) findViewById(R.id.customctrl_drawerlayout);

		navDrawer = (NavigationView) findViewById(R.id.customctrl_navigation_view);
		navDrawer.setNavigationItemSelectedListener(
			new NavigationView.OnNavigationItemSelectedListener() {
				@Override
				public boolean onNavigationItemSelected(MenuItem menuItem) {
					switch (menuItem.getItemId()) {
						case R.id.menu_ctrl_load:
							actionLoad();
							break;
						case R.id.menu_ctrl_add:
							ctrlLayout.addControlButton(new ControlData("New", LWJGLGLFWKeycode.GLFW_KEY_UNKNOWN, 100, 100));
							break;
						case R.id.menu_ctrl_selectdefault:
							dialogSelectDefaultCtrl();
							break;
						case R.id.menu_ctrl_save:
							save(false);
							break;
					}
					//Toast.makeText(MainActivity.this, menuItem.getTitle() + ":" + menuItem.getItemId(), Toast.LENGTH_SHORT).show();

					drawerLayout.closeDrawers();
					return true;
				}
			});

		ctrlLayout.setActivity(this);
		ctrlLayout.setModifiable(true);

		loadControl(LauncherPreferences.PREF_DEFAULTCTRL_PATH);
	}

	@Override
	public void onBackPressed() {
		if (!isModified) {
			super.onBackPressed();
			return;
		}

		save(true);
	}

	private void setDefaultControlJson(String path) {
		try {
			// Load before save to make sure control is not error
			ctrlLayout.loadLayout(Tools.GLOBAL_GSON.fromJson(Tools.read(path), CustomControls.class));
			LauncherPreferences.DEFAULT_PREF.edit().putString("defaultCtrl", path).commit();
			LauncherPreferences.PREF_DEFAULTCTRL_PATH = path;
		} catch (Throwable th) {
			Tools.showError(this, th);
		}
	}
    
	private void dialogSelectDefaultCtrl() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.customctrl_selectdefault);
		builder.setPositiveButton(android.R.string.cancel, null);

		final AlertDialog dialog = builder.create();
		FileListView flv = new FileListView(dialog, "json");
		flv.lockPathAt(Tools.CTRLMAP_PATH);
		flv.setFileSelectedListener(new FileSelectedListener(){

				@Override
				public void onFileSelected(File file, String path) {
					setDefaultControlJson(path);
					dialog.dismiss();
				}
			});
		dialog.setView(flv);
		dialog.show();
	}

	private String doSaveCtrl(String name) throws Exception {
		String jsonPath = Tools.CTRLMAP_PATH + "/" + name + ".json";
		ctrlLayout.saveLayout(jsonPath);

		return jsonPath;
	}

	private void save(final boolean exit) {
		final EditText edit = new EditText(this);
		edit.setSingleLine();
		edit.setText(selectedName);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.global_save);
		builder.setView(edit);
		builder.setPositiveButton(android.R.string.ok, null);
		builder.setNegativeButton(android.R.string.cancel, null);
		if (exit) {
			builder.setNeutralButton(R.string.mcn_exit_call, new AlertDialog.OnClickListener(){
					@Override
					public void onClick(DialogInterface p1, int p2) {
						CustomControlsActivity.super.onBackPressed();
					}
				});
		}
		final AlertDialog dialog = builder.create();
		dialog.setOnShowListener(new DialogInterface.OnShowListener() {

				@Override
				public void onShow(DialogInterface dialogInterface) {

					Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
					button.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View view) {
								if (edit.getText().toString().isEmpty()) {
									edit.setError(getResources().getString(R.string.global_error_field_empty));
								} else {
									try {
										String jsonPath = doSaveCtrl(edit.getText().toString());

										Toast.makeText(CustomControlsActivity.this, getString(R.string.global_save) + ": " + jsonPath, Toast.LENGTH_SHORT).show();

										dialog.dismiss();
										if (exit) {
											CustomControlsActivity.super.onBackPressed();
										}
									} catch (Throwable th) {
										Tools.showError(CustomControlsActivity.this, th, exit);
									}
								}
							}
						});
				}
			});
		dialog.show();

	}

	private void actionLoad() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.global_load);
		builder.setPositiveButton(android.R.string.cancel, null);

		final AlertDialog dialog = builder.create();
		FileListView flv = new FileListView(dialog, "json");
		flv.listFileAt(Tools.CTRLMAP_PATH);
		flv.setFileSelectedListener(new FileSelectedListener(){

				@Override
				public void onFileSelected(File file, String path) {
					loadControl(path);
					dialog.dismiss();
				}
			});
		dialog.setView(flv);
		dialog.show();
	}

	private void loadControl(String path) {
		try {
			ctrlLayout.loadLayout(path);

			selectedName = new File(path).getName();
			// Remove `.json`
			selectedName = selectedName.substring(0, selectedName.length() - 5);
		} catch (Exception e) {
			Tools.showError(CustomControlsActivity.this, e);
		}
	}
}
