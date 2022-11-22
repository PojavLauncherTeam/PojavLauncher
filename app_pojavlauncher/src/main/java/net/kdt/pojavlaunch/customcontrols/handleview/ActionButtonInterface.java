package net.kdt.pojavlaunch.customcontrols.handleview;

import android.view.View;

import net.kdt.pojavlaunch.customcontrols.buttons.ControlInterface;

/** Interface defining the behavior of action buttons */
public interface ActionButtonInterface extends View.OnClickListener {

    /** HAS TO BE CALLED BY THE CONSTRUCTOR */
    void init();

    /** Called when the button should be made aware of the current target */
    void setFollowedView(ControlInterface view);

    /** Called when the button action should be executed on the target */
    void onClick();

    /** Whether the button should be shown, given the current contextual information that it has */
    boolean shouldBeVisible();

    @Override  // Wrapper to remove the arg
    default void onClick(View v){onClick();}
}
