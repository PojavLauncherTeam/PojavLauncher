package net.kdt.pojavlaunch;

import android.content.*;
import android.util.*;
import android.view.*;

public class MinecraftGLView extends TextureView
{
    public MinecraftGLView(Context context) {
        this(context, null);
    }

    public MinecraftGLView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        //Fixes freeform and dex mode having transparent glass,
        //since it forces android to used the background color of the view/layout behind it.
        setOpaque(false);
        setFocusable(true);
    }
}
