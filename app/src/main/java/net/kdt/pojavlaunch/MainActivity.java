package net.kdt.pojavlaunch;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.widget.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.inputmethod.*;
import android.widget.*;
import com.kdt.pointer.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import net.kdt.pojavlaunch.customcontrols.*;
import net.kdt.pojavlaunch.prefs.*;
import net.kdt.pojavlaunch.utils.*;
import org.lwjgl.glfw.*;

public class MainActivity extends LoggableActivity implements OnTouchListener, OnClickListener
{
    public static volatile ClipboardManager GLOBAL_CLIPBOARD;
    
	public static final String initText = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA  ";
    volatile public static boolean isInputStackCall;

	private static int[] hotbarKeys = {
		LWJGLGLFWKeycode.GLFW_KEY_1, LWJGLGLFWKeycode.GLFW_KEY_2,	LWJGLGLFWKeycode.GLFW_KEY_3,
		LWJGLGLFWKeycode.GLFW_KEY_4, LWJGLGLFWKeycode.GLFW_KEY_5,	LWJGLGLFWKeycode.GLFW_KEY_6,
		LWJGLGLFWKeycode.GLFW_KEY_7, LWJGLGLFWKeycode.GLFW_KEY_8, LWJGLGLFWKeycode.GLFW_KEY_9};

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
						int x = CallbackBridge.mouseX;
						int y = CallbackBridge.mouseY;
						if (CallbackBridge.isGrabbing() &&
							Math.abs(initialX - x) < fingerStillThreshold &&
							Math.abs(initialY - y) < fingerStillThreshold) {
							triggeredLeftMouseButton = true;
							sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT, true);
						}
					} break;
				case MSG_DROP_ITEM_BUTTON_CHECK: {
						sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_Q, 0, true);
					} break;
			}
		}
	};
	private MinecraftGLView minecraftGLView;
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
	private TextView textLog;
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

	private File logFile;
	private PrintStream logStream;
	
	/*
	 private LinearLayout contentCanvas;
	 private AWTSurfaceView contentCanvasView;
	 */
	private boolean lastEnabled = false;
	private boolean lastGrab = false;
	private boolean isExited = false;
	private boolean isLogAllow = false;
	// private int navBarHeight = 40;
	
	// private static Collection<? extends Provider.Service> rsaPkcs1List;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		try {
            // FIXME: is it safe fot multi thread?
            GLOBAL_CLIPBOARD = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            
			logFile = new File(Tools.MAIN_PATH, "latestlog.txt");
			logFile.delete();
			logFile.createNewFile();
			logStream = new PrintStream(logFile.getAbsolutePath());
			
			Tools.setFullscreen(this);
			
			mProfile = PojavProfile.getCurrentProfileContent(this);
			mVersionInfo = Tools.getVersionInfo(mProfile.getVersion());
            // Minecraft 1.12.x special case: use indirect char pipe
            CallbackBridge.isMinecraft1p12 = mVersionInfo.assets.startsWith("1.12");
            
			setTitle("Minecraft " + mProfile.getVersion());
            
            // Minecraft 1.13+
            isInputStackCall = mVersionInfo.arguments != null;
            
			this.displayMetrics = Tools.getDisplayMetrics(this);
			CallbackBridge.windowWidth = displayMetrics.widthPixels / scaleFactor;
			CallbackBridge.windowHeight = displayMetrics.heightPixels / scaleFactor;
			System.out.println("WidthHeight: " + CallbackBridge.windowWidth + ":" + CallbackBridge.windowHeight);

			gestureDetector = new GestureDetector(this, new SingleTapConfirm());

			// Menu
			drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_options);

			navDrawer = findViewById(R.id.main_navigation_view);
			navDrawer.setNavigationItemSelectedListener(
				new NavigationView.OnNavigationItemSelectedListener() {
					@Override
					public boolean onNavigationItemSelected(MenuItem menuItem) {
						switch (menuItem.getItemId()) {
							case R.id.nav_forceclose: dialogForceClose(MainActivity.this);
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
			// this.textLogBehindGL = (TextView) findViewById(R.id.main_log_behind_GL);
			// this.textLogBehindGL.setTypeface(Typeface.MONOSPACE);

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

			this.minecraftGLView = (MinecraftGLView) findViewById(R.id.main_game_render_view);

			ControlButton[] specialButtons = ControlButton.getSpecialButtons();
			specialButtons[0].specialButtonListener = new View.OnClickListener(){
				@Override
				public void onClick(View view) {
					showKeyboard(); 
				}
			};
			specialButtons[1].specialButtonListener = new View.OnClickListener(){
				@Override
				public void onClick(View view) {
					MainActivity.this.onClick(toggleControlButton);
				}
			};

			// toggleGui(null);
			onClick(toggleControlButton);
			this.drawerLayout.closeDrawers();

			AndroidLWJGLKeycode.isBackspaceAfterChar = mVersionInfo.minimumLauncherVersion >= 18;

			placeMouseAt(CallbackBridge.windowWidth / 2, CallbackBridge.windowHeight / 2);
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
										if (lastGrab && !CallbackBridge.isGrabbing() && lastEnabled) {
											touchPad.setVisibility(View.VISIBLE);
											placeMouseAt(CallbackBridge.windowWidth / 2, CallbackBridge.windowHeight / 2);
										}

										if (!CallbackBridge.isGrabbing()) {
											lastEnabled = touchPad.getVisibility() == View.VISIBLE;
										} else if (touchPad.getVisibility() != View.GONE) {
											touchPad.setVisibility(View.GONE);
										}

										if (isPointerCaptureSupported()) {
											if (!CallbackBridge.isGrabbing() && isCapturing) {
												pointerSurface.releaseCapture(); // minecraftGLView.releasePointerCapture();
												isCapturing = false;
											} else if (CallbackBridge.isGrabbing() && !isCapturing) {
												minecraftGLView.requestFocus();
												pointerSurface.requestCapture(); // minecraftGLView.requestPointerCapture();
												isCapturing = true;
											}
										}

										lastGrab = CallbackBridge.isGrabbing();
									}
								});

							try {
								Thread.sleep(100);
							} catch (Throwable th) {}
						}
					}
				}).start();

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

                            CallbackBridge.sendCursorPos((int) mouseX, (int) mouseY);
                            CallbackBridge.sendMouseKeycode(rightOverride ? LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT : LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT);
							if (!rightOverride) {
								CallbackBridge.mouseLeft = true;
							}

						} else {
							switch (action) {
								case MotionEvent.ACTION_UP: // 1
								case MotionEvent.ACTION_CANCEL: // 3
								case MotionEvent.ACTION_POINTER_UP: // 6
									if (!rightOverride) {
										CallbackBridge.mouseLeft = false;
									}
									break;
								case MotionEvent.ACTION_MOVE: // 2
									mouseX = Math.max(0, Math.min(CallbackBridge.windowWidth, mouseX + x - prevX));
                                    mouseY = Math.max(0, Math.min(CallbackBridge.windowHeight, mouseY + y - prevY));
									placeMouseAt(mouseX, mouseY);

									CallbackBridge.sendCursorPos((int) mouseX, (int) mouseY);
                                    CallbackBridge.sendMouseKeycode(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT, 0, isLeftMouseDown);
                                    CallbackBridge.sendMouseKeycode(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, 0, isRightMouseDown);
									break;
							}
						}
						prevX = x;
						prevY = y;
                        
                        debugText.setText(CallbackBridge.DEBUG_STRING.toString());
                        CallbackBridge.DEBUG_STRING.setLength(0);
                        
						return true;
					}
				});

			// System.loadLibrary("Regal");

			minecraftGLView.setFocusable(true);
			minecraftGLView.setFocusableInTouchMode(true);
			// minecraftGLView.setEGLContextClientVersion(2);

            glTouchListener = new OnTouchListener(){
                private boolean isTouchInHotbar = false;
                private int hotbarX, hotbarY;
                private int scrollInitialX, scrollInitialY;
                @Override
                public boolean onTouch(View p1, MotionEvent e)
                {
                    // System.out.println("Pre touch, isTouchInHotbar=" + Boolean.toString(isTouchInHotbar) + ", action=" + MotionEvent.actionToString(e.getActionMasked()));
                    int x = ((int) e.getX()) / scaleFactor;
                    int y = ((int) e.getY()) / scaleFactor;
                    int hudKeyHandled = handleGuiBar(x, y);
                    if (!CallbackBridge.isGrabbing() && gestureDetector.onTouchEvent(e)) {
                        if (hudKeyHandled != -1) {
                            sendKeyPress(hudKeyHandled);
                        } else {
                            CallbackBridge.putMouseEventWithCoords(rightOverride ? (byte) 1 : (byte) 0, x, y);
                            if (!rightOverride) {
                                CallbackBridge.mouseLeft = true;
                            }
                        }
                    } else {
                        switch (e.getActionMasked()) {
                            case MotionEvent.ACTION_DOWN: // 0
                            case MotionEvent.ACTION_POINTER_DOWN: // 5
                                CallbackBridge.sendPrepareGrabInitialPos();
                                
                                isTouchInHotbar = hudKeyHandled != -1;
                                if (isTouchInHotbar) {
                                    sendKeyPress(hudKeyHandled, 0, true);
                                    hotbarX = x;
                                    hotbarY = y;

                                    theHandler.sendEmptyMessageDelayed(MainActivity.MSG_DROP_ITEM_BUTTON_CHECK, LauncherPreferences.PREF_LONGPRESS_TRIGGER);
                                } else {
                                    CallbackBridge.mouseX = x;
                                    CallbackBridge.mouseY = y;
                                    CallbackBridge.sendCursorPos(x, y);
                                    if (!rightOverride) {
                                        CallbackBridge.mouseLeft = true;
                                    }

                                    if (CallbackBridge.isGrabbing()) {
                                        // It cause hold left mouse while moving camera
                                        // CallbackBridge.putMouseEventWithCoords(rightOverride ? (byte) 1 : (byte) 0, (byte) 1, x, y);
                                        initialX = x;
                                        initialY = y;
                                        theHandler.sendEmptyMessageDelayed(MainActivity.MSG_LEFT_MOUSE_BUTTON_CHECK, LauncherPreferences.PREF_LONGPRESS_TRIGGER);
                                    }
                                   
                                    scrollInitialX = x;
                                    scrollInitialY = y;
                                }
                                break;
                                
                            case MotionEvent.ACTION_UP: // 1
                            case MotionEvent.ACTION_POINTER_UP: // 6
                            case MotionEvent.ACTION_CANCEL: // 3
                                if (!isTouchInHotbar) {
                                    CallbackBridge.mouseX = x;
                                    CallbackBridge.mouseY = y;
                                    
                                    // -TODO uncomment after fix wrong trigger
                                    // CallbackBridge.putMouseEventWithCoords(rightOverride ? (byte) 1 : (byte) 0, (byte) 0, x, y);
                                    CallbackBridge.sendCursorPos(x, y);
                                    if (!rightOverride) {
                                        CallbackBridge.mouseLeft = false;
                                    }
                                } 

                                if (CallbackBridge.isGrabbing()) {
                                    // System.out.println((String) ("[Math.abs(" + initialX + " - " + x + ") = " + Math.abs(initialX - x) + "] < " + fingerStillThreshold));
                                    // System.out.println((String) ("[Math.abs(" + initialY + " - " + y + ") = " + Math.abs(initialY - y) + "] < " + fingerStillThreshold));
                                    if (isTouchInHotbar && Math.abs(hotbarX - x) < fingerStillThreshold && Math.abs(hotbarY - y) < fingerStillThreshold) {
                                        sendKeyPress(hudKeyHandled, 0, false);
                                    } else if (!triggeredLeftMouseButton && Math.abs(initialX - x) < fingerStillThreshold && Math.abs(initialY - y) < fingerStillThreshold) {
                                        sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, true);
                                        sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, false);
                                    }
                                    if (!isTouchInHotbar) {
                                        if (triggeredLeftMouseButton) {
                                            sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT, false);
                                        }
                                        triggeredLeftMouseButton = false;
                                        theHandler.removeMessages(MainActivity.MSG_LEFT_MOUSE_BUTTON_CHECK);
                                    } else {
                                        sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_Q, 0, false);
                                        theHandler.removeMessages(MSG_DROP_ITEM_BUTTON_CHECK);
                                    }
                                }
                                
                                break;
/*
                            case MotionEvent.ACTION_POINTER_DOWN: // 5
                                CallbackBridge.sendScroll(x - scrollInitialX, y - scrollInitialY);
                                scrollInitialX = x;
                                scrollInitialY = y;
                                break;
                                
                            case MotionEvent.ACTION_POINTER_UP: // 6
                                scrollInitialX = x;
                                scrollInitialY = y;
                                break;
 */
                            case MotionEvent.ACTION_MOVE:
                                if (!isTouchInHotbar) {
                                    CallbackBridge.mouseX = x;
                                    CallbackBridge.mouseY = y;
                                    
                                    CallbackBridge.sendCursorPos(x, y);
                                    CallbackBridge.sendMouseKeycode(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT, 0, isLeftMouseDown);
                                    CallbackBridge.sendMouseKeycode(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, 0, isRightMouseDown);
                                    
                                    CallbackBridge.sendScroll(x - scrollInitialX, y - scrollInitialY);
                                }
                                break;
                        }
                    }
                    
/*
					int x = ((int) e.getX()) / scaleFactor;
					int y = (minecraftGLView.getHeight() - ((int) e.getY())) / scaleFactor;
					int hudKeyHandled = handleGuiBar(x, y, e);
					if (!CallbackBridge.isGrabbing() && gestureDetector.onTouchEvent(e)) {
						if (hudKeyHandled != -1) {
							sendKeyPress(hudKeyHandled);
						} else {
                            CallbackBridge.sendMouseEvent(
                                x, CallbackBridge.windowHeight - y,
                                rightOverride ? LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT : LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT
                            );
							if (!rightOverride) {
								CallbackBridge.mouseLeft = true;
							}
						}
					} else {
						switch (e.getActionMasked()) {
							case MotionEvent.ACTION_DOWN: // 0
							case MotionEvent.ACTION_POINTER_DOWN: // 5
								isTouchInHotbar = hudKeyHandled != -1;
								if (isTouchInHotbar) {
									sendKeyPress(hudKeyHandled, 0, true);
									hotbarX = x;
									hotbarY = y;

									theHandler.sendEmptyMessageDelayed(MainActivity.MSG_DROP_ITEM_BUTTON_CHECK, LauncherPreferences.PREF_LONGPRESS_TRIGGER);
								} else {
									CallbackBridge.sendCursorPos(x, CallbackBridge.windowHeight - y);
                                    
									// if (!rightOverride)
										// CallbackBridge.mouseLeft = true;
									
                                    

									if (CallbackBridge.isGrabbing()) {
										CallbackBridge.sendMouseKeycode(rightOverride ? LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT : LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT, 0, true);
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
                                    CallbackBridge.sendCursorPos(x, CallbackBridge.windowHeight - y);

									// TODO uncomment after fix wrong trigger
									// CallbackBridge.putMouseEventWithCoords(rightOverride ? (byte) 1 : (byte) 0, (byte) 0, x, y);
									if (!rightOverride) {
										// CallbackBridge.mouseLeft = false;
									}
								} 

								if (CallbackBridge.isGrabbing()) {
									// System.out.println((String) ("[Math.abs(" + initialX + " - " + x + ") = " + Math.abs(initialX - x) + "] < " + fingerStillThreshold));
									// System.out.println((String) ("[Math.abs(" + initialY + " - " + y + ") = " + Math.abs(initialY - y) + "] < " + fingerStillThreshold));
									if (isTouchInHotbar && Math.abs(hotbarX - x) < fingerStillThreshold && Math.abs(hotbarY - y) < fingerStillThreshold) {
										sendKeyPress(hudKeyHandled, 0, false);
									} else if (!triggeredLeftMouseButton && Math.abs(initialX - x) < fingerStillThreshold && Math.abs(initialY - y) < fingerStillThreshold) {
										sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, true);
										sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, false);
									}
									if (!isTouchInHotbar) {
										if (triggeredLeftMouseButton) {
											sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT, false);
										}
										triggeredLeftMouseButton = false;
										theHandler.removeMessages(MainActivity.MSG_LEFT_MOUSE_BUTTON_CHECK);
									} else {
										sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_Q, 0, false);
										theHandler.removeMessages(MSG_DROP_ITEM_BUTTON_CHECK);
									}
								}
								break;

							default:
								if (!isTouchInHotbar) {
									CallbackBridge.sendCursorPos(x, CallbackBridge.windowHeight - y);
								}
								break;
                                
						}
					}
                    */
                    
                    debugText.setText(CallbackBridge.DEBUG_STRING.toString());
                    CallbackBridge.DEBUG_STRING.setLength(0);

					return true;
					// return !CallbackBridge.isGrabbing();
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

                    CallbackBridge.sendCursorPos(x, y);
                    
					switch (e.getButtonState()) {
						case MotionEvent.BUTTON_PRIMARY: CallbackBridge.mouseLeft = true;
							break;
						case MotionEvent.BUTTON_SECONDARY: CallbackBridge.mouseLeft = false;
							break;
					}

					switch (e.getActionMasked()) {
						case MotionEvent.ACTION_DOWN: // 0
						case MotionEvent.ACTION_POINTER_DOWN: // 5
                            CallbackBridge.sendPrepareGrabInitialPos();
                            
                            CallbackBridge.sendMouseKeycode(CallbackBridge.mouseLeft ? LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT : LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, 0, true);
							initialX = x;
							initialY = y;
						
							sendMouseButton(CallbackBridge.mouseLeft ? LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT : LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, false);
							
							// theHandler.sendEmptyMessageDelayed(MainActivity.MSG_LEFT_MOUSE_BUTTON_CHECK, LauncherPreferences.PREF_LONGPRESS_TRIGGER);
							break;

						case MotionEvent.ACTION_UP: // 1
						case MotionEvent.ACTION_CANCEL: // 3
						case MotionEvent.ACTION_POINTER_UP: // 6
                            // CallbackBridge.sendCursorPos(x, y);
                            // CallbackBridge.sendMouseKeycode(rightOverride ? LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT : LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT, 0, true);
							CallbackBridge.putMouseEventWithCoords(CallbackBridge.mouseLeft /* rightOverride */ ? (byte) 0 : (byte) 1, (byte) 1, x, y);
							/*
							 if (!triggeredLeftMouseButton && Math.abs(initialX - x) < fingerStillThreshold && Math.abs(initialY - y) < fingerStillThreshold) {
							 sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, true);
							 sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, false);
							 }
							 if (triggeredLeftMouseButton) {
							 sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT, false);
							 }
							 */

							sendMouseButton(CallbackBridge.mouseLeft ? LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT : LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, true);

							// triggeredLeftMouseButton = false;
							// theHandler.removeMessages(MainActivity.MSG_LEFT_MOUSE_BUTTON_CHECK);
							break;
					}

                    debugText.setText(CallbackBridge.DEBUG_STRING.toString());
                    CallbackBridge.DEBUG_STRING.setLength(0);
                    
					return true;
					// If onClick fail with false, change back to true
				}
			};

			if (isPointerCaptureSupported()) {
				this.pointerSurface = new PointerOreoWrapper(minecraftGLView);
				this.pointerSurface.setOnCapturedPointerListener(new PointerOreoWrapper.OnCapturedPointerListener(){
						@Override
						public boolean onCapturedPointer(View view, MotionEvent event) {
							return pointerCaptureListener.onTouch(view, event);
						}
					});
			}

			minecraftGLView.setOnHoverListener(new View.OnHoverListener(){
					@Override
					public boolean onHover(View p1, MotionEvent p2) {
						if (!CallbackBridge.isGrabbing() && mIsResuming) {
							return glTouchListener.onTouch(p1, p2);
						}
						return true;
					}
				});
			minecraftGLView.setOnTouchListener(glTouchListener);
            
			minecraftGLView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener(){
				
					private boolean isCalled = false;
					@Override
					public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
						CallbackBridge.windowWidth = width;
						CallbackBridge.windowHeight = height;
						calculateMcScale();
						
						// Should we do that?
						if (!isCalled) {
							isCalled = true;
							
							JREUtils.setupBridgeWindow(new Surface(texture));
							
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
					}

					@Override
					public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
						return true;
					}

					@Override
					public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
						CallbackBridge.windowWidth = width;
						CallbackBridge.windowHeight = height;
                        CallbackBridge.sendUpdateWindowSize(width, height);
						calculateMcScale();
						
						// TODO: Implement this method for GLFW window size callback
					}

					@Override
					public void onSurfaceTextureUpdated(SurfaceTexture texture) {
						
					}
				});
            
            OnGenericMotionListener gmlistener = new OnGenericMotionListener(){
                    @Override
                    public boolean onGenericMotion(View v, MotionEvent event) {
                        switch (event.getActionMasked()) {
                            case MotionEvent.ACTION_SCROLL:
                                CallbackBridge.sendScroll((double) event.getAxisValue(MotionEvent.AXIS_VSCROLL), (double) event.getAxisValue(MotionEvent.AXIS_HSCROLL));
                                break;
                        }
                        return true;
                    }
                };
            minecraftGLView.setOnGenericMotionListener(gmlistener);
            touchPad.setOnGenericMotionListener(gmlistener);
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
		// if (minecraftGLView != null) minecraftGLView.requestRender();
        final int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		/*
		 if (hasFocus && minecraftGLView.getVisibility() == View.GONE) {
		 minecraftGLView.setVisibility(View.VISIBLE);
		 }
		 */
	}

	@Override
	protected void onPause()
	{
		if (CallbackBridge.isGrabbing()){
			sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_ESCAPE);
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
			case R.id.control_up: sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_W, 0, isDown); break;
			case R.id.control_left: sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_A, 0, isDown); break;
			case R.id.control_down: sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_S, 0, isDown); break;
			case R.id.control_right: sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_D, 0, isDown); break;
			case R.id.control_jump: sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_SPACE, 0, isDown); break;
			case R.id.control_primary: sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT, isDown); break;
			case R.id.control_secondary:
				if (CallbackBridge.isGrabbing()) {
					sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, isDown);
				} else {
                    /*
					if (!isDown) {
                        CallbackBridge.putMouseEventWithCoords(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, CallbackBridge.mouseX, CallbackBridge.mouseY);
					}
                    */
                    
                    CallbackBridge.putMouseEventWithCoords(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, isDown ? 1 : 0, CallbackBridge.mouseX, CallbackBridge.mouseY);
                    
					setRightOverride(isDown);
				} break;
			case R.id.control_debug: sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_F3, 0, isDown); break;
			case R.id.control_shift: sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_LEFT_SHIFT, 0, isDown); break;
			case R.id.control_inventory: sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_E, 0, isDown); break;
			case R.id.control_talk: sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_T, 0, isDown); break;
			case R.id.control_keyboard: showKeyboard(); break;
			case R.id.control_thirdperson: sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_F5, 0, isDown); break;
			case R.id.control_zoom: sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_C, 0, isDown); break;
			case R.id.control_listplayers: sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_TAB, 0, isDown); break;
		}

        return false;
    }

	public static void fullyExit() {
		System.exit(0);
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

	// private FileObserver mLogObserver;
	private void runCraft() throws Throwable {
		/* Old logger
		if (Tools.LAUNCH_TYPE != Tools.LTYPE_PROCESS) {
			currLogFile = JREUtils.redirectStdio(true);
			// DEPRECATED constructor (String) api 29
			mLogObserver = new FileObserver(currLogFile.getAbsolutePath(), FileObserver.MODIFY){
				@Override
				public void onEvent(int event, String file) {
					try {
						if (event == FileObserver.MODIFY && currLogFile.length() > 0l) {
							System.out.println(Tools.read(currLogFile.getAbsolutePath()));
							Tools.write(currLogFile.getAbsolutePath(), "");
						}
					} catch (Throwable th) {
						Tools.showError(MainActivity.this, th);
						mLogObserver.stopWatching();
					}
				}
			};
			mLogObserver.startWatching();
		}
		*/
        
        appendlnToLog("--------- beggining with launcher debug");
        checkLWJGL3Installed();
        
        Map<String, String> jreReleaseList = readJREReleaseProperties();
        checkJavaArchitecture(jreReleaseList.get("OS_ARCH"));
        checkJavaArgsIsLaunchable(jreReleaseList.get("JAVA_VERSION"));
        // appendlnToLog("Info: Custom Java arguments: \"" + LauncherPreferences.PREF_CUSTOM_JAVA_ARGS + "\"");
        
		JREUtils.redirectAndPrintJRELog(this, mProfile.getAccessToken());
		Tools.launchMinecraft(this, mProfile, mVersionInfo);
	}
    
    private Map<String, String> readJREReleaseProperties() throws IOException {
        Map<String, String> jreReleaseMap = new ArrayMap<>();
        BufferedReader jreReleaseReader = new BufferedReader(new FileReader(Tools.homeJreDir + "/release"));
        String currLine;
        while ((currLine = jreReleaseReader.readLine()) != null) {
            if (!currLine.isEmpty() || currLine.contains("=")) {
                String[] keyValue = currLine.split("=");
                jreReleaseMap.put(keyValue[0], keyValue[1].replace("\"", ""));
            }
        }
        jreReleaseReader.close();
        return jreReleaseMap;
    }
    
    private void checkJavaArchitecture(String jreArch) throws Exception {
        String[] argName = Tools.currentArch.split("/");
        appendlnToLog("Architecture: " + Tools.currentArch);
        if (!(jreArch.contains(argName[0]) || jreArch.contains(argName[1]))) {
            appendlnToLog("Architecture " + Tools.currentArch + " is incompatible with Java Runtime " + jreArch);
            throw new RuntimeException(getString(R.string.mcn_check_fail_incompatiblearch, Tools.currentArch, jreArch));
        }
    }

    private void checkJavaArgsIsLaunchable(String jreVersion) throws Throwable {
        appendlnToLog("Info: Custom Java arguments: \"" + LauncherPreferences.PREF_CUSTOM_JAVA_ARGS + "\"");
        
        if (jreVersion.equals("1.9.0")) return;
        
    /*
        // Test java
        ShellProcessOperation shell = new ShellProcessOperation(new ShellProcessOperation.OnPrintListener(){
            @Override
            public void onPrintLine(String text){
                appendlnToLog("[JRETest] " + text);
            }
        });
        JREUtils.setJavaEnvironment(this, shell);
        
        List<String> testArgs = new ArrayList<String>();
        testArgs.add(Tools.homeJreDir + "/bin/java");
        Tools.getJavaArgs(this, testArgs);
        testArgs.add("-version");
        
        new File(Tools.homeJreDir + "/bin/java").setExecutable(true);
        
        // shell.writeToProcess("chmod 777 " + Tools.homeJreDir + "/bin/java");
        shell.writeToProcess("set -e");
        shell.writeToProcess(testArgs.toArray(new String[0]));
        
        int exitCode = shell.waitFor();
        appendlnToLog("Info: java test command exited with " + exitCode);
        
        if (exitCode != 0) {
            appendlnToLog("Error: the test returned non-zero exit code.");
            // throw new RuntimeException(getString(R.string.mcn_check_fail_java));
        }
    */
    }

    private void checkLWJGL3Installed() {
        File lwjgl3dir = new File(Tools.MAIN_PATH, "lwjgl3");
        if (!lwjgl3dir.exists() || lwjgl3dir.isFile() || lwjgl3dir.list().length == 0) {
            appendlnToLog("Error: LWJGL3 was not installed!");
            throw new RuntimeException(getString(R.string.mcn_check_fail_lwjgl));
        } else {
            appendlnToLog("Info: LWJGL3 directory: " + Arrays.toString(lwjgl3dir.list()));
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
     
    @Override
	public void appendToLog(final String text, boolean checkAllow) {
		logStream.print(text);
		if (checkAllow && !isLogAllow) return;
		textLog.post(new Runnable(){
				@Override
				public void run() {
					textLog.append(text);
					contentScroll.fullScroll(ScrollView.FOCUS_DOWN);
				}
			});
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
		if (CallbackBridge.isGrabbing()) return;

		boolean isVis = touchPad.getVisibility() == View.VISIBLE;
		touchPad.setVisibility(isVis ? View.GONE : View.VISIBLE);
		((Button) view).setText(isVis ? R.string.control_mouseoff: R.string.control_mouseon);
	}

	public static void dialogForceClose(Context ctx) {
		new AlertDialog.Builder(ctx)
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
		button.setWidth((int) Tools.dpToPx(this, Tools.pxToDp(this, button.getWidth()) * LauncherPreferences.PREF_BUTTONSIZE));
		button.setHeight((int) Tools.dpToPx(this, Tools.pxToDp(this, button.getHeight()) * LauncherPreferences.PREF_BUTTONSIZE));
        button.setOnTouchListener(this);
        return button;
    }

	@Override
	public void onBackPressed() {
		// Prevent back
		// Catch back as Esc keycode at another place
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
		minecraftGLView.requestFocus();
	}

	private void setRightOverride(boolean val) {
        this.rightOverride = val;
        // this.secondaryButton.setBackgroundDrawable(this.rightOverride ? this.secondaryButtonColorBackground : this.secondaryButtonDefaultBackground);
    }

	public void sendKeyPress(int keyCode, int modifiers, boolean status) {
        sendKeyPress(keyCode, '\u0000', modifiers, status);
    }

    public void sendKeyPress(int keyCode, char keyChar, int modifiers, boolean status) {
        CallbackBridge.sendKeycode(keyCode, keyChar, modifiers, status);
    }

	public void sendKeyPress(char keyChar) {
		sendKeyPress(0, keyChar, 0, true);
		sendKeyPress(0, keyChar, 0, false);
	}

	public void sendKeyPress(int keyCode) {
		sendKeyPress(keyCode, 0, true);
		sendKeyPress(keyCode, 0, false);
	}

    private boolean isLeftMouseDown, isRightMouseDown;
	public void sendMouseButton(int button, boolean status) {
        // TODO implement this method!!!
        // CallbackBridge.setMouseButtonInGrabMode((byte) button, status ? (byte) 1 : (byte) 0);
        // or
        
        isLeftMouseDown = button == LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT && status;
        isRightMouseDown = button == LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT && status;
        
        CallbackBridge.sendMouseKeycode(button, 0, status);
    }

	public void calculateMcScale() {
        int scale = 1;
        while (CallbackBridge.windowWidth / (scale + 1) >= 320 && CallbackBridge.windowHeight / (scale + 1) >= 240) {
            scale++;
        }
        this.guiScale = scale;
    }

    public int handleGuiBar(int x, int y) {
        if (!CallbackBridge.isGrabbing()) return -1;
        
        int barheight = mcscale(20);
        int barwidth = mcscale(180);
        int barx = (CallbackBridge.windowWidth / 2) - (barwidth / 2);
        int bary = CallbackBridge.windowHeight - barheight;
        if (x < barx || x >= barx + barwidth || y < bary || y >= bary + barheight) {
            return -1;
        }
        return hotbarKeys[((x - barx) / mcscale(180 / 9)) % 9];
	}
/*
	public int handleGuiBar(int x, int y, MotionEvent e) {
        if (!CallbackBridge.isGrabbing()) {
            return -1;
        }

        // int screenHeight = CallbackBridge.windowHeight;
        int barheight = mcscale(20);
        int barwidth = mcscale(180);
        int barx = (CallbackBridge.windowWidth / 2) - (barwidth / 2);
        if (x < barx || x >= barx + barwidth || y < 0 || y >= 0 + barheight) {
            return -1;
        }
        return hotbarKeys[((x - barx) / mcscale(20)) % 9];
    }
*/
}
