package net.kdt.pojavlaunch;

import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.*;
import androidx.drawerlayout.widget.*;
import com.google.android.material.navigation.*;
import com.google.gson.*;
import com.kdt.filerapi.*;
import java.io.*;
import net.kdt.pojavlaunch.*;
import net.kdt.pojavlaunch.value.customcontrols.*;

public class CustomControlsActivity extends AppCompatActivity
{
	private DrawerLayout drawerLayout;
    private NavigationView navDrawer;

	private ControlsLayout ctrlLayout;
	
	private String selectedName = "";

	private CustomControls mCtrl;
	
	private Gson gson;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.control_mapping);
		
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
							ControlButton ctrlBtn = new ControlButton();
							ctrlBtn.name = "New";
							ctrlBtn.x = 100;
							ctrlBtn.y = 100;
							ctrlLayout.addControlButton(ctrlBtn);
							break;
						case R.id.menu_ctrl_edit: // openLogOutput();
							break;
						case R.id.menu_ctrl_remove: // toggleDebug();
							break;
					}
					//Toast.makeText(MainActivity.this, menuItem.getTitle() + ":" + menuItem.getItemId(), Toast.LENGTH_SHORT).show();

					drawerLayout.closeDrawers();
					return true;
				}
			});
		
		mCtrl = new CustomControls();
		/*
		ControlButton ctrlEx = new ControlButton();
		ctrlEx.name = "Test";
		ctrlEx.x = 100;
		ctrlEx.y = 100;
		
		mCtrl.button.add(ctrlEx);
		*/
		ctrlLayout = (ControlsLayout) findViewById(R.id.customctrl_controllayout);
		ctrlLayout.loadLayout(mCtrl);
		ctrlLayout.setCanMove(true);
	}

	@Override
	public void onBackPressed() {
		final EditText edit = new EditText(this);
		edit.setSingleLine();
		edit.setText(selectedName);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.global_save);
		builder.setView(edit);
		builder.setPositiveButton(android.R.string.ok, null);
		builder.setNegativeButton(android.R.string.cancel, null);
		builder.setNeutralButton("Exit without save", new AlertDialog.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					CustomControlsActivity.super.onBackPressed();
				}
			});
		final AlertDialog dialog = builder.create();
		dialog.setOnShowListener(new DialogInterface.OnShowListener() {

				@Override
				public void onShow(DialogInterface dialogInterface) {

					Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
					button.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View view) {
								if (edit.getText().toString().isEmpty()) {
									edit.setError(getResources().getString(R.string.global_error_field_empty));
								} else {
									try {
										Tools.write(Tools.CTRLMAP_PATH + "/" + edit.getText().toString() + ".json", gson.toJson(mCtrl));
										dialog.dismiss();
										CustomControlsActivity.super.onBackPressed();
									} catch (Throwable th) {
										Tools.showError(CustomControlsActivity.this, th);
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
		builder.setTitle("Select OptiFine jar file");
		builder.setPositiveButton(android.R.string.cancel, null);

		final AlertDialog dialog = builder.create();
		FileListView flv = new FileListView(this);
		flv.setFileSelectedListener(new FileSelectedListener(){

				@Override
				public void onFileSelected(File file, String path, String name) {
					if (name.endsWith(".json")) {
						// doInstallOptiFine(file);
						dialog.dismiss();
					}
				}
			});
		dialog.setView(flv);
		dialog.show();
	}
}
