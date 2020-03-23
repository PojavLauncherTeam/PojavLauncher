package net.kdt.pojavlaunch;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.widget.*;
import android.system.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.inputmethod.*;
import android.widget.*;
import com.kdt.glsupport.*;
import dalvik.system.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import javax.microedition.khronos.egl.*;
import javax.microedition.khronos.opengles.*;
import net.kdt.pojavlaunch.exit.*;
import net.kdt.pojavlaunch.libs.*;
import net.minecraft.launchwrapper.*;
import org.lwjgl.input.*;
import org.lwjgl.opengl.*;
import org.lwjgl.util.applet.*;
import org.lwjgl.util.glu.tessellation.*;

import android.graphics.drawable.Drawable;
import android.view.GestureDetector.*;
import java.util.concurrent.locks.*;
import com.kdt.pointer.*;
import net.kdt.pojavlaunch.value.*;
import java.net.*;
public class MainActivity extends Activity implements OnTouchListener
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
	private static boolean triggeredLeftMouseButton = false;
	private Handler theHandler = new Handler();
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
				   screenshotButton, listPlayersButton;
	private LinearLayout touchPad;
	private ImageView mousePointer;
	//private EditText hiddenEditor;
	private ViewGroup overlayView;
	private Drawable secondaryButtonColorBackground;
    private Drawable secondaryButtonDefaultBackground;
	private MCProfile.Builder mProfile;
	
	private DrawerLayout drawerLayout;
    private NavigationView navDrawer;
	
	private LinearLayout contentLog;
	private TextView textLog, textLogBehindGL;
	private ScrollView contentScroll;
	private ToggleButton toggleScrollLog;
	private GestureDetector gestureDetector;

	private PointerOreoWrapper pointerSurface;
	
	private StringBuilder mQueueText = new StringBuilder();
	
	private MinecraftVersion mVersionInfo;
	
	/*
	private LinearLayout contentCanvas;
	private AWTSurfaceView contentCanvasView;
	*/
	private boolean lastEnabled = false;
	private boolean lastGrab = false;
	private boolean isExited = false;
	private boolean isLogAllow = false;
	
	private String getStr(int id) {
		return getResources().getString(id);
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		try {
			ExitManager.setExitTrappedListener(new ExitManager.ExitTrappedListener(){
				@Override
				public void onExitTrapped()
				{
					runOnUiThread(new Runnable(){

						@Override
						public void run()
						{
							isExited = true;
							
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
			
			initEnvs();
			System.loadLibrary("gl04es");
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
							case R.id.nav_forceclose: forceCloseSure();
								break;
							case R.id.nav_viewlog: openLogOutput();
								break;
							case R.id.nav_viewcanvas: //openCanvasOutput();
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
			this.screenshotButton = findButton(R.id.control_screenshot);
			this.listPlayersButton = findButton(R.id.control_listplayers);
			this.overlayView = (ViewGroup) findViewById(R.id.main_control_overlay);
			this.secondaryButtonDefaultBackground = this.secondaryButton.getBackground();
			this.secondaryButtonColorBackground = new ColorDrawable(-65536);

			//this.hiddenEditor = findViewById(R.id.hiddenTextbox);

			// Mouse pointer part
			//this.mouseToggleButton = findButton(R.id.control_togglemouse);
			this.touchPad = (LinearLayout) findViewById(R.id.main_touchpad);
			this.mousePointer = (ImageView) findViewById(R.id.main_mouse_pointer);

			this.contentLog = (LinearLayout) findViewById(R.id.content_log_layout);
			this.contentScroll = (ScrollView) findViewById(R.id.content_log_scroll);
			this.textLog = (TextView) contentScroll.getChildAt(0);
			this.toggleScrollLog = (ToggleButton) findViewById(R.id.content_log_toggle_scrolldown);
			this.toggleScrollLog.setChecked(true);
			this.textLogBehindGL = (TextView) findViewById(R.id.main_log_behind_GL);
			this.textLogBehindGL.setTypeface(Typeface.MONOSPACE);
			
			
			/*
			this.contentCanvas = (LinearLayout) findViewById(R.id.content_canvas_layout);
			this.contentCanvasView = (AWTSurfaceView) findViewById(R.id.content_canvas_view);
			this.contentCanvasView.startAWTThread(displayMetrics.widthPixels, displayMetrics.heightPixels);
			*/
			
			// this.mirrorView = (ImageView) findViewById(R.id.mainGameSecondRenderView);
			
			this.textLog.setTypeface(Typeface.MONOSPACE);
			
			LogWrapper.setAndroidLogReceiver(new LogWrapper.AndroidLogger(){

					@Override
					public void onPrint(String time, String name, String level, String message)
					{
						appendlnToLog("[" + time + "] [main/" + level + "]: [" + name + "] " + message);
					}
				});

			toggleGui(null);

			this.drawerLayout.closeDrawers();

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

			// Touch pad
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
							AndroidDisplay.putMouseEventWithCoords(MainActivity.this.rightOverride ? (byte) 1 : (byte) 0, (byte) 0, (int) mouseX, (int) (AndroidDisplay.windowHeight - mouseY), 0, System.nanoTime());
							AndroidDisplay.putMouseEventWithCoords(MainActivity.this.rightOverride ? (byte) 1 : (byte) 0, (byte) 1, (int) mouseX, (int) (AndroidDisplay.windowHeight - mouseY), 0, System.nanoTime());
							if (!MainActivity.this.rightOverride) {
								AndroidDisplay.mouseLeft = true;
							}
							
						} else {
							switch (action) {
								/*
								case MotionEvent.ACTION_DOWN: // 0
								case MotionEvent.ACTION_POINTER_DOWN: // 5
									if (mVelocityTracker == null) {
										mVelocityTracker = VelocityTracker.obtain();
									}
									else {
										mVelocityTracker.clear();
									}
									mVelocityTracker.addMovement(event);
									break;
								*/
								case MotionEvent.ACTION_UP: // 1
								case MotionEvent.ACTION_CANCEL: // 3
								case MotionEvent.ACTION_POINTER_UP: // 6
									if (!MainActivity.this.rightOverride) {
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
		} catch (Throwable e) {
			e.printStackTrace();
			Tools.showError(this, e, true);
		}
		

		this.glSurfaceView = (MinecraftGLView) findViewById(R.id.main_game_render_view);
		
		glSurfaceView.setEGLContextClientVersion(2);
		
		final View.OnTouchListener glTouchListener = new OnTouchListener(){

			@Override
			public boolean onTouch(View p1, MotionEvent e)
			{
				int x = ((int) e.getX()) / MainActivity.this.scaleFactor;
				int y = (MainActivity.this.glSurfaceView.getHeight() - ((int) e.getY())) / MainActivity.this.scaleFactor;
				if (MainActivity.this.handleGuiBar(x, y, e)) {
					return true;
				} else if (!AndroidDisplay.grab && gestureDetector.onTouchEvent(e)) {
					AndroidDisplay.putMouseEventWithCoords(MainActivity.this.rightOverride ? (byte) 1 : (byte) 0, (byte) 0, x, y, 0, System.nanoTime());
					AndroidDisplay.putMouseEventWithCoords(MainActivity.this.rightOverride ? (byte) 1 : (byte) 0, (byte) 1, x, y, 0, System.nanoTime());
					if (!MainActivity.this.rightOverride) {
						AndroidDisplay.mouseLeft = true;
					}
					return true;
				} else {
					AndroidDisplay.mouseX = x;
					AndroidDisplay.mouseY = y;
					switch (e.getActionMasked()) {
						case e.ACTION_DOWN: // 0
						case e.ACTION_POINTER_DOWN: // 5
							if (!MainActivity.this.rightOverride) {
								AndroidDisplay.mouseLeft = true;
							}

							if (AndroidDisplay.grab) {
								AndroidDisplay.putMouseEventWithCoords(MainActivity.this.rightOverride ? (byte) 1 : (byte) 0, (byte) 1, x, y, 0, System.nanoTime());
								MainActivity.this.initialX = x;
								MainActivity.this.initialY = y;
								MainActivity.this.theHandler.sendEmptyMessageDelayed(MainActivity.MSG_LEFT_MOUSE_BUTTON_CHECK, 500);
								break;
							}
							break;

						case e.ACTION_UP: // 1
						case e.ACTION_CANCEL: // 3
						case e.ACTION_POINTER_UP: // 6
							AndroidDisplay.putMouseEventWithCoords(MainActivity.this.rightOverride ? (byte) 1 : (byte) 0, (byte) 0, x, y, 0, System.nanoTime());
							if (!MainActivity.this.rightOverride) {
								AndroidDisplay.mouseLeft = false;
							}

							if (AndroidDisplay.grab) {
								MainActivity.this.initialX = x;
								MainActivity.this.initialY = y;
								MainActivity.this.theHandler.sendEmptyMessageDelayed(MainActivity.MSG_LEFT_MOUSE_BUTTON_CHECK, 500);

								if (!MainActivity.this.triggeredLeftMouseButton && Math.abs(MainActivity.this.initialX - x) < MainActivity.this.fingerStillThreshold && Math.abs(MainActivity.this.initialY - y) < MainActivity.this.fingerStillThreshold) {
									MainActivity.this.sendMouseButton(1, true);
									MainActivity.this.sendMouseButton(1, false);
								}
								if (MainActivity.this.triggeredLeftMouseButton) {
									MainActivity.this.sendMouseButton(0, false);
								}
								MainActivity.this.triggeredLeftMouseButton = false;
								MainActivity.this.theHandler.removeMessages(MainActivity.MSG_LEFT_MOUSE_BUTTON_CHECK);
								break;
							}
							break;
					}
				}

				return true;
				// If onClick fail with false, change back to true
			}
		};
		
		if (isPointerCaptureSupported()) {
			this.pointerSurface = new PointerOreoWrapper(glSurfaceView);
			this.pointerSurface.setOnCapturedPointerListener(new PointerOreoWrapper.OnCapturedPointerListener(){

					@Override
					public boolean onCapturedPointer(View view, MotionEvent event)
					{
						return glTouchListener.onTouch(view, event);
					}
				});
		}
		
		glSurfaceView.setOnHoverListener(new View.OnHoverListener(){

				@Override
				public boolean onHover(View p1, MotionEvent p2)
				{
					if (!AndroidDisplay.grab) {
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
					MainActivity.this.calculateMcScale();
					
					EGL10 egl10 = (EGL10) EGLContext.getEGL();
					AndroidContextImplementation.theEgl = egl10;
					AndroidContextImplementation.context = egl10.eglGetCurrentContext();
					AndroidContextImplementation.display = egl10.eglGetCurrentDisplay();
					AndroidContextImplementation.read = egl10.eglGetCurrentSurface(12378);
					AndroidContextImplementation.draw = egl10.eglGetCurrentSurface(12377);
					egl10.eglMakeCurrent(AndroidContextImplementation.display, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
					System.out.println(new StringBuffer().append("Gave up context: ").append(AndroidContextImplementation.context).toString());
					
					new Thread(new Runnable(){

							@Override
							public void run()
							{
								synchronized (MainActivity.this) {
									try
									{
										Thread.sleep(200);
										runCraft();
									}
									catch (Throwable e)
									{
										Tools.showError(MainActivity.this, e, true);
									}
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
					AndroidDisplay.windowWidth = width / MainActivity.this.scaleFactor;
					AndroidDisplay.windowHeight = height / MainActivity.this.scaleFactor;
				}
			});
		glSurfaceView.setPreserveEGLContextOnPause(true);
		glSurfaceView.setRenderMode(MinecraftGLView.RENDERMODE_CONTINUOUSLY);
		glSurfaceView.requestRender();
		/*
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					try {
						Thread.sleep(5000);
						isLogAllow = true;
						appendToLog("");
					} catch (InterruptedException e) {}
				}
			}).start();
		*/
		
		
		
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
			onBackPressed();
		}
		super.onPause();
	}

	public static void fullyExit() {
		ExitManager.stopExitLoop();
	}
	/*
	private void fillCanvasGL() {
		//bit.eraseColor(Color.TRANSPARENT);
		//Canvas c = ;
		int measuredWidth = View.MeasureSpec.makeMeasureSpec(AndroidDisplay.windowWidth, View.MeasureSpec.EXACTLY);
		int measuredHeight = View.MeasureSpec.makeMeasureSpec(AndroidDisplay.windowHeight, View.MeasureSpec.EXACTLY);

		//Cause the view to re-layout
		glSurfaceView.measure(measuredWidth, measuredHeight);
		glSurfaceView.layout(0, 0, measuredWidth, measuredHeight);
		
		glSurfaceView.draw(new Canvas(bit));
	}
	*/
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
            //System.setProperty("net.zhuoweizhang.boardwalk.org.apache.logging.log4j.level", "INFO");
            //System.setProperty("net.zhuoweizhang.boardwalk.org.apache.logging.log4j.simplelog.level", "INFO");
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
		varArgMap.put("assets_index_name", versionName);
		varArgMap.put("auth_uuid", mProfile.getProfileID());
		varArgMap.put("auth_access_token", mProfile.getAccessToken());
		varArgMap.put("user_properties", "{}");
		varArgMap.put("user_type", userType);
		
		String[] argsFromJson = insertVariableArgument(mVersionInfo.minecraftArguments.split(" "), varArgMap);
		return argsFromJson;
	}
	
	private String[] insertVariableArgument(String[] args, Map<String, String> keyValueMap) {
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			String argVar = null;
			if (arg.startsWith("${") && arg.endsWith("}")) {
				argVar = arg.substring(2, arg.length() - 2);
				for (Map.Entry<String, String> keyValue : keyValueMap.entrySet()) {
					if (argVar.equals(keyValue.getKey())) {
						args[i] = keyValue.getValue();
					}
				}
			}
			
			// Check again
			if (arg.startsWith("${") && arg.endsWith("}")) {
				System.out.println("Warning: Can't find variable \"" + argVar + "\".");
			}
		}
		return args;
	}
	
	private void runCraft() throws Throwable
	{
		// BEGIN KEEPUP
		File optDir = getDir("dalvik-cache", 0);
		optDir.mkdirs();
		
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

		String classpath = Tools.generate(mProfile.getVersion());
		
		System.out.println("> Running Minecraft with classpath:");
		System.out.println(classpath);
		System.out.println();
		
		
		LaunchClassLoaderAgruments.putAll(classpath, optDir.getAbsolutePath(), getApplicationInfo().nativeLibraryDir);
		
		ClassLoader loader;
		loader = new DexClassLoader(classpath, optDir.getAbsolutePath(), getApplicationInfo().nativeLibraryDir, getClassLoader());
		
		// BEGIN URL
		/*
		List<URL> urlList = new ArrayList<URL>();
		for (String perJar : classpath.split(":")) {
			if (perJar.isEmpty()) continue;
			urlList.add(new File(perJar).toURI().toURL());
		}
		
		loader = new URLClassLoader(urlList.toArray(new URL[0]));
		*/
		// END URL
		Class mainClass = loader.loadClass(mVersionInfo.mainClass);
		Method mainMethod = mainClass.getMethod("main", String[].class);
		mainMethod.setAccessible(true);
		mainMethod.invoke(null, new Object[]{getMCArgs()});
		
		// Method v6:
		
		/**
		 * Pojav Execute arguments.
		 *
		 * [0] = argOptdir (eg. "/dir")
		 * [1] = argNative (eg. "/dir/lib/arm64")
		 * [2] = classpath (eg. "mc.jar:lib1.jar:...")
		 * [3] = mainclass (eg. "net.minecraft.client.Minecraft")
		 */
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
		// mQueueText.append(text);
		
		if (!isLogAllow) {
			return;
		}
		
		try {
			textLog.post(new Runnable(){
					@Override
					public void run()
					{
						textLog.append(text);
						if (toggleScrollLog.isChecked()) {
							contentScroll.fullScroll(ScrollView.FOCUS_DOWN);
						}
					}
				});
			textLogBehindGL.post(new Runnable(){

					@Override
					public void run()
					{
						textLogBehindGL.append(text);
					}
				});
		} finally {
			mQueueText.setLength(0);
		}
	}
	
	public void handleMessage(Message msg) {
		switch (msg.what) {
			case MainActivity.MSG_LEFT_MOUSE_BUTTON_CHECK /*1028*/:
				int x = AndroidDisplay.mouseX;
				int y = AndroidDisplay.mouseY;
				if (AndroidDisplay.grab && Math.abs(MainActivity.this.initialX - x) < MainActivity.this.fingerStillThreshold && Math.abs(MainActivity.this.initialY - y) < MainActivity.this.fingerStillThreshold) {
					MainActivity.this.triggeredLeftMouseButton = true;
					MainActivity.this.sendMouseButton(0, true);
					return;
				}
				return;
			default:
				return;
		}
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
	
	public void toggleGui(View view) {
		switch(overlayView.getVisibility()){
			case View.VISIBLE: overlayView.setVisibility(View.GONE);
				break;
			case View.GONE: overlayView.setVisibility(View.VISIBLE);
		}
	}
	
	public void toggleMouse(View view) {
		if (AndroidDisplay.grab) return;
		
		boolean isVis = touchPad.getVisibility() == View.VISIBLE;
		touchPad.setVisibility(isVis ? View.GONE : View.VISIBLE);
		((Button) view).setText(isVis ? R.string.control_mouseoff: R.string.control_mouseon);
	}
	
	public void forceCloseSure()
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
			})
			.show();
	}
	
	private Button findButton(int id) {
        Button button = (Button) findViewById(id);
        button.setOnTouchListener(this);
        return button;
    }

    public boolean onTouch(View v, MotionEvent e) {
        boolean isDown;
        switch (e.getActionMasked()) {
            case TessState.T_DORMANT /*0*/:
            case AppletLoader.STATE_CHECKING_FOR_UPDATES /*5*/:
                isDown = true;
                break;
            case TessState.T_IN_POLYGON /*1*/:
            case AppletLoader.STATE_DETERMINING_PACKAGES /*3*/:
            case AppletLoader.STATE_DOWNLOADING /*6*/:
                isDown = false;
                break;
            default:
                return false;
        }
        if (v == this.upButton) {
            sendKeyPress(Keyboard.KEY_W, isDown);
        } else if (v == this.downButton) {
            sendKeyPress(Keyboard.KEY_S, isDown);
        } else if (v == this.leftButton) {
            sendKeyPress(Keyboard.KEY_A, isDown);
        } else if (v == this.rightButton) {
            sendKeyPress(Keyboard.KEY_D, isDown);
        } else if (v == this.jumpButton) {
            sendKeyPress(Keyboard.KEY_SPACE, isDown);
        } else if (v == this.primaryButton) {
            sendMouseButton(0, isDown);
        } else if (v == this.secondaryButton) {
            if (AndroidDisplay.grab) {
                sendMouseButton(1, isDown);
            } else {
                setRightOverride(isDown);
            }
        } else if (v == debugButton) {
			sendKeyPress(Keyboard.KEY_F3, isDown);
		} else if (v == shiftButton) {
			sendKeyPress(Keyboard.KEY_LSHIFT, isDown);
		} else if (v == inventoryButton) {
			sendKeyPress(Keyboard.KEY_E, isDown);
		} else if (v == talkButton) {
			sendKeyPress(Keyboard.KEY_T, isDown);
		} else if (v == keyboardButton) {
			showKeyboard();
		} else if (v == thirdPersonButton) {
			sendKeyPress(Keyboard.KEY_F5, isDown);
		} else if (v == this.screenshotButton) {
			sendKeyPress(Keyboard.KEY_F2, isDown);
		} else if (v == this.listPlayersButton) {
			sendKeyPress(Keyboard.KEY_TAB, isDown);
		}
        return false;
    }
	
	@Override
	public void onBackPressed() {
		sendKeyPress(Keyboard.KEY_ESCAPE);
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
	}
	
	private void setRightOverride(boolean val) {
        this.rightOverride = val;
        this.secondaryButton.setBackgroundDrawable(this.rightOverride ? this.secondaryButtonColorBackground : this.secondaryButtonDefaultBackground);
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
	
	public boolean handleGuiBar(int x, int y, MotionEvent e) {
        if (!AndroidDisplay.grab) {
            return false;
        }
        boolean isDown;
        switch (e.getActionMasked()) {
            case 0:
            case 5:
                isDown = true;
                break;
            case 1:
            case 3:
            case 6:
                isDown = false;
                break;
            default:
                return false;
        }
        int screenWidth = AndroidDisplay.windowWidth;
        int screenHeight = AndroidDisplay.windowHeight;
        int barheight = mcscale(20);
        int barwidth = mcscale(180);
        int barx = (screenWidth / 2) - (barwidth / 2);
        if (x < barx || x >= barx + barwidth || y < 0 || y >= 0 + barheight) {
            return false;
        }
        sendKeyPress(hotbarKeys[((x - barx) / mcscale(20)) % 9], isDown);
        return true;
    }
	
	private class SingleTapConfirm extends SimpleOnGestureListener
	{
        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }
    }
}
