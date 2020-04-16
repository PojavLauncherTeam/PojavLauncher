package net.kdt.pojavlaunch;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
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
import com.android.internal.awt.*;
import com.kdt.glsupport.*;
import com.kdt.pointer.*;
import dalvik.system.*;
import java.io.*;
import java.lang.reflect.*;
import java.security.*;
import java.util.*;
import javax.crypto.*;
import javax.microedition.khronos.egl.*;
import javax.microedition.khronos.opengles.*;
import net.kdt.pojavlaunch.exit.*;
import net.kdt.pojavlaunch.prefs.*;
import optifine.*;
import org.apache.harmony.security.fortress.*;
import org.lwjgl.input.*;
import org.lwjgl.opengl.*;
import org.lwjgl.util.applet.*;
import org.lwjgl.util.glu.tessellation.*;

import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import net.kdt.pojavlaunch.value.customcontrols.*;
import com.google.android.gles_jni.*;

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
			mProfile = PojavProfile.getCurrentProfileContent(this);
			mVersionInfo = Tools.getVersionInfo(mProfile.getVersion());
			
			setTitle("Minecraft " + mProfile.getVersion());
			
			initEnvs();
			//System.loadLibrary("gl4es");
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

			Bitmap awtGraphics = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
			AndroidGraphics2D.getInstance(this, new Canvas(awtGraphics), null);
			
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
									
									theHandler.sendEmptyMessageDelayed(MainActivity.MSG_DROP_ITEM_BUTTON_CHECK, 500);
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
										theHandler.sendEmptyMessageDelayed(MainActivity.MSG_LEFT_MOUSE_BUTTON_CHECK, 500);
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
					
					if (debugText.getVisibility() == View.VISIBLE) {
						StringBuilder builder = new StringBuilder();
						builder.append("PointerCapture debug\n");
						builder.append("MotionEvent=" + e.getActionMasked() + "\n\n");
					
						builder.append("PointerX=" + e.getX() + "\n");
						builder.append("PointerY=" + e.getY() + "\n");
						builder.append("RawX=" + e.getRawX() + "\n");
						builder.append("RawY=" + e.getRawY() + "\n\n");
					
						builder.append("XPos=" + x + "\n");
						builder.append("YPos=" + y + "\n\n");
						builder.append("MovingX=" + getMoving(e.getX(), true) + "\n");
						builder.append("MovingY=" + getMoving(e.getY(), false) + "\n");
						
						debugText.setText(builder.toString());
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
							AndroidDisplay.putMouseEventWithCoords(rightOverride ? (byte) 1 : (byte) 0, (byte) 1, x, y, 0, System.nanoTime());
							initialX = x;
							initialY = y;
							theHandler.sendEmptyMessageDelayed(MainActivity.MSG_LEFT_MOUSE_BUTTON_CHECK, 500);
							break;

						case MotionEvent.ACTION_UP: // 1
						case MotionEvent.ACTION_CANCEL: // 3
						case MotionEvent.ACTION_POINTER_UP: // 6
							AndroidDisplay.putMouseEventWithCoords(rightOverride ? (byte) 1 : (byte) 0, (byte) 0, x, y, 0, System.nanoTime());
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
							sendMouseButton(AndroidDisplay.mouseLeft ? 0 : 1, false);
							
							// triggeredLeftMouseButton = false;
							theHandler.removeMessages(MainActivity.MSG_LEFT_MOUSE_BUTTON_CHECK);
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
						if (!AndroidDisplay.grab && isResumed()) {
							return glTouchListener.onTouch(p1, p2);
						}
						return true;
					}
				});
			glSurfaceView.setOnTouchListener(glTouchListener);
			glSurfaceView.setRenderer(new GLTextureView.Renderer() {
					private volatile long eglContext = 0l;
					@Override
					public void onSurfaceDestroyed(GL10 gl) {
						Log.d(Tools.APP_NAME, "Surface destroyed.");
					}

					@Override
					public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig p2)
					{
						calculateMcScale();

						EGL10 egl10 = (EGL10) EGLContext.getEGL();
						
						/*
						AndroidContextImplementation.theEgl = egl10;
						AndroidContextImplementation.context = egl10.eglGetCurrentContext();
						AndroidContextImplementation.display = egl10.eglGetCurrentDisplay();
						AndroidContextImplementation.read = egl10.eglGetCurrentSurface(EGL10.EGL_READ);
						AndroidContextImplementation.draw = egl10.eglGetCurrentSurface(EGL10.EGL_DRAW);
						egl10.eglMakeCurrent(AndroidContextImplementation.display, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
						System.out.println(new StringBuffer().append("Gave up context: ").append(AndroidContextImplementation.context).toString());
						*/
						
						EGLContextImpl eglContextImpl = (EGLContextImpl) egl10.eglGetCurrentContext();
						try {
							Field eglContextField = eglContextImpl.getClass().getDeclaredField("mEGLContext");
							eglContextField.setAccessible(true);
							eglContext = eglContextField.get(eglContextImpl);
						} catch (Throwable th) {
							Tools.showError(MainActivity.this, th, true);
						}
						
						new Thread(new Runnable(){

								@Override
								public void run()
								{
									try {
										Thread.sleep(200);
										runCraft(eglContext);
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
			glSurfaceView.requestRender();
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
		glSurfaceView.requestRender();
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
					AndroidDisplay.putMouseEventWithCoords(/* right mouse */ (byte) 1, ((byte) (isDown ? 1 : 0)), AndroidDisplay.mouseX, AndroidDisplay.mouseY, 0, System.nanoTime());
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

    public void initEnvs() {
        try {
            Os.setenv("LIBGL_MIPMAP", "3", true);
            System.setProperty("user.home", Tools.MAIN_PATH);
            if (!System.getProperty("user.home", "/").equals(Tools.MAIN_PATH)) {
                forceUserHome(Tools.MAIN_PATH);
            }
            System.setProperty("org.apache.logging.log4j.level", "INFO");
            System.setProperty("org.apache.logging.log4j.simplelog.level", "INFO");
			
			// Disable javax management for smaller launcher.
			System.setProperty("log4j2.disable.jmx", "true");

            //System.setProperty("net.zhuoweizhang.boardwalk.org.apache.logging.log4j.level", "INFO");
            //System.setProperty("net.zhuoweizhang.boardwalk.org.apache.logging.log4j.simplelog.level", "INFO");
			
			// Change info for useful dump
			System.setProperty("java.vm.info", Build.MANUFACTURER + " " + Build.MODEL + ", Android " + Build.VERSION.RELEASE);
        } catch (Exception e) {
            Tools.showError(MainActivity.this, e, true);
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
		
		String[] argsFromJson = insertVariableArgument(splitAndFilterEmpty(mVersionInfo.minecraftArguments), varArgMap);
		// Tools.dialogOnUiThread(this, "Result args", Arrays.asList(argsFromJson).toString());
		return argsFromJson;
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
	
	public static String launchClassPath;
	public static String launchOptimizedDirectory;
	public static String launchLibrarySearchPath;
	private void runCraft(long eglContext) throws Throwable
	{
		String[] launchArgs = getMCArgs();
		
		// Setup OptiFine
		if (mVersionInfo.optifineLib != null) {
			String[] optifineInfo = mVersionInfo.optifineLib.name.split(":");
			String optifineJar = Tools.libraries + "/" + Tools.artifactToPath(optifineInfo[0], optifineInfo[1], optifineInfo[2]);

			AndroidOptiFineUtilities.originalOptifineJar = PojavPreferenceActivity.PREF_FORGETOF ? "/null/file.jar" : optifineJar;
		}
		
		File optDir = getDir("dalvik-cache", 0);
		optDir.mkdirs();

		launchClassPath = Tools.generate(mProfile.getVersion());
		launchOptimizedDirectory = optDir.getAbsolutePath();
		launchLibrarySearchPath = getApplicationInfo().nativeLibraryDir;
		
		if (mVersionInfo.mainClass.equals("net.minecraft.launchwrapper.Launch")) {
			net.minecraft.launchwrapper.Launch.main(launchArgs);
		} else {
			/*
			LoggerJava.OnStringPrintListener printLog = new LoggerJava.OnStringPrintListener(){

				@Override
				public void onCharPrint(char c)
				{
					appendToLog(Character.toString(c));
				}
			};

			PrintStream theStreamOut = new PrintStream( new LoggerJava.LoggerOutputStream(System.out, printLog));
			System.setOut(theStreamOut);

			PrintStream theStreamErr = new PrintStream(new LoggerJava.LoggerOutputStream(System.err, printLog));
			System.setErr(theStreamErr);
			*/
			
			fixRSAPadding();

			System.out.println("> Running Minecraft with classpath:");
			System.out.println(launchClassPath);
			System.out.println();
			
			List<String> dalvikArgs = new ArrayList<String>();
			dalvikArgs.add("dalvikvm32");
			dalvikArgs.add("-Dorg.apache.logging.log4j.level=INFO");
            dalvikArgs.add("-Dorg.apache.logging.log4j.simplelog.level=INFO");
			dalvikArgs.add("-Dlog4j2.disable.jmx=true");
			dalvikArgs.add("-Xmx512M"); // Max heap
			dalvikArgs.add("-Djava.library.path=/system/lib:" + getApplicationInfo().nativeLibraryDir);
			dalvikArgs.add("-cp");
			dalvikArgs.add(getApplicationInfo().publicSourceDir + ":" + launchClassPath);
			
			dalvikArgs.add("com.kdt.minecraftegl.MinecraftEGLInitializer");
			dalvikArgs.add(Long.toString(eglContext));
			dalvikArgs.add(mVersionInfo.mainClass);
			dalvikArgs.addAll(Arrays.asList(launchArgs));
			
			java.lang.Process process = Runtime.getRuntime().exec(dalvikArgs.toArray(new String[0]));
			
			BufferedReader bis1 = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String read1;
			while((read1 = bis1.readLine()) != null) {
        		appendlnToLog(read1);
			}

			BufferedReader bis2 = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String read2;
			while((read2 = bis2.readLine()) != null) {
        		appendlnToLog(read2);
			}
			
			final int waitFor = process.waitFor();
			
			runOnUiThread(new Runnable(){

					@Override
					public void run()
					{
						AlertDialog.Builder d = new AlertDialog.Builder(MainActivity.this);
						d.setTitle(R.string.mcn_exit_title);
						d.setMessage("Exited with code " + waitFor);
						d.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){

								@Override
								public void onClick(DialogInterface p1, int p2)
								{
									// finish();
								}
							});
						d.setCancelable(false);
						d.show();
					}
				});
		}
	}

	private void createEGLHackStuff() {
		 
	}
	
	public void fixRSAPadding() throws Exception {
		// welcome to the territory of YOLO; I'll be your tour guide for today.
		
		try {
/*
			System.out.println(Cipher.getInstance("RSA"));
			System.out.println(Cipher.getInstance("RSA/ECB/PKCS1Padding"));
*/
			if (android.os.Build.VERSION.SDK_INT >= 23) { // Marshmallow
				// FUUUUU I DON'T KNOW FIXME
				Cipher rsaPkcs1Cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
				// Cipher.getInstance("RSA", rsaPkcs1Cipher.getProvider());
				
				Cipher newRSACipher = Cipher.getInstance("RSA");
				
				Field fieldPKCS1 = Provider.class.getDeclaredField("serviceMap");
				fieldPKCS1.setAccessible(true);
				Map /* <Provider.ServerKey, Provider.Service> */ mapPKCS1 = (Map) fieldPKCS1.get(rsaPkcs1Cipher.getProvider());
				
				Field fieldRSA = Provider.class.getDeclaredField("serviceMap");
				fieldRSA.setAccessible(true);
				Map /* <Provider.ServerKey, Provider.Service> */ mapRSA = (Map) fieldRSA.get(newRSACipher.getProvider());
				mapRSA.clear();
				mapRSA.putAll(mapPKCS1);
			} else {
				ArrayList<Provider.Service> rsaList = Services.getServices("Cipher.RSA");
				ArrayList<Provider.Service> rsaPkcs1List = Services.getServices("Cipher.RSA/ECB/PKCS1PADDING");
				rsaList.clear();
				rsaList.addAll(rsaPkcs1List);
			}
		} catch (Throwable th) {
			// Tools.dialogOnUiThread(MainActivity.this, "Warning: can't fix RSA Padding", Log.getStackTraceString(th));
			th.printStackTrace();
			
			runOnUiThread(new Runnable(){

					@Override
					public void run()
					{
						Toast.makeText(MainActivity.this, "Unable to fix RSAPadding. You can't play premium servers", Toast.LENGTH_LONG).show();
					}
				});
		}
		
		
		//System.out.println("After: " + KeyFactory.getInstance("RSA") + ":" + KeyFactory.getInstance("RSA").getProvider());

		/*		Provider provider = KeyFactory.getInstance("RSA").getProvider();
		 System.out.println("Before: " + provider.getService("KeyService", "RSA"));
		 Provider.Service service = provider.getService("KeyService", "RSA/ECB/PKCS5Padding");
		 System.out.println(service);
		 provider.putService(service);
		 System.out.println("After: " + provider.getService("KeyService", "RSA"));*/
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
		WindowAnimation.fadeIn(contentLog, 500);
	}

	public void closeLogOutput(View view) {
		//scrollLog.setVisibility(View.GONE);

		WindowAnimation.fadeOut(contentLog, 500);
	}
/*
	private void openCanvasOutput() {
		WindowAnimation.fadeIn(contentCanvas, 500);
	}
	
	public void closeCanvasOutput(View view) {
		WindowAnimation.fadeOut(contentCanvas, 500);
	}
*/
	private void appendlnToLog(String text) {
		appendToLog(text + "\n");
	}
	
	private void appendToLog(final String text) {
		// if (!isLogAllow) return;
		textLog.post(new Runnable(){
				@Override
				public void run()
				{
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
					android.os.Process.killProcess(android.os.Process.myPid());
					
					// Toast.makeText(MainActivity.this, "Could not exit. Please force close this app.", Toast.LENGTH_LONG).show();
				}

				private void fullyExit()
				{
					// TODO: Implement this method
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
