package net.kdt.pojavlaunch.value.customcontrols;
import android.widget.*;
import android.content.*;
import android.util.*;
import android.view.*;
import com.google.gson.*;
import net.kdt.pojavlaunch.*;
import android.support.v7.app.*;

public class ControlsLayout extends FrameLayout
{
	private boolean mCanMove;
	private CustomControls mLayout;
	public ControlsLayout(Context ctx) {
		super(ctx);
	}
	
	public ControlsLayout(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
	}
	
	public void loadLayout(CustomControls controlLayout) {
		mLayout = controlLayout;
		for (ControlButton button : controlLayout.button) {
			final ControlView view = new ControlView(getContext(), button);
			view.setOnClickListener(new View.OnClickListener(){

					@Override
					public void onClick(View p1) {
						AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
						alert.setTitle(getResources().getString(R.string.global_edit) + " " + view.getText());
						// alert.setView(edit);
						alert.show();
					}
				});
			view.setCanMove(mCanMove);
			view.setLayoutParams(new LayoutParams((int) Tools.dpToPx(getContext(), 50), (int) Tools.dpToPx(getContext(), 50)));
			addView(view);
		}
	}
	
	public void refreshLayout(/* CustomControls controlLayout */) {
		removeAllViews();
		loadLayout(mLayout);
		
		// loadLayout(controlLayout);
	}
	
	public void saveLayout(String path) throws Exception {
		Tools.write(path, new Gson().toJson(mLayout));
	}
	
	public void setCanMove(boolean z) {
		mCanMove = z;
		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			if (v instanceof ControlView) {
				((ControlView) v).setCanMove(z);
			}
		}
	}
}
