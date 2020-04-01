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
    public synchronized void setContents(final Transferable contents, ClipboardOwner owner) {
		try {
			final Activity act = ModdingKit.getCurrentActivity();
			act.runOnUiThread(new Runnable(){

				@Override
				public void run()
				{
					ClipboardManager clipboard = (ClipboardManager) act.getSystemService(Context.CLIPBOARD_SERVICE); 
					ClipData clip = ClipData.newPlainText("Copied text", ((StringSelection) contents).getString());
					clipboard.setPrimaryClip(clip);

					Toast.makeText(act, "Copied to clipboard!", Toast.LENGTH_SHORT).show();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
