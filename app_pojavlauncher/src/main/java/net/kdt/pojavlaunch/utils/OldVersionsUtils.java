package net.kdt.pojavlaunch.utils;

import android.util.Log;

import net.kdt.pojavlaunch.JMinecraftVersionList;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.extra.ExtraConstants;
import net.kdt.pojavlaunch.extra.ExtraCore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/** Class here to help with various stuff to help run lower versions smoothly */
public class OldVersionsUtils {
    /** Lower minecraft versions fare better with opengl 1
     * @param version The version about to be launched
     */
    public static void selectOpenGlVersion(JMinecraftVersionList.Version version){
        // 1309989600 is 2011-07-07  2011-07-07T22:00:00+00:00
        String creationDate = version.time;
        if(!Tools.isValidString(creationDate)){
            ExtraCore.setValue(ExtraConstants.OPEN_GL_VERSION, "2");
            return;
        }

        try {
            int tIndexOf = creationDate.indexOf('T');
            if(tIndexOf != -1) creationDate = creationDate.substring(0, tIndexOf);
            Date creationDateObj = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(creationDate);
            if(creationDateObj == null) {
                Log.e("GL_SELECT", "Failed to parse version date");
                ExtraCore.setValue(ExtraConstants.OPEN_GL_VERSION, "2");
                return;
            }

            String openGlVersion =  creationDateObj.before(new Date(new GregorianCalendar(2011, 6, 8).getTimeInMillis())) ? "1" : "2";
            Log.i("GL_SELECT", openGlVersion);
            ExtraCore.setValue(ExtraConstants.OPEN_GL_VERSION, openGlVersion);
        }catch (ParseException exception){
            Log.e("GL_SELECT", exception.toString());
            ExtraCore.setValue(ExtraConstants.OPEN_GL_VERSION, "2");
        }
    }
}
