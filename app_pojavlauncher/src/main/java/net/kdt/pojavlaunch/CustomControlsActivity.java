package net.kdt.pojavlaunch;

import android.app.Activity;
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
	public boolean isFromMainActivity = false;
	private static String selectedName = "new_control";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("fromMainActivity", false)) {
            // TODO translucent!
            // setTheme(androidx.appcompat.R.style.Theme_AppCompat_Translucent);
        }
        
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
							load(ctrlLayout);
							break;
						case R.id.menu_ctrl_add:
							ctrlLayout.addControlButton(new ControlData("New"));
							break;
						case R.id.menu_ctrl_add_drawer:
							ctrlLayout.addDrawer(new ControlDrawerData());
							break;
						case R.id.menu_ctrl_selectdefault:
							dialogSelectDefaultCtrl(ctrlLayout);
							break;
						case R.id.menu_ctrl_save:
							save(false,ctrlLayout);
							break;
					}
					//Toast.makeText(MainActivity.this, menuItem.getTitle() + ":" + menuItem.getItemId(), Toast.LENGTH_SHORT).show();

					drawerLayout.closeDrawers();
					return true;
				}
			});

		ctrlLayout.setActivity(this);
		ctrlLayout.setModifiable(true);

		loadControl(LauncherPreferences.PREF_DEFAULTCTRL_PATH,ctrlLayout);
	}

	@Override
	public void onBackPressed() {
		if (!isModified) {
		    setResult(Activity.RESULT_OK, new Intent());
			super.onBackPressed();
			return;
		}

		save(true,ctrlLayout);
	}

	private static void setDefaultControlJson(String path,ControlLayout ctrlLayout) {
		try {
			// Load before save to make sure control is not error
			ctrlLayout.loadLayout(Tools.GLOBAL_GSON.fromJson(Tools.read(path), CustomControls.class));
			LauncherPreferences.DEFAULT_PREF.edit().putString("defaultCtrl", path).commit();
			LauncherPreferences.PREF_DEFAULTCTRL_PATH = path;
		} catch (Throwable th) {
			Tools.showError(ctrlLayout.getContext(), th);
		}
	}
    
	public static void dialogSelectDefaultCtrl(final ControlLayout layout) {
		AlertDialog.Builder builder = new AlertDialog.Builder(layout.getContext());
		builder.setTitle(R.string.customctrl_selectdefault);
		builder.setPositiveButton(android.R.string.cancel, null);

		final AlertDialog dialog = builder.create();
		FileListView flv = new FileListView(dialog, "json");
		flv.lockPathAt(Tools.CTRLMAP_PATH);
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

	private static String doSaveCtrl(String name, final ControlLayout layout) throws Exception {
		String jsonPath = Tools.CTRLMAP_PATH + "/" + name + ".json";
		layout.saveLayout(jsonPath);

		return jsonPath;
	}

	public static void save(final boolean exit, final ControlLayout layout) {
		final Context ctx = layout.getContext();
		final EditText edit = new EditText(ctx);
		edit.setSingleLine();
		edit.setText(selectedName);

		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(R.string.global_save);
		builder.setView(edit);
		builder.setPositiveButton(android.R.string.ok, null);
		builder.setNegativeButton(android.R.string.cancel, null);
		if (exit) {
			builder.setNeutralButton(R.string.mcn_exit_call, new AlertDialog.OnClickListener(){
					@Override
					public void onClick(DialogInterface p1, int p2) {
						layout.setModifiable(false);
						if(ctx instanceof MainActivity) {
							((MainActivity) ctx).leaveCustomControls();
						}else{
							((CustomControlsActivity) ctx).isModified = false;
							((Activity)ctx).onBackPressed();
						}
		//			    setResult(Activity.RESULT_OK, new Intent());
		//				CustomControlsActivity.super.onBackPressed();
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
									edit.setError(ctx.getResources().getString(R.string.global_error_field_empty));
								} else {
									try {
										String jsonPath = doSaveCtrl(edit.getText().toString(),layout);

										Toast.makeText(ctx, ctx.getString(R.string.global_save) + ": " + jsonPath, Toast.LENGTH_SHORT).show();

										dialog.dismiss();
										if (exit) {
											if(ctx instanceof MainActivity) {
												((MainActivity) ctx).leaveCustomControls();
											}else{
												((Activity)ctx).onBackPressed();
											}
											//CustomControlsActivity.super.onBackPressed();
										}
									} catch (Throwable th) {
										Tools.showError(ctx, th, exit);
									}
								}
							}
						});
				}
			});
		dialog.show();

	}

	public static void load(final ControlLayout layout) {
		AlertDialog.Builder builder = new AlertDialog.Builder(layout.getContext());
		builder.setTitle(R.string.global_load);
		builder.setPositiveButton(android.R.string.cancel, null);

		final AlertDialog dialog = builder.create();
		FileListView flv = new FileListView(dialog, "json");
		flv.listFileAt(Tools.CTRLMAP_PATH);
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

	private static void loadControl(String path,ControlLayout layout) {
		try {
			layout.loadLayout(path);
			selectedName = new File(path).getName();
			// Remove `.json`
			selectedName = selectedName.substring(0, selectedName.length() - 5);
		} catch (Exception e) {
			Tools.showError(layout.getContext(), e);
		}
	}
}
