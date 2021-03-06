package net.lxzhu.todaily.dao;

import java.util.ArrayList;
import java.util.List;

import net.lxzhu.todaily.util.Linq;
import net.lxzhu.todaily.util.SelectDelegate;
import net.lxzhu.todaily.util.ToastUtil;
import android.content.Context;
import android.database.Cursor;

public class SqlDataContext {
	protected Context context;
	protected SqliteDB sqlite;

	public SqlDataContext(Context context) {
		this.context = context;
		SqliteDB.loadSqlScripts(context);
		this.sqlite = new SqliteDB(this.context);
	}

	protected <T> T fetchFirst(String sql, Object[] args, DataBinder<T> binder) {

		String[] texts = new Linq<Object>(args).select(new SelectDelegate<Object, String>() {

			@Override
			public String exec(Object data) {
				if (null == data) {
					return "";
				} else {
					return data.toString();
				}
			}
		}).toArray(new String[0]);

		return fetchFirst(sql, texts, binder);
	}

	protected <T> T fetchFirst(String sql, String[] args, DataBinder<T> binder) {
		T retObject = null;
		Cursor cursor = this.sqlite.getReadableDatabase().rawQuery(sql, args);
		binder.init(cursor);
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			try {
				retObject = binder.getDataObject(cursor);
			} catch (Exception e) {
				e.printStackTrace();
				ToastUtil.showText(this.context, e.getLocalizedMessage());
			}
			cursor.close();
		}
		return retObject;
	}

	protected <T> List<T> fetchList(String sql, String[] args, DataBinder<T> binder) {
		List<T> retList = new ArrayList<T>();
		Cursor cursor = this.sqlite.getReadableDatabase().rawQuery(sql, args);
		binder.init(cursor);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			try {
				T dataObject = binder.getDataObject(cursor);
				if (dataObject != null) {
					retList.add(dataObject);
				}
			} catch (Exception e) {
				e.printStackTrace();
				ToastUtil.showText(this.context, e.getLocalizedMessage());
			}
			cursor.moveToNext();
		}
		return retList;
	}

	protected long fetchFirstLong(String sql, Object[] args) {
		return fetchFirst(sql, args, new DataBinder<Long>() {

			@Override
			public void init(Cursor cursor) {
				// TODO Auto-generated method stub

			}

			@Override
			public Long getDataObject(Cursor cursor) {
				return cursor.getLong(0);
			}

		});
	}

	protected double fetchFirstDouble(String sql, Object[] args) {
		return fetchFirst(sql, args, new DataBinder<Double>() {

			@Override
			public void init(Cursor cursor) {
				// TODO Auto-generated method stub

			}

			@Override
			public Double getDataObject(Cursor cursor) {
				return cursor.getDouble(0);
			}
		});
	}

	protected byte[] fetchFirstBlob(String sql, Object[] args) {
		return fetchFirst(sql, args, new DataBinder<byte[]>() {
			@Override
			public void init(Cursor cursor) {
				// TODO Auto-generated method stub

			}

			@Override
			public byte[] getDataObject(Cursor cursor) {
				return cursor.getBlob(0);
			}
		});
	}

	protected String fetchFirstString(String sql, Object[] args) {
		return fetchFirst(sql, args, new DataBinder<String>() {
			@Override
			public void init(Cursor cursor) {
				// TODO Auto-generated method stub

			}

			@Override
			public String getDataObject(Cursor cursor) {
				return cursor.getString(0);
			}
		});
	}
}
