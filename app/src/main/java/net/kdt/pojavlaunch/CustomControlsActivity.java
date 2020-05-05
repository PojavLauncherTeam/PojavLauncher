package net.kdt.pojavlaunch;

import android.content.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import com.google.gson.*;
import com.kdt.filerapi.*;
import java.io.*;
import java.util.*;
import net.kdt.pojavlaunch.value.customcontrols.*;
import org.lwjgl.input.*;

public class CustomControlsActivity extends AppCompatActivity
{
	private DrawerLayout drawerLayout;
    private NavigationView navDrawer;
	private ControlsLayout ctrlLayout;
	private CustomControls mCtrl;
	
	private SharedPreferences mPref;
	
	public boolean isModified = false;
	
	private Gson gson;
	private String selectedName = "new_control";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.control_mapping);
		
		mPref = getSharedPreferences(getPackageName() + "_preferences", Context.MODE_PRIVATE);
		
		gson = new GsonBuilder().setPrettyPrinting().create();
		
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
							ctrlLayout.addControlButton(new ControlButton("New", Keyboard.CHAR_NONE, 100, 100));
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
		
		mCtrl = new CustomControls();
		String defaultControl = mPref.getString("defaultCtrl", "");
		if (defaultControl.isEmpty() || defaultControl.endsWith("/default.json")) {
			generateDefaultControlMap();
			try {
				doSaveCtrl("default");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			loadControl(defaultControl);
		}
		
		ctrlLayout = (ControlsLayout) findViewById(R.id.customctrl_controllayout);
		ctrlLayout.setActivity(this);
		ctrlLayout.loadLayout(mCtrl);
		ctrlLayout.setModifiable(true);
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
		mPref.edit().putString("defaultCtrl", path).commit();
	}
	
	private void dialogSelectDefaultCtrl() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.customctrl_selectdefault);
		builder.setPositiveButton(android.R.string.cancel, null);

		final AlertDialog dialog = builder.create();
		FileListView flv = new FileListView(this);
		flv.listFileAt(Tools.CTRLMAP_PATH);
		flv.setFileSelectedListener(new FileSelectedListener(){

				@Override
				public void onFileSelected(File file, String path, String name) {
					if (name.endsWith(".json")) {
						setDefaultControlJson(path);
						dialog.dismiss();
					}
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
			builder.setNeutralButton("Exit without save", new AlertDialog.OnClickListener(){
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
		builder.setTitle(R.string.customctrl_title_selectctrl);
		builder.setPositiveButton(android.R.string.cancel, null);

		final AlertDialog dialog = builder.create();
		FileListView flv = new FileListView(this);
		flv.listFileAt(Tools.CTRLMAP_PATH);
		flv.setFileSelectedListener(new FileSelectedListener(){

				@Override
				public void onFileSelected(File file, String path, String name) {
					if (name.endsWith(".json")) {
						loadControl(path);
						dialog.dismiss();
					}
				}
			});
		dialog.setView(flv);
		dialog.show();
	}

	private void loadControl(String path) {
		try {
			mCtrl = gson.fromJson(Tools.read(path), CustomControls.class);
			ctrlLayout.loadLayout(mCtrl);
			
			selectedName = new File(path).getName();
			// Remove `.json`
			selectedName = selectedName.substring(0, selectedName.length() - 5);
		} catch (Exception e) {
			Tools.showError(CustomControlsActivity.this, e);
		}
	}
	
	private void generateDefaultControlMap() {
		List<ControlButton> btn = mCtrl.button;
		btn.add(ControlButton.getSpecialButtons()[0].clone()); // Keyboard
		btn.add(ControlButton.getSpecialButtons()[1].clone()); // GUI
		// btn.add(ControlButton.getSpecialButtons()[2]); // Toggle mouse
		btn.add(new ControlButton(this, R.string.control_debug, Keyboard.KEY_F3, ControlButton.pixelOf2dp, ControlButton.pixelOf2dp, false));
		btn.add(new ControlButton(this, R.string.control_chat, Keyboard.KEY_T, ControlButton.pixelOf2dp * 2 + ControlButton.pixelOf80dp, ControlButton.pixelOf2dp, false)); 
		btn.add(new ControlButton(this, R.string.control_listplayers, Keyboard.KEY_TAB, ControlButton.pixelOf2dp * 4 + ControlButton.pixelOf80dp * 3, ControlButton.pixelOf2dp, false));
		btn.add(new ControlButton(this, R.string.control_thirdperson, Keyboard.KEY_F5, ControlButton.pixelOf2dp, ControlButton.pixelOf30dp + ControlButton.pixelOf2dp, false));
	}
}
