/**
 * Copyright (C) 2009 Michael A. MacDonald
 */
package android.androidVNC;

import com.antlersoft.android.db.FieldAccessor;
import com.antlersoft.android.db.TableInterface;

/**
 * @author Michael A. MacDonald
 *
 */
@TableInterface(TableName="META_KEY",ImplementingClassName="AbstractMetaKeyBean")
public interface IMetaKey {
	@FieldAccessor
	long get_Id();
	@FieldAccessor
	long getMetaListId();
	@FieldAccessor
	String getKeyDesc();
	@FieldAccessor
	int getMetaFlags();
	@FieldAccessor
	boolean isMouseClick();
	@FieldAccessor
	int getMouseButtons();
	@FieldAccessor
	int getKeySym();
	@FieldAccessor
	String getShortcut();
}
