/**
 * Copyright (C) 2009 Michael A. MacDonald
 */
package android.androidVNC;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ImageView.ScaleType;

import com.antlersoft.android.dbimpl.NewInstance;

import java.lang.Comparable;

/**
 * @author Michael A. MacDonald
 *
 */
class ConnectionBean extends AbstractConnectionBean implements Comparable<ConnectionBean> {
	static final NewInstance<ConnectionBean> newInstance=new NewInstance<ConnectionBean>() {
		public ConnectionBean get() { return new ConnectionBean(); }
	};
	ConnectionBean()
	{
		set_Id(0);
		setAddress("");
		setPassword("");
		setKeepPassword(true);
		setNickname("");
		setUserName("");
		setPort(5900);
		setColorModel(COLORMODEL.C64.nameString());
		setScaleMode(ScaleType.MATRIX);
		setInputMode(VncCanvasActivity.TOUCH_ZOOM_MODE);
		setRepeaterId("");
		setMetaListId(1);
	}
	
	boolean isNew()
	{
		return get_Id()== 0;
	}
	
	void save(SQLiteDatabase database) {
		ContentValues values=Gen_getValues();
		values.remove(GEN_FIELD__ID);
		if ( ! getKeepPassword()) {
			values.put(GEN_FIELD_PASSWORD, "");
		}
		if ( isNew()) {
			set_Id(database.insert(GEN_TABLE_NAME, null, values));
		} else {
			database.update(GEN_TABLE_NAME, values, GEN_FIELD__ID + " = ?", new String[] { Long.toString(get_Id()) });
		}
	}
	
	ScaleType getScaleMode()
	{
		return ScaleType.valueOf(getScaleModeAsString());
	}
	
	void setScaleMode(ScaleType value)
	{
		setScaleModeAsString(value.toString());
	}
	
	@Override
	public String toString() {
		if ( isNew())
		{
			return "New";
		}
		return getNickname()+":"+getAddress()+":"+getPort();
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ConnectionBean another) {
		int result = getNickname().compareTo(another.getNickname());
		if (result == 0) {
			result = getAddress().compareTo(another.getAddress());
			if ( result == 0) {
				result = getPort() - another.getPort();
			}
		}
		return result;
	}
}
