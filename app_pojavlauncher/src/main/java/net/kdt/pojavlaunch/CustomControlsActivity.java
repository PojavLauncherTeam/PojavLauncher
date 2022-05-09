package net.kdt.pojavlaunch;

import android.app.Activity;
import android.content.*;
import android.os.*;

import androidx.appcompat.app.*;

import android.widget.*;

import androidx.drawerlayout.widget.DrawerLayout;

import com.google.gson.JsonSyntaxException;
import com.kdt.pickafile.*;
import java.io.*;

import net.kdt.pojavlaunch.prefs.*;
import net.kdt.pojavlaunch.customcontrols.*;


public class CustomControlsActivity extends BaseActivity {
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerNavigationView;
	private ControlLayout mControlLayout;

	public boolean isModified = false;
	private static String sSelectedName = "new_control";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("fromMainActivity", false)) {
            // TODO translucent!
            // setTheme(androidx.appcompat.R.style.Theme_AppCompat_Translucent);
        }
        
		setContentView(R.layout.activity_custom_controls);

		mControlLayout = (ControlLayout) findViewById(R.id.customctrl_controllayout);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.customctrl_drawerlayout);
		mDrawerNavigationView = (ListView) findViewById(R.id.customctrl_navigation_view);

		mDrawerNavigationView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.menu_customcontrol)));
		mDrawerNavigationView.setOnItemClickListener((parent, view, position, id) -> {
			switch(position) {
				case 0:
					mControlLayout.addControlButton(new ControlData("New"));
					break;
				case 1:
					mControlLayout.addDrawer(new ControlDrawerData());
					break;
				case 2:
					load(mControlLayout);
					break;
				case 3:
					save(false, mControlLayout);
					break;
				case 4:
					dialogSelectDefaultCtrl(mControlLayout);
					break;
			}
			mDrawerLayout.closeDrawers();
		});
		/*mDrawerNavigationView.setNavigationItemSelectedListener(
				menuItem -> {
					switch (menuItem.getItemId()) {
						case R.id.menu_ctrl_load:
							load(mControlLayout);
							break;
						case R.id.menu_ctrl_add:
							mControlLayout.addControlButton(new ControlData("New"));
							break;
						case R.id.menu_ctrl_add_drawer:
							mControlLayout.addDrawer(new ControlDrawerData());
							break;
						case R.id.menu_ctrl_selectdefault:
							dialogSelectDefaultCtrl(mControlLayout);
							break;
						case R.id.menu_ctrl_save:
							save(false, mControlLayout);
							break;
					}
					//Toast.makeText(MainActivity.this, menuItem.getTitle() + ":" + menuItem.getItemId(), Toast.LENGTH_SHORT).show();

					mDrawerLayout.closeDrawers();
					return true;
				});
		*/
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
		if(Build.VERSION.SDK_INT < 29)flv.listFileAt(Tools.CTRLMAP_PATH);
		else flv.lockPathAt(Tools.CTRLMAP_PATH);
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
			ctrlLayout.loadLayout(Tools.GLOBAL_GSON.fromJson(Tools.read(path), CustomControls.class));
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
			sSelectedName = new File(path).getName();
			// Remove `.json`
			sSelectedName = sSelectedName.substring(0, sSelectedName.length() - 5);
		} catch (Exception e) {
			Tools.showError(layout.getContext(), e);
		}
	}
}
