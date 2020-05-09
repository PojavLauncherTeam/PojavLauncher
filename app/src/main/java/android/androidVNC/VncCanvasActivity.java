/* 
 * This is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this software; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
 * USA.
 */

//
// CanvasView is the Activity for showing VNC Desktop.
//
package android.androidVNC;

import android.app.*;
import android.content.*;
import android.content.DialogInterface.*;
import android.content.res.*;
import android.database.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.antlersoft.android.bc.*;
import com.theqvd.android.xpro.*;
import java.io.*;
import java.text.*;
import java.util.*;
import net.kdt.pojavlaunch.*;
import net.kdt.pojavlaunch.prefs.*;

import android.app.AlertDialog;
import com.theqvd.android.xpro.Config;
import net.kdt.pojavlaunch.value.customcontrols.*;
import com.google.gson.*;
import org.lwjgl.opengl.*;
import android.view.inputmethod.*;

public class VncCanvasActivity extends AppCompatActivity
{
	private NavigationView navDrawer;
	private DrawerLayout drawerLayout;

	private final static String TAG = "VncCanvasActivity";

	AbstractInputHandler inputHandler;

	VncCanvas vncCanvas;
	VncDatabase database;

	private MenuItem[] inputModeMenuItems;
	private AbstractInputHandler inputModeHandlers[];
	private ConnectionBean connection;
	private boolean trackballButtonDown;
	private static final int inputModeIds[] = { R.id.itemInputFitToScreen,
			R.id.itemInputTouchpad,
			R.id.itemInputMouse, R.id.itemInputPan,
			R.id.itemInputTouchPanTrackballMouse,
			R.id.itemInputDPadPanTouchMouse, R.id.itemInputTouchPanZoomMouse };

	ZoomControls zoomer;
	Panner panner;
	
	java.lang.Process mXServerProcess;
	MCProfile.Builder mProfile;
	JMinecraftVersionList.Version mVersionInfo;
	SimpleShellProcess mJavaProcess, mXVNCProcess;

	public volatile boolean ignoreDisconnect = false;
	
	private LinearLayout contentLog;
	private TextView textLog;
	private ScrollView contentScroll;
	private ToggleButton toggleLog;
	private ControlsLayout mControlLayout;
	
	private SharedPreferences mVncPrefs;
	
	private String modPath;
	@Override
	public void onCreate(Bundle icicle) {

		super.onCreate(icicle);
		
		// Generate fake XRandR info
		try {
			Tools.write(Tools.homeJreDir + "/home/fakexrandr.txt",
						"Screen 0: minimum 1 x 1, current " + AndroidDisplay.windowWidth + " x " + AndroidDisplay.windowHeight + ", maximum " + AndroidDisplay.windowWidth + " x " + AndroidDisplay.windowHeight + "\n" +
						"screen connected " + AndroidDisplay.windowWidth + "x" + AndroidDisplay.windowHeight + "+0+0 0mm x 0mm\n" +
						"   " + AndroidDisplay.windowWidth + "x" + AndroidDisplay.windowHeight + "      0.00*\n"
			);
		} catch (Throwable th) {
			Toast.makeText(this, "Could not generate fake XRandR info, resolution will not correctly!", Toast.LENGTH_LONG).show();
		}

		MetaKeyBean.initStatic();

		mVncPrefs = getSharedPreferences("vnc_preferences", MODE_PRIVATE);
		mProfile = PojavProfile.getCurrentProfileContent(this);
		mVersionInfo = Tools.getVersionInfo(mProfile.getVersion());
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
							 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.canvas);

		final Bundle extras = getIntent().getExtras();
		
		if (extras != null) {
			modPath = extras.getString("launchJar", "");
			if (modPath == null || modPath.isEmpty()) {
				modPath = null;
			}
		} else {
			modPath = null;
		}
		
		mControlLayout = findViewById(R.id.main_controllayout);
		mControlLayout.setModifiable(false);
		if (modPath == null) {
			ControlButton[] specialButtons = ControlButton.getSpecialButtons();
			specialButtons[0].specialButtonListener = new View.OnClickListener(){
				@Override
				public void onClick(View v) {

				}
			};
			specialButtons[1].specialButtonListener = new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					// showKeyboard(); 
				}
			};
			// MetaKeyBean.keysByMouseButton.get(VncCanvas.MOUSE_BUTTON_RIGHT);
			specialButtons[2].specialButtonListener = new ControlButton.TouchListener(){
				@Override
				public void onTouch(boolean down) {
					// showKeyboard(); 
				}
			};
			specialButtons[3].specialButtonListener = new ControlButton.TouchListener(){
				@Override
				public void onTouch(boolean down) {
					// showKeyboard(); 
				}
			};
			
			mControlLayout.loadLayout(getSharedPreferences(getPackageName() + "_preferences", MODE_PRIVATE).getString("defaultCtrl", Tools.CTRLMAP_PATH + "/default.json"));
			mControlLayout.setControlVisible(false);
			boolean controlVisible = false;
			for (ControlView specialView : mControlLayout.getSpecialControlViewArray()) {
				switch (specialView.getProperties().keycode) {
					case ControlButton.SPECIALBTN_KEYBOARD: 
						InputMethodManager inputMgr = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
						inputMgr.toggleSoftInput(0, 0);
						break;
					case ControlButton.SPECIALBTN_TOGGLECTRL:
						controlVisible = !controlVisible;
						mControlLayout.setControlVisible(controlVisible);
						break;
					case ControlButton.SPECIALBTN_MOUSEPRI: 
						break;
					case ControlButton.SPECIALBTN_MOUSESEC: 
						break;
				}
			}
			
			mControlLayout.setupKeyEvent(new ControlsLayout.ControlListener(){
					@Override
					public void onKey(MetaKeyBase vncKey, boolean down)
					{
						vncCanvas.sendKeyboardKey(new MetaKeyBean(0, 0, vncKey), down);
					}
				});
		} else {
			mControlLayout.setVisibility(View.GONE);
		}
		
		database = new VncDatabase(VncCanvasActivity.this);
		connection = new ConnectionBean();
		
		contentLog = (LinearLayout) findViewById(R.id.content_log_layout);
		contentScroll = (ScrollView) findViewById(R.id.content_log_scroll);
		textLog = (TextView) contentScroll.getChildAt(0);
		textLog.setTypeface(Typeface.MONOSPACE);
		toggleLog = (ToggleButton) findViewById(R.id.content_log_toggle_log);
		toggleLog.setChecked(true);

		vncCanvas = (VncCanvas) findViewById(R.id.vnc_canvas);
		zoomer = (ZoomControls) findViewById(R.id.zoomer);
		
		// Menu
		drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_options);

		navDrawer = (NavigationView) findViewById(R.id.main_navigation_view);
		navDrawer.setNavigationItemSelectedListener(
			new NavigationView.OnNavigationItemSelectedListener() {
				@Override
				public boolean onNavigationItemSelected(MenuItem item) {
					switch (item.getItemId()) {
						case R.id.nav_forceclose: dialogForceClose();
							return true;
						case R.id.nav_viewlog: openLogOutput();
							break;
						case R.id.itemInfo:
							vncCanvas.showConnectionInfo();
							return true;
						case R.id.itemSpecialKeys:
							showDialog(R.layout.metakey);
							break;
							// return true;
						case R.id.itemColorMode:
							selectColorModel();
							break;
							// return true;
							// Following sets one of the scaling options
						case R.id.itemZoomable:
						case R.id.itemOneToOne:
						case R.id.itemFitToScreen:
							AbstractScaling.getById(item.getItemId()).setScaleTypeForActivity(VncCanvasActivity.this);
							item.setChecked(true);
							showPanningState();
							break;
							// return true;
						case R.id.itemCenterMouse:
							vncCanvas.warpMouse(vncCanvas.absoluteXPosition
												+ vncCanvas.getVisibleWidth() / 2,
												vncCanvas.absoluteYPosition + vncCanvas.getVisibleHeight()
												/ 2);
							break;
							// return true;
						case R.id.itemEnterText:
							showDialog(R.layout.entertext);
							return true;
						case R.id.itemCtrlAltDel:
							vncCanvas.sendMetaKey(MetaKeyBean.keyCtrlAltDel);
							return true;
						case R.id.itemFollowMouse:
							boolean newFollow = !connection.getFollowMouse();
							item.setChecked(newFollow);
							connection.setFollowMouse(newFollow);
							if (newFollow) {
								vncCanvas.panToMouse();
							}
							
							editPref().putBoolean("isFollowMouse", newFollow).commit();
							// connection.save(database.getWritableDatabase());
							return true;
						case R.id.itemFollowPan:
							boolean newFollowPan = !connection.getFollowPan();
							item.setChecked(newFollowPan);
							connection.setFollowPan(newFollowPan);
							editPref().putBoolean("isFollowPan", newFollowPan).commit();
							return true;
						case R.id.itemArrowLeft:
							vncCanvas.sendMetaKey(MetaKeyBean.keyArrowLeft);
							return true;
						case R.id.itemArrowUp:
							vncCanvas.sendMetaKey(MetaKeyBean.keyArrowUp);
							return true;
						case R.id.itemArrowRight:
							vncCanvas.sendMetaKey(MetaKeyBean.keyArrowRight);
							return true;
						case R.id.itemArrowDown:
							vncCanvas.sendMetaKey(MetaKeyBean.keyArrowDown);
							return true;
						case R.id.itemSendKeyAgain:
							sendSpecialKeyAgain();
							return true;
						case R.id.itemOpenDoc:
							Utils.showDocumentation(VncCanvasActivity.this);
							return true;
						default:
							AbstractInputHandler input = getInputHandlerById(item.getItemId());
							if (input != null) {
								inputHandler = input;
								connection.setInputMode(input.getName());
								
								editPref().putString("inputMode", input.getName()).commit();
								if (input.getName().equals(TOUCHPAD_MODE)) {
									connection.setFollowMouse(true);
									
									editPref().putBoolean("isFollowMouse", true);
								}
								item.setChecked(true);
								showPanningState();
								// connection.save(database.getWritableDatabase());
								return true;
							}
					}
					
					//Toast.makeText(MainActivity.this, menuItem.getTitle() + ":" + menuItem.getItemId(), Toast.LENGTH_SHORT).show();

					drawerLayout.closeDrawers();
					return true;
				}
			});
		Menu menu = navDrawer.getMenu();
		if (vncCanvas.scaling != null)
			menu.findItem(vncCanvas.scaling.getId()).setChecked(true);

		Menu inputMenu = menu.findItem(R.id.itemInputMode).getSubMenu();

		inputModeMenuItems = new MenuItem[inputModeIds.length];
		for (int i = 0; i < inputModeIds.length; i++) {
			inputModeMenuItems[i] = inputMenu.findItem(inputModeIds[i]);
		}
		updateInputMenu();
		menu.findItem(R.id.itemFollowMouse).setChecked(
			connection.getFollowMouse());
		menu.findItem(R.id.itemFollowPan).setChecked(connection.getFollowPan());

		// Launch X Server before init anything!
		final Config config = new Config(this);
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					String cmd = Config.xvnccmd + " -geometry "+ config.get_width_pixels() + "x"  + config.get_height_pixels();
					cmd += config.isAppConfig_remote_vnc_allowed() ? "" : " " + Config.notAllowRemoteVncConns;
					// Not enable XRender because a Java X11 connection cause XVnc server to crash.
					// cmd += config.isAppConfig_render() ? " +render" : "";
					cmd += config.isAppConfig_xinerama() ? " +xinerama" : "";
					Log.i("VncCanvasActivity", "Launching: "+cmd);
					
					String cmdList[] = cmd.split("[ ]+");
					try {
						mXVNCProcess = new SimpleShellProcess(new SimpleShellProcess.OnPrintListener(){

								@Override
								public void onPrintLine(String text)
								{
									log(text);
								}
							});
						mXVNCProcess.initInputStream(VncCanvasActivity.this);
						mXVNCProcess.writeToProcess(cmdList);
						
						launchJava();
						int javaResultCode = mJavaProcess.waitFor();
						
						// Kill XVnc server before exit
						ignoreDisconnect = true;
						mXVNCProcess.terminate();
						
						if (javaResultCode == 0) {
							runOnUiThread(new Runnable(){
								@Override
								public void run() {
									Toast.makeText(VncCanvasActivity.this, R.string.mcn_exit_title, Toast.LENGTH_SHORT).show();
									// finish();
								}
							});
						} else {
							Tools.dialogOnUiThread(VncCanvasActivity.this, getString(R.string.mcn_exit_title), getString(R.string.mcn_exit_crash, javaResultCode));
						}
					} catch (Throwable th) {
						Tools.showError(VncCanvasActivity.this, th);
					}
				}
			}).start();
		
		new Handler().postDelayed(new Runnable(){

				@Override
				public void run()
				{

					Uri data = Uri.parse(Config.vnccmd); // extras.getParcelable("x11");
					if ((data != null) && (data.getScheme().equals("vnc"))) {
						String host = data.getHost();
						// This should not happen according to Uri contract, but bug introduced in Froyo (2.2)
						// has made this parsing of host necessary
						int index = host.indexOf(':');
						int port;
						if (index != -1)
						{
							try
							{
								port = Integer.parseInt(host.substring(index + 1));
							}
							catch (NumberFormatException nfe)
							{
								port = 0;
							}
							host = host.substring(0,index);
						}
						else
						{
							port = data.getPort();
						}
						if (host.equals(VncConstants.CONNECTION))
						{
							/*
							if (connection.Gen_read(database.getReadableDatabase(), port))
							{
								MostRecentBean bean = androidVNC.getMostRecent(database.getReadableDatabase());
								if (bean != null)
								{
									bean.setConnectionId(connection.get_Id());
									bean.Gen_update(database.getWritableDatabase());
								}
							}
							*/
						}
						else
						{
							connection.setAddress(host);
							connection.setNickname(connection.getAddress());
							connection.setPort(port);
							List<String> path = data.getPathSegments();
							if (path.size() >= 1) {
								connection.setColorModel(path.get(0));
								editPref().putString("colorModel", path.get(0)).commit();
							}
							if (path.size() >= 2) {
								connection.setPassword(path.get(1));
							}
							// connection.save(database.getWritableDatabase());
						}
					} else {
						if (extras != null) {
							connection.Gen_populate((ContentValues) extras
													.getParcelable(VncConstants.CONNECTION));
						}
						if (connection.getPort() == 0)
							connection.setPort(5900);

						// Parse a HOST:PORT entry
						String host = connection.getAddress();
						if (host.indexOf(':') > -1) {
							String p = host.substring(host.indexOf(':') + 1);
							try {
								connection.setPort(Integer.parseInt(p));
							} catch (Exception e) {
							}
							connection.setAddress(host.substring(0, host.indexOf(':')));
						}
					}
					
					vncCanvas.initializeVncCanvas(VncCanvasActivity.this, connection, new Runnable() {
							public void run() {
								setModes();
							}
						});
					zoomer.hide();
					zoomer.setOnZoomInClickListener(new View.OnClickListener() {

							/*
							 * (non-Javadoc)
							 * 
							 * @see android.view.View.OnClickListener#onClick(android.view.View)
							 */
							@Override
							public void onClick(View v) {
								showZoomer(true);
								vncCanvas.scaling.zoomIn(VncCanvasActivity.this);

							}

						});
					zoomer.setOnZoomOutClickListener(new View.OnClickListener() {

							/*
							 * (non-Javadoc)
							 * 
							 * @see android.view.View.OnClickListener#onClick(android.view.View)
							 */
							@Override
							public void onClick(View v) {
								showZoomer(true);
								vncCanvas.scaling.zoomOut(VncCanvasActivity.this);

							}

						});
				
					panner = new Panner(VncCanvasActivity.this, vncCanvas.handler);
					inputHandler = getInputHandlerById(R.id.itemInputTouchpad);
				}
			}, 200);
	}
	
	public SharedPreferences.Editor editPref() {
		return mVncPrefs.edit();
	}

	private void openLogOutput() {
		contentLog.setVisibility(View.VISIBLE);
	}

	public void closeLogOutput(View view) {
		contentLog.setVisibility(View.GONE);
	}
	
	public void dialogForceClose()
	{
		new AlertDialog.Builder(this)
			.setMessage(R.string.mcn_exit_confirm)
			.setNegativeButton(android.R.string.cancel, null)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					mXServerProcess.destroy();
					mJavaProcess.terminate();

					finish();
				}
			})
			.show();
	}

	private void logn(String str) {
		log(str + "\n");
	}

	private void log(final String str) {
		runOnUiThread(new Runnable(){

				@Override
				public void run()
				{
					if (toggleLog.isChecked()) {
						textLog.append(str);
						contentScroll.fullScroll(ScrollView.FOCUS_DOWN);
					}
				}
			});
	}

	private void launchJava() {
		try {
			/*
			 * 17w43a (1.13) and higher change Minecraft arguments tag from
			 * `minecraftArguments` to `arguments` so easily to check
			 * if the selected version requires LWJGL 3 or not.
			 */
			boolean isLwjgl3 = mVersionInfo.arguments != null;

			List<String> mJreArgs = new ArrayList<String>();
			mJreArgs.add("java");
			// Fix LWJGL3 / Minecraft 1.13 unable to load LWJGL3 native libraries.
			mJreArgs.add("-Djava.library.path=$LD_LIBRARY_PATH");
			mJreArgs.add("-Duser.home=" + Tools.MAIN_PATH);
			mJreArgs.add("-Xmx512M");
			
			if (modPath == null) {
				mJreArgs.add("-jar");
				mJreArgs.add(Tools.libraries + "/ClassWrapper.jar");
				mJreArgs.add(Tools.generate(mProfile.getVersion()));
				mJreArgs.add(mVersionInfo.mainClass);
				mJreArgs.addAll(Arrays.asList(getMCArgs()));
			} else {
				mJreArgs.add("-jar");
				mJreArgs.add(modPath);
			}

			mJavaProcess = new SimpleShellProcess(new SimpleShellProcess.OnPrintListener(){
					@Override
					public void onPrintLine(String text) {
						log(text);
					}
				}, LauncherPreferences.PREF_RUNASROOT ? "su" : "sh" + " " + Tools.homeJreDir + "/usr/bin/jre.sh");
			mJavaProcess.initInputStream(this);
			mJavaProcess.writeToProcess("unset LD_PRELOAD");
			/* To prevent Permission Denied, chmod again.
			 * Useful if enable root mode */
			mJavaProcess.writeToProcess("chmod -R 700 " + Tools.homeJreDir);
			mJavaProcess.writeToProcess("cd " + Tools.MAIN_PATH);
			mJavaProcess.writeToProcess("export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/minecraft_lib/lwjgl" + (isLwjgl3 ? "3" : "2"));
			// mJavaProcess.writeToProcess("echo \"Running Minecraft: " + fromStringArray(mJreArgs.toArray(new String[0])) + "\"");
			mJavaProcess.writeToProcess(mJreArgs.toArray(new String[0]));
			mJavaProcess.writeToProcess("exit");
		} catch (Throwable th) {
			th.printStackTrace();
			Tools.showError(this, th);
		}
	}

	private String[] getMCArgs() {
		String username = mProfile.getUsername();
		String versionName = mProfile.getVersion();
		String mcAssetsDir = Tools.ASSETS_PATH;
		String userType = "mojang";

		File gameDir = new File(Tools.MAIN_PATH);
		gameDir.mkdirs();

		Map<String, String> varArgMap = new ArrayMap<String, String>();
		varArgMap.put("auth_player_name", username);
		varArgMap.put("version_name", versionName);
		varArgMap.put("game_directory", gameDir.getAbsolutePath());
		varArgMap.put("assets_root", mcAssetsDir);
		varArgMap.put("assets_index_name", mVersionInfo.assets);
		varArgMap.put("auth_uuid", mProfile.getProfileID());
		varArgMap.put("auth_access_token", mProfile.getAccessToken());
		varArgMap.put("user_properties", "{}");
		varArgMap.put("user_type", userType);
		varArgMap.put("version_type", mVersionInfo.type);
		varArgMap.put("game_assets", Tools.ASSETS_PATH);

		List<String> minecraftArgs = new ArrayList<String>();
		if (mVersionInfo.arguments != null) {
			// Support Minecraft 1.13
			for (Object arg : mVersionInfo.arguments.game) {
				if (arg instanceof String) {
					minecraftArgs.add((String) arg);
				} else {
					/*
					 for (JMinecraftVersionList.Arguments.ArgValue.ArgRules rule : arg.rules) {
					 // rule.action = allow
					 // TODO implement this
					 }
					 */
				}
			}
		}

		String[] argsFromJson = insertVariableArgument(
			splitAndFilterEmpty(
				mVersionInfo.minecraftArguments == null ?
				fromStringArray(minecraftArgs.toArray(new String[0])):
				mVersionInfo.minecraftArguments
			), varArgMap
		);
		// Tools.dialogOnUiThread(this, "Result args", Arrays.asList(argsFromJson).toString());
		return argsFromJson;
	}

	private String fromStringArray(String[] strArr) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < strArr.length; i++) {
			if (i > 0) builder.append(" ");
			builder.append(strArr[i]);
		}

		return builder.toString();
	}

	private String[] splitAndFilterEmpty(String argStr) {
		List<String> strList = new ArrayList<String>();
		strList.add("--fullscreen");
		for (String arg : argStr.split(" ")) {
			if (!arg.isEmpty()) {
				strList.add(arg);
			}
		}
		return strList.toArray(new String[0]);
	}

	private String[] insertVariableArgument(String[] args, Map<String, String> keyValueMap) {
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			String argVar = null;
			if (arg.startsWith("${") && arg.endsWith("}")) {
				argVar = arg.substring(2, arg.length() - 1);
				for (Map.Entry<String, String> keyValue : keyValueMap.entrySet()) {
					if (argVar.equals(keyValue.getKey())) {
						args[i] = keyValue.getValue();
					}
				}
			}
		}
		return args;
	}

	/**
	 * Set modes on start to match what is specified in the ConnectionBean;
	 * color mode (already done) scaling, input mode
	 */
	void setModes() {
		AbstractInputHandler handler = getInputHandlerByName(mVncPrefs.getString("inputMode", ""));
		AbstractScaling.getByScaleType(connection.getScaleMode())
				.setScaleTypeForActivity(this);
		this.inputHandler = handler;
		showPanningState();
	}

	ConnectionBean getConnection() {
		return connection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case R.layout.entertext:
			return new EnterTextDialog(this);
		}
		// Default to meta key dialog
		return new MetaKeyDialog(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPrepareDialog(int, android.app.Dialog)
	 */
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		if (dialog instanceof ConnectionSettable)
			((ConnectionSettable) dialog).setConnection(connection);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// ignore orientation/keyboard change
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onStop() {
		vncCanvas.disableRepaints();
		super.onStop();
	}

	@Override
	protected void onRestart() {
		vncCanvas.enableRepaints();
		super.onRestart();
	}

	/**
	 * Change the input mode sub-menu to reflect change in scaling
	 */
	void updateInputMenu() {
		if (inputModeMenuItems == null || vncCanvas.scaling == null) {
			return;
		}
		for (MenuItem item : inputModeMenuItems) {
			item.setEnabled(vncCanvas.scaling
					.isValidInputMode(item.getItemId()));
			if (getInputHandlerById(item.getItemId()) == inputHandler)
				item.setChecked(true);
		}
	}

	/**
	 * If id represents an input handler, return that; otherwise return null
	 * 
	 * @param id
	 * @return
	 */
	AbstractInputHandler getInputHandlerById(int id) {
		if (inputModeHandlers == null) {
			inputModeHandlers = new AbstractInputHandler[inputModeIds.length];
		}
		for (int i = 0; i < inputModeIds.length; ++i) {
			if (inputModeIds[i] == id) {
				if (inputModeHandlers[i] == null) {
					switch (id) {
					case R.id.itemInputFitToScreen:
						inputModeHandlers[i] = new FitToScreenMode();
						break;
					case R.id.itemInputPan:
						inputModeHandlers[i] = new PanMode();
						break;
					case R.id.itemInputMouse:
						inputModeHandlers[i] = new MouseMode();
						break;
					case R.id.itemInputTouchPanTrackballMouse:
						inputModeHandlers[i] = new TouchPanTrackballMouse();
						break;
					case R.id.itemInputDPadPanTouchMouse:
						inputModeHandlers[i] = new DPadPanTouchMouseMode();
						break;
					case R.id.itemInputTouchPanZoomMouse:
						inputModeHandlers[i] = vncCanvas.new ZoomInputHandler();
						break;
					case R.id.itemInputTouchpad:
						inputModeHandlers[i] = vncCanvas.new TouchpadInputHandler();
						break;
					}
				}
				return inputModeHandlers[i];
			}
		}
		return null;
	}

	AbstractInputHandler getInputHandlerByName(String name) {
		AbstractInputHandler result = null;
		for (int id : inputModeIds) {
			AbstractInputHandler handler = getInputHandlerById(id);
			if (handler.getName().equals(name)) {
				result = handler;
				break;
			}
		}
		if (result == null) {
			result = getInputHandlerById(R.id.itemInputTouchPanZoomMouse);
		}
		return result;
	}
	
	int getModeIdFromHandler(AbstractInputHandler handler) {
		for (int id : inputModeIds) {
			if (handler == getInputHandlerById(id))
				return id;
		}
		return R.id.itemInputTouchPanZoomMouse;
	}

	private MetaKeyBean lastSentKey;

	private void sendSpecialKeyAgain() {
		if (lastSentKey == null
				|| lastSentKey.get_Id() != connection.getLastMetaKeyId()) {
			ArrayList<MetaKeyBean> keys = new ArrayList<MetaKeyBean>();
			Cursor c = database.getReadableDatabase().rawQuery(
					MessageFormat.format("SELECT * FROM {0} WHERE {1} = {2}",
							MetaKeyBean.GEN_TABLE_NAME,
							MetaKeyBean.GEN_FIELD__ID, connection
									.getLastMetaKeyId()),
					MetaKeyDialog.EMPTY_ARGS);
			MetaKeyBean.Gen_populateFromCursor(c, keys, MetaKeyBean.NEW);
			c.close();
			if (keys.size() > 0) {
				lastSentKey = keys.get(0);
			} else {
				lastSentKey = null;
			}
		}
		if (lastSentKey != null)
			vncCanvas.sendMetaKey(lastSentKey);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (isFinishing()) {
			vncCanvas.closeConnection();
			vncCanvas.onDestroy();
			database.close();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent evt) {
		if (keyCode == KeyEvent.KEYCODE_MENU)
			return super.onKeyDown(keyCode, evt);

		return inputHandler.onKeyDown(keyCode, evt);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent evt) {
		if (keyCode == KeyEvent.KEYCODE_MENU)
			return super.onKeyUp(keyCode, evt);

		return inputHandler.onKeyUp(keyCode, evt);
	}

	public void showPanningState() {
		Toast.makeText(this, inputHandler.getHandlerDescription(),
				Toast.LENGTH_SHORT).show();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onTrackballEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			trackballButtonDown = true;
			break;
		case MotionEvent.ACTION_UP:
			trackballButtonDown = false;
			break;
		}
		return inputHandler.onTrackballEvent(event);
	}

	private void selectColorModel() {
		// Stop repainting the desktop
		// because the display is composited!
		vncCanvas.disableRepaints();

		String[] choices = new String[COLORMODEL.values().length];
		int currentSelection = -1;
		for (int i = 0; i < choices.length; i++) {
			COLORMODEL cm = COLORMODEL.values()[i];
			choices[i] = cm.toString();
			if (vncCanvas.isColorModel(cm))
				currentSelection = i;
		}

		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		ListView list = new ListView(this);
		list.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_checked, choices));
		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		list.setItemChecked(currentSelection, true);
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				dialog.dismiss();
				COLORMODEL cm = COLORMODEL.values()[arg2];
				vncCanvas.setColorModel(cm);
				connection.setColorModel(cm.nameString());
				editPref().putString("colorModel", cm.nameString()).commit();
				// connection.save(database.getWritableDatabase());
				Toast.makeText(VncCanvasActivity.this,
						"Updating Color Model to " + cm.toString(),
						Toast.LENGTH_SHORT).show();
			}
		});
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				Log.i(TAG, "Color Model Selector dismissed");
				// Restore desktop repaints
				vncCanvas.enableRepaints();
			}
		});
		dialog.setContentView(list);
		dialog.show();
	}

	float panTouchX, panTouchY;

	/**
	 * Pan based on touch motions
	 * 
	 * @param event
	 */
	private boolean pan(MotionEvent event) {
		float curX = event.getX();
		float curY = event.getY();
		int dX = (int) (panTouchX - curX);
		int dY = (int) (panTouchY - curY);

		return vncCanvas.pan(dX, dY);
	}

	boolean defaultKeyDownHandler(int keyCode, KeyEvent evt) {
		if (vncCanvas.processLocalKeyEvent(keyCode, evt))
			return true;
		return super.onKeyDown(keyCode, evt);
	}

	boolean defaultKeyUpHandler(int keyCode, KeyEvent evt) {
		if (vncCanvas.processLocalKeyEvent(keyCode, evt))
			return true;
		return super.onKeyUp(keyCode, evt);
	}

	boolean touchPan(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			panTouchX = event.getX();
			panTouchY = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			pan(event);
			panTouchX = event.getX();
			panTouchY = event.getY();
			break;
		case MotionEvent.ACTION_UP:
			pan(event);
			break;
		}
		return true;
	}

	private static int convertTrackballDelta(double delta) {
		return (int) Math.pow(Math.abs(delta) * 6.01, 2.5)
				* (delta < 0.0 ? -1 : 1);
	}

	boolean trackballMouse(MotionEvent evt) {
		int dx = convertTrackballDelta(evt.getX());
		int dy = convertTrackballDelta(evt.getY());

		evt.offsetLocation(vncCanvas.mouseX + dx - evt.getX(), vncCanvas.mouseY
				+ dy - evt.getY());

		if (vncCanvas.processPointerEvent(evt, trackballButtonDown)) {
			return true;
		}
		return VncCanvasActivity.super.onTouchEvent(evt);
	}

	long hideZoomAfterMs;
	static final long ZOOM_HIDE_DELAY_MS = 2500;
	HideZoomRunnable hideZoomInstance = new HideZoomRunnable();

	public void showZoomer(boolean force) {
		if (force || zoomer.getVisibility() != View.VISIBLE) {
			zoomer.show();
			hideZoomAfterMs = SystemClock.uptimeMillis() + ZOOM_HIDE_DELAY_MS;
			vncCanvas.handler
					.postAtTime(hideZoomInstance, hideZoomAfterMs + 10);
		}
	}

	private class HideZoomRunnable implements Runnable {
		public void run() {
			if (SystemClock.uptimeMillis() >= hideZoomAfterMs) {
				zoomer.hide();
			}
		}

	}

	/**
	 * Touches and dpad (trackball) pan the screen
	 * 
	 * @author Michael A. MacDonald
	 * 
	 */
	class PanMode implements AbstractInputHandler {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.androidVNC.AbstractInputHandler#onKeyDown(int,
		 *      android.view.KeyEvent)
		 */
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent evt) {
			// DPAD KeyDown events are move MotionEvents in Panning Mode
			final int dPos = 100;
			boolean result = false;
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_CENTER:
				result = true;
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				onTouchEvent(MotionEvent
						.obtain(1, System.currentTimeMillis(),
								MotionEvent.ACTION_MOVE, panTouchX + dPos,
								panTouchY, 0));
				result = true;
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				onTouchEvent(MotionEvent
						.obtain(1, System.currentTimeMillis(),
								MotionEvent.ACTION_MOVE, panTouchX - dPos,
								panTouchY, 0));
				result = true;
				break;
			case KeyEvent.KEYCODE_DPAD_UP:
				onTouchEvent(MotionEvent
						.obtain(1, System.currentTimeMillis(),
								MotionEvent.ACTION_MOVE, panTouchX, panTouchY
										+ dPos, 0));
				result = true;
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				onTouchEvent(MotionEvent
						.obtain(1, System.currentTimeMillis(),
								MotionEvent.ACTION_MOVE, panTouchX, panTouchY
										- dPos, 0));
				result = true;
				break;
			default:
				result = defaultKeyDownHandler(keyCode, evt);
				break;
			}
			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.androidVNC.AbstractInputHandler#onKeyUp(int,
		 *      android.view.KeyEvent)
		 */
		@Override
		public boolean onKeyUp(int keyCode, KeyEvent evt) {
			// Ignore KeyUp events for DPAD keys in Panning Mode; trackball
			// button switches to mouse mode
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_CENTER:
				inputHandler = getInputHandlerById(R.id.itemInputMouse);
				connection.setInputMode(inputHandler.getName());
				editPref().putString("inputMode", inputHandler.getName()).commit();
				// connection.save(database.getWritableDatabase());
				updateInputMenu();
				showPanningState();
				return true;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				return true;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				return true;
			case KeyEvent.KEYCODE_DPAD_UP:
				return true;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				return true;
			}
			return defaultKeyUpHandler(keyCode, evt);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.androidVNC.AbstractInputHandler#onTouchEvent(android.view.MotionEvent)
		 */
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			return touchPan(event);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.androidVNC.AbstractInputHandler#onTrackballEvent(android.view.MotionEvent)
		 */
		@Override
		public boolean onTrackballEvent(MotionEvent evt) {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.androidVNC.AbstractInputHandler#handlerDescription()
		 */
		@Override
		public CharSequence getHandlerDescription() {
			return getResources().getText(R.string.input_mode_panning);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.androidVNC.AbstractInputHandler#getName()
		 */
		@Override
		public String getName() {
			return "PAN_MODE";
		}

	}

	/**
	 * The touchscreen pans the screen; the trackball moves and clicks the
	 * mouse.
	 * 
	 * @author Michael A. MacDonald
	 * 
	 */
	public class TouchPanTrackballMouse implements AbstractInputHandler {
		private DPadMouseKeyHandler keyHandler = new DPadMouseKeyHandler(VncCanvasActivity.this, vncCanvas.handler);
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.androidVNC.AbstractInputHandler#onKeyDown(int,
		 *      android.view.KeyEvent)
		 */
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent evt) {
			return keyHandler.onKeyDown(keyCode, evt);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.androidVNC.AbstractInputHandler#onKeyUp(int,
		 *      android.view.KeyEvent)
		 */
		@Override
		public boolean onKeyUp(int keyCode, KeyEvent evt) {
			return keyHandler.onKeyUp(keyCode, evt);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.androidVNC.AbstractInputHandler#onTouchEvent(android.view.MotionEvent)
		 */
		@Override
		public boolean onTouchEvent(MotionEvent evt) {
			return touchPan(evt);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.androidVNC.AbstractInputHandler#onTrackballEvent(android.view.MotionEvent)
		 */
		@Override
		public boolean onTrackballEvent(MotionEvent evt) {
			return trackballMouse(evt);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.androidVNC.AbstractInputHandler#handlerDescription()
		 */
		@Override
		public CharSequence getHandlerDescription() {
			return getResources().getText(
					R.string.input_mode_touchpad_pan_trackball_mouse);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.androidVNC.AbstractInputHandler#getName()
		 */
		@Override
		public String getName() {
			return "TOUCH_PAN_TRACKBALL_MOUSE";
		}

	}

	static final String FIT_SCREEN_NAME = "FIT_SCREEN";
	/** Internal name for default input mode with Zoom scaling */
	static final String TOUCH_ZOOM_MODE = "TOUCH_ZOOM_MODE";
	static final String TOUCHPAD_MODE = "TOUCHPAD_MODE";

	/**
	 * In fit-to-screen mode, no panning. Trackball and touchscreen work as
	 * mouse.
	 * 
	 * @author Michael A. MacDonald
	 * 
	 */
	public class FitToScreenMode implements AbstractInputHandler {
		private DPadMouseKeyHandler keyHandler = new DPadMouseKeyHandler(VncCanvasActivity.this, vncCanvas.handler);
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.androidVNC.AbstractInputHandler#onKeyDown(int,
		 *      android.view.KeyEvent)
		 */
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent evt) {
			return keyHandler.onKeyDown(keyCode, evt);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.androidVNC.AbstractInputHandler#onKeyUp(int,
		 *      android.view.KeyEvent)
		 */
		@Override
		public boolean onKeyUp(int keyCode, KeyEvent evt) {
			return keyHandler.onKeyUp(keyCode, evt);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.androidVNC.AbstractInputHandler#onTouchEvent(android.view.MotionEvent)
		 */
		@Override
		public boolean onTouchEvent(MotionEvent evt) {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.androidVNC.AbstractInputHandler#onTrackballEvent(android.view.MotionEvent)
		 */
		@Override
		public boolean onTrackballEvent(MotionEvent evt) {
			return trackballMouse(evt);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.androidVNC.AbstractInputHandler#handlerDescription()
		 */
		@Override
		public CharSequence getHandlerDescription() {
			return getResources().getText(R.string.input_mode_fit_to_screen);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.androidVNC.AbstractInputHandler#getName()
		 */
		@Override
		public String getName() {
			return FIT_SCREEN_NAME;
		}

	}

	/**
	 * Touch screen controls, clicks the mouse.
	 * 
	 * @author Michael A. MacDonald
	 * 
	 */
	class MouseMode implements AbstractInputHandler {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.androidVNC.AbstractInputHandler#onKeyDown(int,
		 *      android.view.KeyEvent)
		 */
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent evt) {
			if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER)
				return true;
			return defaultKeyDownHandler(keyCode, evt);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.androidVNC.AbstractInputHandler#onKeyUp(int,
		 *      android.view.KeyEvent)
		 */
		@Override
		public boolean onKeyUp(int keyCode, KeyEvent evt) {
			if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
				inputHandler = getInputHandlerById(R.id.itemInputPan);
				showPanningState();
				connection.setInputMode(inputHandler.getName());
				// connection.save(database.getWritableDatabase());
				updateInputMenu();
				return true;
			}
			return defaultKeyUpHandler(keyCode, evt);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.androidVNC.AbstractInputHandler#onTouchEvent(android.view.MotionEvent)
		 */
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			// Mouse Pointer Control Mode
			// Pointer event is absolute coordinates.

			vncCanvas.changeTouchCoordinatesToFullFrame(event);
			if (vncCanvas.processPointerEvent(event, true))
				return true;
			return VncCanvasActivity.super.onTouchEvent(event);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.androidVNC.AbstractInputHandler#onTrackballEvent(android.view.MotionEvent)
		 */
		@Override
		public boolean onTrackballEvent(MotionEvent evt) {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.androidVNC.AbstractInputHandler#handlerDescription()
		 */
		@Override
		public CharSequence getHandlerDescription() {
			return getResources().getText(R.string.input_mode_mouse);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.androidVNC.AbstractInputHandler#getName()
		 */
		@Override
		public String getName() {
			return "MOUSE";
		}

	}

	/**
	 * Touch screen controls, clicks the mouse. DPad pans the screen
	 * 
	 * @author Michael A. MacDonald
	 * 
	 */
	class DPadPanTouchMouseMode implements AbstractInputHandler {

		private boolean isPanning;

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.androidVNC.AbstractInputHandler#onKeyDown(int,
		 *      android.view.KeyEvent)
		 */
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent evt) {
			int xv = 0;
			int yv = 0;
			boolean result = true;
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_LEFT:
				xv = -1;
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				xv = 1;
				break;
			case KeyEvent.KEYCODE_DPAD_UP:
				yv = -1;
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				yv = 1;
				break;
			default:
				result = defaultKeyDownHandler(keyCode, evt);
				break;
			}
			if ((xv != 0 || yv != 0) && !isPanning) {
				final int x = xv;
				final int y = yv;
				isPanning = true;
				panner.start(x, y, new Panner.VelocityUpdater() {

					/*
					 * (non-Javadoc)
					 * 
					 * @see android.androidVNC.Panner.VelocityUpdater#updateVelocity(android.graphics.Point,
					 *      long)
					 */
					@Override
					public boolean updateVelocity(PointF p, long interval) {
						double scale = (2.0 * (double) interval / 50.0);
						if (Math.abs(p.x) < 500)
							p.x += (int) (scale * x);
						if (Math.abs(p.y) < 500)
							p.y += (int) (scale * y);
						return true;
					}

				});
				vncCanvas.pan(x, y);
			}
			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.androidVNC.AbstractInputHandler#onKeyUp(int,
		 *      android.view.KeyEvent)
		 */
		@Override
		public boolean onKeyUp(int keyCode, KeyEvent evt) {
			boolean result = false;

			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_LEFT:
			case KeyEvent.KEYCODE_DPAD_RIGHT:
			case KeyEvent.KEYCODE_DPAD_UP:
			case KeyEvent.KEYCODE_DPAD_DOWN:
				panner.stop();
				isPanning = false;
				result = true;
				break;
			default:
				result = defaultKeyUpHandler(keyCode, evt);
				break;
			}
			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.androidVNC.AbstractInputHandler#onTouchEvent(android.view.MotionEvent)
		 */
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			// Mouse Pointer Control Mode
			// Pointer event is absolute coordinates.

			vncCanvas.changeTouchCoordinatesToFullFrame(event);
			if (vncCanvas.processPointerEvent(event, true))
				return true;
			return VncCanvasActivity.super.onTouchEvent(event);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.androidVNC.AbstractInputHandler#onTrackballEvent(android.view.MotionEvent)
		 */
		@Override
		public boolean onTrackballEvent(MotionEvent evt) {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.androidVNC.AbstractInputHandler#handlerDescription()
		 */
		@Override
		public CharSequence getHandlerDescription() {
			return getResources().getText(
					R.string.input_mode_dpad_pan_touchpad_mouse);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.androidVNC.AbstractInputHandler#getName()
		 */
		@Override
		public String getName() {
			return "DPAD_PAN_TOUCH_MOUSE";
		}

	}
}
