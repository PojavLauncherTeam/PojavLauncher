package net.kdt.pojavlaunch;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.system.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.inputmethod.*;
import android.widget.*;
import com.kdt.glsupport.*;
import com.kdt.pointer.*;
import dalvik.system.*;
import java.io.*;
import java.lang.reflect.*;
import java.security.*;
import java.util.*;
import javax.microedition.khronos.egl.*;
import javax.microedition.khronos.opengles.*;
import net.kdt.pojavlaunch.exit.*;
import net.kdt.pojavlaunch.prefs.*;
import net.kdt.pojavlaunch.value.customcontrols.*;
import org.lwjgl.input.*;
import org.lwjgl.opengl.*;

import android.app.AlertDialog;
import com.oracle.dalvik.*;

public class MainActivity extends AppCompatActivity implements OnTouchListener, OnClickListener
{
	public static final String initText = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA  ";

	private static int[] hotbarKeys = {
		Keyboard.KEY_1, Keyboard.KEY_2,	Keyboard.KEY_3,
		Keyboard.KEY_4, Keyboard.KEY_5,	Keyboard.KEY_6,
		Keyboard.KEY_7, Keyboard.KEY_8, Keyboard.KEY_9};

	private boolean rightOverride = false;
	private int scaleFactor = 1;
    private int fingerStillThreshold = 8;
	private int initialX;
    private int initialY;
	private boolean mIsResuming = false;
	private static final int MSG_LEFT_MOUSE_BUTTON_CHECK = 1028;
	private static final int MSG_DROP_ITEM_BUTTON_CHECK = 1029;
	private static boolean triggeredLeftMouseButton = false;
	private Handler theHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_LEFT_MOUSE_BUTTON_CHECK: {
						int x = AndroidDisplay.mouseX;
						int y = AndroidDisplay.mouseY;
						if (AndroidDisplay.grab &&
							Math.abs(initialX - x) < fingerStillThreshold &&
							Math.abs(initialY - y) < fingerStillThreshold) {
							triggeredLeftMouseButton = true;
							sendMouseButton(0, true);
						}
					} break;
				case MSG_DROP_ITEM_BUTTON_CHECK: {
						sendKeyPress(Keyboard.KEY_Q, true);
					} break;
			}
		}
	};
	private MinecraftGLView glSurfaceView;
	private int guiScale;
	private DisplayMetrics displayMetrics;
	public boolean hiddenTextIgnoreUpdate = true;
	public String hiddenTextContents = initText;
	private Button upButton,
	downButton, leftButton,
	rightButton, jumpButton,
	primaryButton, secondaryButton,
	debugButton, shiftButton,
	keyboardButton, inventoryButton,
	talkButton, thirdPersonButton,
	zoomButton, listPlayersButton,
	toggleControlButton;
	private LinearLayout touchPad;
	private ImageView mousePointer;
	//private EditText hiddenEditor;
	// private ViewGroup overlayView;
	private MCProfile.Builder mProfile;

	private DrawerLayout drawerLayout;
    private NavigationView navDrawer;

	private LinearLayout contentLog;
	private TextView textLog, textLogBehindGL;
	private ScrollView contentScroll;
	private ToggleButton toggleLog;
	private GestureDetector gestureDetector;

	private TextView debugText;

	private PointerOreoWrapper pointerSurface;
	private View.OnTouchListener pointerCaptureListener;

	// private String mQueueText = new String();

	private JMinecraftVersionList.Version mVersionInfo;

	private View.OnTouchListener glTouchListener;

	private Button[] controlButtons;

	/*
	 private LinearLayout contentCanvas;
	 private AWTSurfaceView contentCanvasView;
	 */
	private boolean lastEnabled = false;
	private boolean lastGrab = false;
	private boolean isExited = false;
	private boolean isLogAllow = false;
	private int navBarHeight = 40;
	
	private static final int LTYPE_INVOCATION = 0;
	private static final int LTYPE_PROCESS = 1;
	private final int LAUNCH_TYPE = LTYPE_PROCESS;
	// LTYPE_INVOCATION;

	// private static Collection<? extends Provider.Service> rsaPkcs1List;

	private String getStr(int id) {
		return getResources().getString(id);
	}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		try {
			final View decorView = getWindow().getDecorView();
			decorView.setOnSystemUiVisibilityChangeListener (new View.OnSystemUiVisibilityChangeListener() {
				@Override
				public void onSystemUiVisibilityChange(int visibility) {
					if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
						decorView.setSystemUiVisibility(
							View.SYSTEM_UI_FLAG_LAYOUT_STABLE
							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
							| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
					}
				}
			});
			
			ExitManager.setExitTrappedListener(new ExitManager.ExitTrappedListener(){
					@Override
					public void onExitTrapped()
					{
						runOnUiThread(new Runnable(){

								@Override
								public void run() {
									isExited = true;
									
									try {
										SecondaryDexLoader.resetFieldArray(getClassLoader());
									} catch (Throwable th) {
										th.printStackTrace();
									}

									AlertDialog.Builder d = new AlertDialog.Builder(MainActivity.this);
									d.setTitle(R.string.mcn_exit_title);

									try {
										File crashLog = Tools.lastFileModified(Tools.crashPath);
										if(crashLog != null && Tools.read(crashLog.getAbsolutePath()).startsWith("---- Minecraft Crash Report ----")){
											d.setMessage(R.string.mcn_exit_crash);
										} else {
											fullyExit();
											return;
										}
									} catch (Throwable th) {
										d.setMessage(getStr(R.string.mcn_exit_errcrash) + "\n" + Log.getStackTraceString(th));
									}
									d.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){

											@Override
											public void onClick(DialogInterface p1, int p2)
											{
												fullyExit();
											}
										});
									d.setCancelable(false);
									d.show();
								}
							});
					}
				});

			try {
				ExitManager.disableSystemExit();
			} catch (Throwable th) {
				Log.w(Tools.APP_NAME, "Could not disable System.exit() method!", th);
			}

			mProfile = PojavProfile.getCurrentProfileContent(this);
			mVersionInfo = Tools.getVersionInfo(mProfile.getVersion());

			setTitle("Minecraft " + mProfile.getVersion());

			//System.loadLibrary("gl4es");
			/*
			if (mVersionInfo.arguments != null) {
				System.loadLibrary("lwjgl32");
				System.loadLibrary("lwjgl_opengl32");
				System.loadLibrary("lwjgl_stb32");
			}
			*/
			
			if (mVersionInfo.arguments == null) {
				// Minecraft 1.12 and below
				
				// TODO uncomment after fix
				// SecondaryDexLoader.install(getClassLoader(), Arrays.asList(new File[]{new File(Tools.libraries + "/" + Tools.artifactToPath("org.lwjgl", "lwjglboardwalk", "2.9.1"))}), optDir);
			}
			
			this.displayMetrics = Tools.getDisplayMetrics(this);

			AndroidDisplay.windowWidth = displayMetrics.widthPixels / scaleFactor;
			AndroidDisplay.windowHeight = displayMetrics.heightPixels / scaleFactor;
			System.out.println("WidthHeight: " + AndroidDisplay.windowWidth + ":" + AndroidDisplay.windowHeight);

			gestureDetector = new GestureDetector(this, new SingleTapConfirm());

			// Menu
			drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_options);

			navDrawer = (NavigationView) findViewById(R.id.main_navigation_view);
			navDrawer.setNavigationItemSelectedListener(
				new NavigationView.OnNavigationItemSelectedListener() {
					@Override
					public boolean onNavigationItemSelected(MenuItem menuItem) {
						switch (menuItem.getItemId()) {
							case R.id.nav_forceclose: dialogForceClose();
								break;
							case R.id.nav_viewlog: openLogOutput();
								break;
							case R.id.nav_debug: toggleDebug();
								break;
							case R.id.nav_customkey: dialogSendCustomKey();
						}
						//Toast.makeText(MainActivity.this, menuItem.getTitle() + ":" + menuItem.getItemId(), Toast.LENGTH_SHORT).show();

						drawerLayout.closeDrawers();
						return true;
					}
				});

			this.upButton = findButton(R.id.control_up);
			this.downButton = findButton(R.id.control_down);
			this.leftButton = findButton(R.id.control_left);
			this.rightButton = findButton(R.id.control_right);
			this.jumpButton = findButton(R.id.control_jump);
			this.primaryButton = findButton(R.id.control_primary);
			this.secondaryButton = findButton(R.id.control_secondary);
			this.debugButton = findButton(R.id.control_debug);
			this.shiftButton = findButton(R.id.control_shift);
			this.keyboardButton = findButton(R.id.control_keyboard);
			this.inventoryButton = findButton(R.id.control_inventory);
			this.talkButton = findButton(R.id.control_talk);
			this.thirdPersonButton = findButton(R.id.control_thirdperson);
			this.zoomButton = findButton(R.id.control_zoom);
			this.listPlayersButton = findButton(R.id.control_listplayers);
			this.toggleControlButton = findButton(R.id.control_togglecontrol);
			this.controlButtons = new Button[]{
				upButton, downButton, leftButton, rightButton,
				jumpButton, primaryButton, secondaryButton,
				debugButton, shiftButton, keyboardButton,
				inventoryButton, talkButton, thirdPersonButton,
				listPlayersButton
			};
			// this.overlayView = (ViewGroup) findViewById(R.id.main_control_overlay);

			//this.hiddenEditor = findViewById(R.id.hiddenTextbox);

			// Mouse pointer part
			//this.mouseToggleButton = findButton(R.id.control_togglemouse);
			this.touchPad = (LinearLayout) findViewById(R.id.main_touchpad);
			this.mousePointer = (ImageView) findViewById(R.id.main_mouse_pointer);

			this.contentLog = (LinearLayout) findViewById(R.id.content_log_layout);
			this.contentScroll = (ScrollView) findViewById(R.id.content_log_scroll);
			this.textLog = (TextView) contentScroll.getChildAt(0);
			this.toggleLog = (ToggleButton) findViewById(R.id.content_log_toggle_log);
			this.toggleLog.setChecked(false);
			this.textLogBehindGL = (TextView) findViewById(R.id.main_log_behind_GL);
			this.textLogBehindGL.setTypeface(Typeface.MONOSPACE);

			this.textLog.setTypeface(Typeface.MONOSPACE);
			this.toggleLog.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener(){

					@Override
					public void onCheckedChanged(CompoundButton button, boolean isChecked)
					{
						isLogAllow = isChecked;
						appendToLog("");
					}
				});

			this.debugText = (TextView) findViewById(R.id.content_text_debug);

			this.toggleControlButton.setOnClickListener(this);
			this.zoomButton.setVisibility(mVersionInfo.optifineLib == null ? View.GONE : View.VISIBLE);

			this.glSurfaceView = (MinecraftGLView) findViewById(R.id.main_game_render_view);

			ControlButton[] specialButtons = ControlButton.getSpecialButtons();
			specialButtons[0].specialButtonListener = new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					showKeyboard(); 
				}
			};
			specialButtons[1].specialButtonListener = new View.OnClickListener(){

				@Override
				public void onClick(View view)
				{
					MainActivity.this.onClick(toggleControlButton);
				}
			};

			// toggleGui(null);
			onClick(toggleControlButton);
			this.drawerLayout.closeDrawers();

			AndroidLWJGLKeycode.isBackspaceAfterChar = mVersionInfo.minimumLauncherVersion >= 18;

			placeMouseAt(AndroidDisplay.windowWidth / 2, AndroidDisplay.windowHeight / 2);
			new Thread(new Runnable(){

					private boolean isCapturing = false;
					@Override
					public void run()
					{
						while (!isExited) {
							mousePointer.post(new Runnable(){

									@Override
									public void run()
									{
										if (lastGrab && !AndroidDisplay.grab && lastEnabled) {
											touchPad.setVisibility(View.VISIBLE);
											placeMouseAt(AndroidDisplay.windowWidth / 2, AndroidDisplay.windowHeight / 2);
										}

										if (!AndroidDisplay.grab) {
											lastEnabled = touchPad.getVisibility() == View.VISIBLE;
										} else if (touchPad.getVisibility() != View.GONE) {
											touchPad.setVisibility(View.GONE);
										}

										if (isPointerCaptureSupported()) {
											if (!AndroidDisplay.grab && isCapturing) {
												pointerSurface.releaseCapture(); // glSurfaceView.releasePointerCapture();
												isCapturing = false;
											} else if (AndroidDisplay.grab && !isCapturing) {
												glSurfaceView.requestFocus();
												pointerSurface.requestCapture(); // glSurfaceView.requestPointerCapture();
												isCapturing = true;
											}
										}

										lastGrab = AndroidDisplay.grab;
									}
								});

							try {
								Thread.sleep(100);
							} catch (Throwable th) {}
						}
					}
				}).start();

			// ORIGINAL Touch pad
			touchPad.setOnTouchListener(new OnTouchListener(){
					private float prevX, prevY;
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// MotionEvent reports input details from the touch screen
						// and other input controls. In this case, you are only
						// interested in events where the touch position changed.
						// int index = event.getActionIndex();
						int action = event.getActionMasked();

						float x = event.getX();
						float y = event.getY();

						float mouseX = mousePointer.getTranslationX();
						float mouseY = mousePointer.getTranslationY();

						if (gestureDetector.onTouchEvent(event)) {
							AndroidDisplay.putMouseEventWithCoords(rightOverride ? (byte) 1 : (byte) 0, (byte) 1, (int) mouseX, (int) (AndroidDisplay.windowHeight - mouseY), 0, System.nanoTime());
							AndroidDisplay.putMouseEventWithCoords(rightOverride ? (byte) 1 : (byte) 0, (byte) 0, (int) mouseX, (int) (AndroidDisplay.windowHeight - mouseY), 0, System.nanoTime());
							if (!rightOverride) {
								AndroidDisplay.mouseLeft = true;
							}

						} else {
							switch (action) {
								case MotionEvent.ACTION_UP: // 1
								case MotionEvent.ACTION_CANCEL: // 3
								case MotionEvent.ACTION_POINTER_UP: // 6
									if (!rightOverride) {
										AndroidDisplay.mouseLeft = false;
									}
									break;
								case MotionEvent.ACTION_MOVE: // 2
									try {
										mouseX += x - prevX;
										mouseY += y - prevY;
										if (mouseX <= 0) {
											mouseX = 0;
										} else if (mouseX >= AndroidDisplay.windowWidth) {
											mouseX = AndroidDisplay.windowWidth;
										} if (mouseY <= 0) {
											mouseY = 0;
										} else if (mouseY >= AndroidDisplay.windowHeight) {
											mouseY = AndroidDisplay.windowHeight;
										}
									} finally {
										placeMouseAt(mouseX, mouseY);

										AndroidDisplay.mouseX = (int) mouseX;
										AndroidDisplay.mouseY = AndroidDisplay.windowHeight - (int) mouseY;
										break;
									}
							}
						}
						prevX = x;
						prevY = y;
						return true;
					}
				});

			// System.loadLibrary("Regal");

			glSurfaceView.setFocusable(true);
			glSurfaceView.setFocusableInTouchMode(true);
			glSurfaceView.setEGLContextClientVersion(2);

			glTouchListener = new OnTouchListener(){
				private boolean isTouchInHotbar = false;
				private int hotbarX, hotbarY;
				@Override
				public boolean onTouch(View p1, MotionEvent e)
				{
					// System.out.println("Pre touch, isTouchInHotbar=" + Boolean.toString(isTouchInHotbar) + ", action=" + MotionEvent.actionToString(e.getActionMasked()));

					int x = ((int) e.getX()) / scaleFactor;
					int y = (glSurfaceView.getHeight() - ((int) e.getY())) / scaleFactor;
					int hudKeyHandled = handleGuiBar(x, y, e);
					if (!AndroidDisplay.grab && gestureDetector.onTouchEvent(e)) {
						if (hudKeyHandled != -1) {
							sendKeyPress(hudKeyHandled);
						} else {
							AndroidDisplay.putMouseEventWithCoords(rightOverride ? (byte) 1 : (byte) 0, (byte) 1, x, y, 0, System.nanoTime());
							AndroidDisplay.putMouseEventWithCoords(rightOverride ? (byte) 1 : (byte) 0, (byte) 0, x, y, 0, System.nanoTime());
							if (!rightOverride) {
								AndroidDisplay.mouseLeft = true;
							}
						}
					} else {
						switch (e.getActionMasked()) {
							case MotionEvent.ACTION_DOWN: // 0
							case MotionEvent.ACTION_POINTER_DOWN: // 5
								isTouchInHotbar = hudKeyHandled != -1;
								if (isTouchInHotbar) {
									sendKeyPress(hudKeyHandled, true);
									hotbarX = x;
									hotbarY = y;

									theHandler.sendEmptyMessageDelayed(MainActivity.MSG_DROP_ITEM_BUTTON_CHECK, LauncherPreferences.PREF_LONGPRESS_TRIGGER);
								} else {
									AndroidDisplay.mouseX = x;
									AndroidDisplay.mouseY = y;
									if (!rightOverride) {
										AndroidDisplay.mouseLeft = true;
									}

									if (AndroidDisplay.grab) {
										AndroidDisplay.putMouseEventWithCoords(rightOverride ? (byte) 1 : (byte) 0, (byte) 1, x, y, 0, System.nanoTime());
										initialX = x;
										initialY = y;
										theHandler.sendEmptyMessageDelayed(MainActivity.MSG_LEFT_MOUSE_BUTTON_CHECK, LauncherPreferences.PREF_LONGPRESS_TRIGGER);
									}
								}
								break;
							case MotionEvent.ACTION_UP: // 1
							case MotionEvent.ACTION_CANCEL: // 3
							case MotionEvent.ACTION_POINTER_UP: // 6
								if (!isTouchInHotbar) {
									AndroidDisplay.mouseX = x;
									AndroidDisplay.mouseY = y;

									// TODO uncomment after fix wrong trigger
									// AndroidDisplay.putMouseEventWithCoords(rightOverride ? (byte) 1 : (byte) 0, (byte) 0, x, y, 0, System.nanoTime());
									if (!rightOverride) {
										AndroidDisplay.mouseLeft = false;
									}
								} 

								if (AndroidDisplay.grab) {
									// System.out.println((String) ("[Math.abs(" + initialX + " - " + x + ") = " + Math.abs(initialX - x) + "] < " + fingerStillThreshold));
									// System.out.println((String) ("[Math.abs(" + initialY + " - " + y + ") = " + Math.abs(initialY - y) + "] < " + fingerStillThreshold));
									if (isTouchInHotbar && Math.abs(hotbarX - x) < fingerStillThreshold && Math.abs(hotbarY - y) < fingerStillThreshold) {
										sendKeyPress(hudKeyHandled, false);
									} else if (!triggeredLeftMouseButton && Math.abs(initialX - x) < fingerStillThreshold && Math.abs(initialY - y) < fingerStillThreshold) {
										sendMouseButton(1, true);
										sendMouseButton(1, false);
									}
									if (!isTouchInHotbar) {
										if (triggeredLeftMouseButton) {
											sendMouseButton(0, false);
										}
										triggeredLeftMouseButton = false;
										theHandler.removeMessages(MainActivity.MSG_LEFT_MOUSE_BUTTON_CHECK);
									} else {
										sendKeyPress(Keyboard.KEY_Q, false);
										theHandler.removeMessages(MSG_DROP_ITEM_BUTTON_CHECK);
									}
								}
								break;

							default:
								if (!isTouchInHotbar) {
									AndroidDisplay.mouseX = x;
									AndroidDisplay.mouseY = y;
								}
								break;
						}
					}

					return true;
					// return !AndroidDisplay.grab;
				}
			};

			pointerCaptureListener = new OnTouchListener(){
				private int x, y;
				private boolean debugErrored = false;

				private String getMoving(float pos, boolean xOrY) {
					if (pos == 0) {
						return "STOPPED";
					} else if (pos > 0) {
						return xOrY ? "RIGHT" : "DOWN";
					} else { // if (pos3 < 0) {
						return xOrY ? "LEFT" : "UP";
					}
				}

				@Override
				public boolean onTouch(View p1, MotionEvent e)
				{
					x += ((int) e.getX()) / scaleFactor;
					y -= ((int) e.getY()) / scaleFactor;
					
					if (debugText.getVisibility() == View.VISIBLE && !debugErrored) {
						StringBuilder builder = new StringBuilder();
						try {
							builder.append("PointerCapture debug\n");
							builder.append("MotionEvent=" + e.getActionMasked() + "\n");
							builder.append("PressingBtn=" + MotionEvent.class.getDeclaredMethod("buttonStateToString").invoke(null, e.getButtonState()) + "\n\n");

							builder.append("PointerX=" + e.getX() + "\n");
							builder.append("PointerY=" + e.getY() + "\n");
							builder.append("RawX=" + e.getRawX() + "\n");
							builder.append("RawY=" + e.getRawY() + "\n\n");

							builder.append("XPos=" + x + "\n");
							builder.append("YPos=" + y + "\n\n");
							builder.append("MovingX=" + getMoving(e.getX(), true) + "\n");
							builder.append("MovingY=" + getMoving(e.getY(), false) + "\n");
						} catch (Throwable th) {
							debugErrored = true;
							builder.append("Error getting debug. The debug will be stopped!\n" + Log.getStackTraceString(th));
						} finally {
							debugText.setText(builder.toString());
							builder.setLength(0);
						}
					}

					AndroidDisplay.mouseX = x;
					AndroidDisplay.mouseY = y;

					switch (e.getButtonState()) {
						case MotionEvent.BUTTON_PRIMARY: AndroidDisplay.mouseLeft = true;
							break;
						case MotionEvent.BUTTON_SECONDARY: AndroidDisplay.mouseLeft = false;
							break;
					}

					switch (e.getActionMasked()) {
						case MotionEvent.ACTION_DOWN: // 0
						case MotionEvent.ACTION_POINTER_DOWN: // 5
							AndroidDisplay.putMouseEventWithCoords(!AndroidDisplay.mouseLeft /* rightOverride */ ? (byte) 1 : (byte) 0, (byte) 1, x, y, 0, System.nanoTime());
							initialX = x;
							initialY = y;
						
							sendMouseButton(AndroidDisplay.mouseLeft ? 0 : 1, false);
							
							// theHandler.sendEmptyMessageDelayed(MainActivity.MSG_LEFT_MOUSE_BUTTON_CHECK, LauncherPreferences.PREF_LONGPRESS_TRIGGER);
							break;

						case MotionEvent.ACTION_UP: // 1
						case MotionEvent.ACTION_CANCEL: // 3
						case MotionEvent.ACTION_POINTER_UP: // 6
							AndroidDisplay.putMouseEventWithCoords(!AndroidDisplay.mouseLeft /* rightOverride */ ? (byte) 1 : (byte) 0, (byte) 0, x, y, 0, System.nanoTime());
							/*
							 if (!triggeredLeftMouseButton && Math.abs(initialX - x) < fingerStillThreshold && Math.abs(initialY - y) < fingerStillThreshold) {
							 sendMouseButton(1, true);
							 sendMouseButton(1, false);
							 }
							 if (triggeredLeftMouseButton) {
							 sendMouseButton(0, false);
							 }
							 */

							sendMouseButton(AndroidDisplay.mouseLeft ? 0 : 1, true);

							// triggeredLeftMouseButton = false;
							// theHandler.removeMessages(MainActivity.MSG_LEFT_MOUSE_BUTTON_CHECK);
							break;
					}

					return true;
					// If onClick fail with false, change back to true
				}
			};

			if (isPointerCaptureSupported()) {
				this.pointerSurface = new PointerOreoWrapper(glSurfaceView);
				this.pointerSurface.setOnCapturedPointerListener(new PointerOreoWrapper.OnCapturedPointerListener(){
						@Override
						public boolean onCapturedPointer(View view, MotionEvent event) {
							return pointerCaptureListener.onTouch(view, event);
						}
					});
			}

			glSurfaceView.setOnHoverListener(new View.OnHoverListener(){
					@Override
					public boolean onHover(View p1, MotionEvent p2) {
						if (!AndroidDisplay.grab && mIsResuming) {
							return glTouchListener.onTouch(p1, p2);
						}
						return true;
					}
				});
			glSurfaceView.setOnTouchListener(glTouchListener);
			glSurfaceView.setRenderer(new GLTextureView.Renderer() {
					@Override
					public void onSurfaceDestroyed(GL10 gl) {
						Log.d(Tools.APP_NAME, "Surface destroyed.");
					}

					@Override
					public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig p2)
					{
						calculateMcScale();

						EGL10 egl10 = (EGL10) EGLContext.getEGL();
						AndroidContextImplementation.theEgl = egl10;
						AndroidContextImplementation.context = egl10.eglGetCurrentContext();
						AndroidContextImplementation.display = egl10.eglGetCurrentDisplay();
						AndroidContextImplementation.read = egl10.eglGetCurrentSurface(EGL10.EGL_READ);
						AndroidContextImplementation.draw = egl10.eglGetCurrentSurface(EGL10.EGL_DRAW);
						// egl10.eglMakeCurrent(AndroidContextImplementation.display, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
						
						System.out.println(new StringBuffer().append("Gave up context: ").append(AndroidContextImplementation.context).toString());

						AndroidDisplay.windowWidth += navBarHeight;
						
						new Thread(new Runnable(){

								@Override
								public void run() {
									try {
										Thread.sleep(200);
										runCraft();
									} catch (Throwable e) {
										Tools.showError(MainActivity.this, e, true);
									}
								}
							}).start();
					}
					@Override
					public void onDrawFrame(GL10 gl) {
						//mkToast("onDrawFrame");

					}
					@Override
					public void onSurfaceChanged(GL10 gl, int width, int height) {
						AndroidDisplay.windowWidth = width / scaleFactor;
						AndroidDisplay.windowHeight = height / scaleFactor;
					}
				});
			glSurfaceView.setPreserveEGLContextOnPause(true);
			glSurfaceView.setRenderMode(MinecraftGLView.RENDERMODE_CONTINUOUSLY);
			// glSurfaceView.requestRender();
		} catch (Throwable e) {
			e.printStackTrace();
			Tools.showError(this, e, true);
		}

		// Mirror video of OpenGL view.
		/*
		 new Thread(new Runnable(){

		 @Override
		 public void run()
		 {
		 try {
		 while (true) {
		 if (bit == null) continue;
		 runOnUiThread(new Runnable(){

		 @Override
		 public void run()
		 {
		 fillCanvasGL();
		 mirrorView.setImageBitmap(bit);
		 }
		 });

		 // ~33fps render
		 Thread.sleep(30);
		 }
		 } catch (Throwable th) {
		 th.printStackTrace();
		 }
		 }
		 }).start();
		 */
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		AndroidLWJGLKeycode.execKey(this, event, keyCode, false);
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		AndroidLWJGLKeycode.execKey(this, event, keyCode, true);
		return super.onKeyDown(keyCode, event);
	}

	//private Dialog menuDial;

	@Override
	public void onResume() {
		super.onResume();
		mIsResuming = true;
		if (glSurfaceView != null) glSurfaceView.requestRender();
        final int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		/*
		 if (hasFocus && glSurfaceView.getVisibility() == View.GONE) {
		 glSurfaceView.setVisibility(View.VISIBLE);
		 }
		 */
	}

	@Override
	protected void onPause()
	{
		if (AndroidDisplay.grab){
			sendKeyPress(Keyboard.KEY_ESCAPE);
		}
		mIsResuming = false;
		super.onPause();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.control_togglecontrol: {
					/*
					 switch(overlayView.getVisibility()){
					 case View.VISIBLE: overlayView.setVisibility(View.GONE);
					 break;
					 case View.GONE: overlayView.setVisibility(View.VISIBLE);
					 }
					 */

					for (Button button : controlButtons) {
						button.setVisibility(button.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
					}

					zoomButton.setVisibility((zoomButton.getVisibility() == View.GONE && mVersionInfo.optifineLib != null) ? View.VISIBLE : View.GONE);
				}
		}
	}

    public boolean onTouch(View v, MotionEvent e) {
        boolean isDown;
        switch (e.getActionMasked()) {
			case MotionEvent.ACTION_DOWN: // 0
			case MotionEvent.ACTION_POINTER_DOWN: // 5
                isDown = true;
                break;
			case MotionEvent.ACTION_UP: // 1
			case MotionEvent.ACTION_CANCEL: // 3
			case MotionEvent.ACTION_POINTER_UP: // 6
                isDown = false;
                break;
            default:
                return false;
        }

		switch (v.getId()) {
			case R.id.control_up: sendKeyPress(Keyboard.KEY_W, isDown); break;
			case R.id.control_left: sendKeyPress(Keyboard.KEY_A, isDown); break;
			case R.id.control_down: sendKeyPress(Keyboard.KEY_S, isDown); break;
			case R.id.control_right: sendKeyPress(Keyboard.KEY_D, isDown); break;
			case R.id.control_jump: sendKeyPress(Keyboard.KEY_SPACE, isDown); break;
			case R.id.control_primary: sendMouseButton(0, isDown); break;
			case R.id.control_secondary:
				if (AndroidDisplay.grab) {
					sendMouseButton(1, isDown);
				} else {
					if (!isDown) {
						AndroidDisplay.putMouseEventWithCoords(/* right mouse */ (byte) 1, (byte) 0, AndroidDisplay.mouseX, AndroidDisplay.mouseY, 0, System.nanoTime());
						AndroidDisplay.putMouseEventWithCoords(/* right mouse */ (byte) 1, (byte) 1, AndroidDisplay.mouseX, AndroidDisplay.mouseY, 0, System.nanoTime());
					}
					setRightOverride(isDown);
				} break;
			case R.id.control_debug: sendKeyPress(Keyboard.KEY_F3, isDown); break;
			case R.id.control_shift: sendKeyPress(Keyboard.KEY_LSHIFT, isDown); break;
			case R.id.control_inventory: sendKeyPress(Keyboard.KEY_E, isDown); break;
			case R.id.control_talk: sendKeyPress(Keyboard.KEY_T, isDown); break;
			case R.id.control_keyboard: showKeyboard(); break;
			case R.id.control_thirdperson: sendKeyPress(Keyboard.KEY_F5, isDown); break;
			case R.id.control_zoom: sendKeyPress(Keyboard.KEY_C, isDown); break;
			case R.id.control_listplayers: sendKeyPress(Keyboard.KEY_TAB, isDown); break;
		}

        return false;
    }

	public static void fullyExit() {
		if (!ExitManager.isExiting()) {
			ExitManager.enableSystemExit();
			System.exit(0);
		}
		ExitManager.stopExitLoop();
	}

    public void forceUserHome(String s) throws Exception {
        Properties props = System.getProperties();
        Class clazz = props.getClass();
        Field f = null;
        while (clazz != null) {
            try {
                f = clazz.getDeclaredField("defaults");
                break;
            } catch (Exception e) {
                clazz = clazz.getSuperclass();
            }
        }
        if (f != null) {
            f.setAccessible(true);
            ((Properties) f.get(props)).put("user.home", s);
        }
    }

	private boolean isPointerCaptureSupported() {
		return Build.VERSION.SDK_INT >= 26; 
	}

	private String[] getMCArgs()
	{
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
			// Support Minecraft 1.13+
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

	private ShellProcessOperation mLaunchShell;
	private void setEnvironment(String name, String value) throws ErrnoException, IOException {
		if (LAUNCH_TYPE == LTYPE_PROCESS) {
			mLaunchShell.writeToProcess("export " + name + "=" + value);
		} else {
			Os.setenv(name, value, true);
		}
	}

	private void runCraft() throws Throwable {
		String[] launchArgs = getMCArgs();

		List<String> javaArgList = new ArrayList<String>();
		
		if (LAUNCH_TYPE == LTYPE_PROCESS) javaArgList.add(Tools.homeJreDir + "/bin/java");
		else javaArgList.add("java");
		
		// javaArgList.add("-Xms512m");
		javaArgList.add("-Xmx512m");
		
		javaArgList.add("-Djava.library.path=" +
			// TODO lwjgl2 vs lwjgl3 native path
			getApplicationInfo().nativeLibraryDir
		);
		
		// javaArgList.add("-Dorg.lwjgl.system.jemalloc.libname=libjemalloc.so");
		javaArgList.add("-Dorg.lwjgl.opengl.libname=libgl04es.so");
			
		// Enable LWJGL3 debug
		javaArgList.add("-Dorg.lwjgl.util.Debug=true");
		javaArgList.add("-Dorg.lwjgl.util.DebugFunctions=true");
		javaArgList.add("-Dorg.lwjgl.util.DebugLoader=true");
		
		// GLFW Stub width height
		javaArgList.add("-Dglfwstub.windowWidth=" + AndroidDisplay.windowWidth);
		javaArgList.add("-Dglfwstub.windowHeight=" + AndroidDisplay.windowHeight);

		
		javaArgList.add("-Dglfwstub.eglContext=" + Tools.getEGLAddress("Context", AndroidContextImplementation.context));
		
		String eglDisplay = Tools.getEGLAddress("Display", AndroidContextImplementation.display);
		if (eglDisplay.equals("1")) {
			eglDisplay = Tools.getEGLAddress("Display", ((EGL10) EGLContext.getEGL()).eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY));
		}
		javaArgList.add("-Dglfwstub.eglDisplay=" + eglDisplay);
		
		javaArgList.add("-Dglfwstub.eglSurfaceRead=" + Tools.getEGLAddress("Surface", AndroidContextImplementation.read));
		javaArgList.add("-Dglfwstub.eglSurfaceDraw=" + Tools.getEGLAddress("Surface", AndroidContextImplementation.draw));
		
		if (mVersionInfo.arguments != null) {
			// Minecraft 1.13+

			javaArgList.add("-Dminecraft.launcher.brand=" + Tools.APP_NAME);
			javaArgList.add("-Dminecraft.launcher.version=" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		}
		
		javaArgList.add("-cp");
		javaArgList.add(Tools.generateLaunchClassPath(mProfile.getVersion()));
		javaArgList.add(mVersionInfo.mainClass);
		javaArgList.addAll(Arrays.asList(launchArgs));

		if (LAUNCH_TYPE == LTYPE_PROCESS) {
			mLaunchShell = new ShellProcessOperation(new ShellProcessOperation.OnPrintListener(){

					@Override
					public void onPrintLine(String text) {
						appendToLog(text, false);
					}
				});
			mLaunchShell.initInputStream(this);
		}
		
		setEnvironment("JAVA_HOME", Tools.homeJreDir);
		setEnvironment("HOME", Tools.MAIN_PATH);
		setEnvironment("TMPDIR",  getCacheDir().getAbsolutePath());
		setEnvironment("LIBGL_MIPMAP", "3");
		setEnvironment("LD_LIBRARY_PATH", "$JAVA_HOME/lib:$JAVA_HOME/lib/jli:$JAVA_HOME/lib/server");
		
		if (LAUNCH_TYPE == LTYPE_PROCESS) {
			mLaunchShell.writeToProcess("cd $HOME");

			mLaunchShell.writeToProcess(javaArgList.toArray(new String[0]));
			int exitCode = mLaunchShell.waitFor();
			if (exitCode != 0) {
				Tools.showError(this, new ErrnoException("java", exitCode), false);
			}
		} else { // Type Invocation
			final FileDescriptor logFile = BinaryExecutor.redirectStdio();
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						BufferedReader bis = new BufferedReader(new FileReader(logFile));
						String currLine;
						while ((currLine = bis.readLine()) != null) {
							appendlnToLog(currLine);
						}
						bis.close();
					} catch (Throwable th) {
						appendlnToLog("Can't get log:\n" + Log.getStackTraceString(th));
					}
				}
			}, "RuntimeLogThread").start();
			
			BinaryExecutor.initJavaRuntime();
			BinaryExecutor.chdir(Tools.MAIN_PATH);
			
			VMLauncher.launchJVM(javaArgList.toArray(new String[0]));
		}
	}
	
	public void printStream(InputStream stream) {
		try {
			BufferedReader buffStream = new BufferedReader(new InputStreamReader(stream));
			String line = null;
			while ((line = buffStream.readLine()) != null) {
				appendlnToLog(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String fromArray(List<String> arr) {
		String s = "";
		for (String exec : arr) {
			s = s + " " + exec;
		}
		return s;
	}

	private void toggleDebug() {
		debugText.setVisibility(debugText.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
	}

	private void dialogSendCustomKey() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle(R.string.control_customkey);
		dialog.setItems(AndroidLWJGLKeycode.generateKeyName(), new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dInterface, int position) {
					AndroidLWJGLKeycode.execKeyIndex(MainActivity.this, position);
				}
			});
		dialog.show();
	}

	private void openLogOutput() {
		contentLog.setVisibility(View.VISIBLE);
		mIsResuming = false;
	}

	public void closeLogOutput(View view) {
		contentLog.setVisibility(View.GONE);
		mIsResuming = true;
	}
	/*
	 private void openCanvasOutput() {
	 WindowAnimation.fadeIn(contentCanvas, 500);
	 }

	 public void closeCanvasOutput(View view) {
	 WindowAnimation.fadeOut(contentCanvas, 500);
	 }
	 */
	private void appendToLog(String text) {
		appendToLog(text, true);
	}
	
	private void appendlnToLog(String text) {
		appendlnToLog(text, true);
	}
	
	private void appendlnToLog(String text, boolean checkAllow) {
		appendToLog(text + "\n", checkAllow);
	}

	private void appendToLog(final String text, boolean checkAllow) {
		if (checkAllow && !isLogAllow) return;
		textLog.post(new Runnable(){
				@Override
				public void run() {
					textLog.append(text);
					contentScroll.fullScroll(ScrollView.FOCUS_DOWN);
				}
			});
	}

	public void handleMessage(Message msg) {
		switch (msg.what) {
			case MainActivity.MSG_LEFT_MOUSE_BUTTON_CHECK /*1028*/:
				int x = AndroidDisplay.mouseX;
				int y = AndroidDisplay.mouseY;
				if (AndroidDisplay.grab && Math.abs(initialX - x) < fingerStillThreshold && Math.abs(initialY - y) < fingerStillThreshold) {
					triggeredLeftMouseButton = true;
					sendMouseButton(0, true);
					return;
				}
				return;
			default:
				return;
		}
	}

	public String getMinecraftOption(String key) {
		try {
			String[] options = Tools.read(Tools.MAIN_PATH + "/options.txt").split("\n");
			for (String option : options) {
				String[] optionKeyValue = option.split(":");
				if (optionKeyValue[0].equals(key)) {
					return optionKeyValue[1];
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public int mcscale(int input) {
        return this.guiScale * input;
    }

	/*
	 public int randomInRange(int min, int max) {
	 return min + (int)(Math.random() * (max - min + 1));
	 }
	 */

	public void toggleMenu(View v) {
		drawerLayout.openDrawer(Gravity.RIGHT);
	}

	public void placeMouseAdd(float x, float y) {
		this.mousePointer.setTranslationX(mousePointer.getTranslationX() + x);
		this.mousePointer.setTranslationY(mousePointer.getTranslationY() + y);
	}

	public void placeMouseAt(float x, float y) {
		this.mousePointer.setTranslationX(x);
		this.mousePointer.setTranslationY(y);
	}

	public void toggleMouse(View view) {
		if (AndroidDisplay.grab) return;

		boolean isVis = touchPad.getVisibility() == View.VISIBLE;
		touchPad.setVisibility(isVis ? View.GONE : View.VISIBLE);
		((Button) view).setText(isVis ? R.string.control_mouseoff: R.string.control_mouseon);
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
					try {
						fullyExit();
					} catch (Throwable th) {
						Log.w(Tools.APP_NAME, "Could not enable System.exit() method!", th);
					}

					// If we are unable to enable exit, use method: kill myself.
					// android.os.Process.killProcess(android.os.Process.myPid());

					// Toast.makeText(MainActivity.this, "Could not exit. Please force close this app.", Toast.LENGTH_LONG).show();
				}
			})
			.show();
	}

	private Button findButton(int id) {
        Button button = (Button) findViewById(id);
		button.setWidth((int) Tools.dpToPx(this, Tools.pxToDp(this, button.getWidth()) * PojavPreferenceActivity.PREF_BUTTONSIZE));
		button.setHeight((int) Tools.dpToPx(this, Tools.pxToDp(this, button.getHeight()) * PojavPreferenceActivity.PREF_BUTTONSIZE));
        button.setOnTouchListener(this);
        return button;
    }

	@Override
	public void onBackPressed() {
		// Prevent back
	}

	public void hideKeyboard() {
		try {
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this).getCurrentFocus().getWindowToken(), 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showKeyboard() {
		((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
		glSurfaceView.requestFocus();
	}

	private void setRightOverride(boolean val) {
        this.rightOverride = val;
        // this.secondaryButton.setBackgroundDrawable(this.rightOverride ? this.secondaryButtonColorBackground : this.secondaryButtonDefaultBackground);
    }

	public void sendKeyPress(int keyCode, boolean status) {
        sendKeyPress(keyCode, '\u0000', status);
    }

    public void sendKeyPress(int keyCode, char keyChar, boolean status) {
        AndroidDisplay.setKey(keyCode, keyChar, status);
    }

	public void sendKeyPress(char keyChar) {
		sendKeyPress(0, keyChar, true);
		sendKeyPress(0, keyChar, false);
	}

	public void sendKeyPress(int keyCode) {
		sendKeyPress(keyCode, true);
		sendKeyPress(keyCode, false);
	}

	public void sendMouseButton(int button, boolean status) {
        AndroidDisplay.setMouseButtonInGrabMode((byte) button, status ? (byte) 1 : (byte) 0);
    }

	public void calculateMcScale() {
        int scale = 1;
        int screenWidth = AndroidDisplay.windowWidth;
        int screenHeight = AndroidDisplay.windowHeight;
        while (screenWidth / (scale + 1) >= 320 && screenHeight / (scale + 1) >= 240) {
            scale++;
        }
        this.guiScale = scale;
    }

	public int handleGuiBar(int x, int y, MotionEvent e) {
        if (!AndroidDisplay.grab) {
            return -1;
        }

        int screenWidth = AndroidDisplay.windowWidth;
        int screenHeight = AndroidDisplay.windowHeight;
        int barheight = mcscale(20);
        int barwidth = mcscale(180);
        int barx = (screenWidth / 2) - (barwidth / 2);
        if (x < barx || x >= barx + barwidth || y < 0 || y >= 0 + barheight) {
            return -1;
        }
        return hotbarKeys[((x - barx) / mcscale(20)) % 9];
    }
}
