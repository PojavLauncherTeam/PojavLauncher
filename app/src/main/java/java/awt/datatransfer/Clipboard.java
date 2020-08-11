package java.awt.datatransfer;

import android.app.Activity;
import android.content.Context;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.widget.Toast;

import net.kdt.pojavlaunch.Tools;
import java.awt.mod.*;
import android.os.*;

public class Clipboard extends Object
{
	public static Context mSystemCtx;
	public static ClipboardManager mClipboardAndroidMgr;
	public Clipboard() {
		// mActivity = ModdingKit.getCurrentActivity();
		mSystemCtx = ModdingKit.getSystemContext();
		mClipboardAndroidMgr = (ClipboardManager) mSystemCtx.getSystemService(Context.CLIPBOARD_SERVICE);
	}
	
    public synchronized void setContents(final Transferable contents, ClipboardOwner owner) {
		new Handler().post(new Runnable(){
				@Override
				public void run() {
					ClipData clip = ClipData.newPlainText("Clipboard", ((StringSelection) contents).getString());
					mClipboardAndroidMgr.setPrimaryClip(clip);

					Toast.makeText(mSystemCtx, "Copied to clipboard!", Toast.LENGTH_SHORT).show();
				}
			});
    }

	private Transferable clipboardText;
	private boolean clipboardPasted = false;
	public synchronized Transferable getContents(Object requestor) {
        try {
			clipboardPasted = false;
			
			new Handler().post(new Runnable(){
					@Override
					public void run() {
						clipboardText = new StringSelection(mClipboardAndroidMgr.getText().toString());
						clipboardPasted = true;
						Toast.makeText(mSystemCtx, "Paste from clipboard", Toast.LENGTH_SHORT).show();
					}
				});
				
			while (!clipboardPasted) {
				Thread.sleep(100);
			}

			return clipboardText;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
