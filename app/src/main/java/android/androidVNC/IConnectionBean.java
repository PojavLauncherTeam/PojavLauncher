/**
 * Copyright (C) 2009 Michael A. MacDonald
 */
package android.androidVNC;

import com.antlersoft.android.db.*;

/**
 * @author Michael A. MacDonald
 *
 */
@TableInterface(ImplementingClassName="AbstractConnectionBean",TableName="CONNECTION_BEAN")
interface IConnectionBean {
	@FieldAccessor
	long get_Id();
	@FieldAccessor
	String getNickname();
	@FieldAccessor
	String getAddress();
	@FieldAccessor
	int getPort();
	@FieldAccessor
	String getPassword();
	@FieldAccessor
	String getColorModel();
	/**
	 * Records bitmap data implementation selection.  0 for auto, 1 for force full bitmap, 2 for force tiled
	 * <p>
	 * For historical reasons, this is named as if it were just a boolean selection for auto and force full.
	 * @return 0 for auto, 1 for force full bitmap, 2 for forced tiled
	 */
	@FieldAccessor
	long getForceFull();
	@FieldAccessor
	String getRepeaterId();
	@FieldAccessor
	String getInputMode();
	@FieldAccessor(Name="SCALEMODE")
	String getScaleModeAsString();
	@FieldAccessor
	boolean getUseLocalCursor();
	@FieldAccessor
	boolean getKeepPassword();
	@FieldAccessor
	boolean getFollowMouse();
	@FieldAccessor
	boolean getUseRepeater();
	@FieldAccessor
	long getMetaListId();
	@FieldAccessor(Name="LAST_META_KEY_ID")
	long getLastMetaKeyId();
	@FieldAccessor(DefaultValue="false")
	boolean getFollowPan();
	@FieldAccessor
	String getUserName();
	@FieldAccessor
	String getSecureConnectionType();
	@FieldAccessor(DefaultValue="true")
	boolean getShowZoomButtons();
	@FieldAccessor(Name="DOUBLE_TAP_ACTION")
	String getDoubleTapActionAsString();
}
