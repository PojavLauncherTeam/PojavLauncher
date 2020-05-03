/**
 * Copyright (c) 2010 Michael A. MacDonald
 */
package android.androidVNC;

import com.antlersoft.android.db.FieldAccessor;
import com.antlersoft.android.db.TableInterface;
/**
 * Interface specification for table storing sent text; the last N text items sent are stored in a table
 * and will be recalled on demand
 * 
 * @author Michael A. MacDonald
 *
 */
@TableInterface(TableName="SENT_TEXT",ImplementingIsAbstract=false,ImplementingClassName="SentTextBean")
public interface ISentText {
	@FieldAccessor
	long get_Id();
	@FieldAccessor
	String getSentText();
}
