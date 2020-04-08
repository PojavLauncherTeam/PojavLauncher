package java.awt.datatransfer;

import android.app.Activity;
import android.content.Context;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.widget.Toast;

import net.kdt.pojavlaunch.Tools;
import java.awt.mod.*;

public class Clipboard extends Object
{
	private final Activity mActivity;
	public Clipboard() {
		mActivity = ModdingKit.getCurrentActivity();
	}
	
    public synchronized void setContents(final Transferable contents, ClipboardOwner owner) {
		try {
			mActivity.runOnUiThread(new Runnable(){

				@Override
				public void run()
				{
					ClipboardManager clipboard = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE); 
					ClipData clip = ClipData.newPlainText("Minecraft", ((StringSelection) contents).getString());
					clipboard.setPrimaryClip(clip);

					Toast.makeText(mActivity, "Copied to clipboard!", Toast.LENGTH_SHORT).show();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	private Transferable clipboardText;
	private boolean clipboardPasted = false;
	public synchronized Transferable getContents(Object requestor) {
        try {
			clipboardPasted = false;
			
			mActivity.runOnUiThread(new Runnable(){

					@Override
					public void run()
					{
						ClipboardManager clipboard = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE); 
						clipboardText = new StringSelection(clipboard.getText().toString());
						clipboardPasted = true;
						Toast.makeText(mActivity, "Paste from clipboard", Toast.LENGTH_SHORT).show();
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
