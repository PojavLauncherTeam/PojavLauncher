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
@TableInterface(TableName="META_LIST",ImplementingIsAbstract=false,ImplementingClassName="MetaList")
public interface IMetaList {
	@FieldAccessor
	long get_Id();
	@FieldAccessor
	String getName();
}
