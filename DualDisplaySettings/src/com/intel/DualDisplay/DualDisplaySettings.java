package com.intel.DualDisplay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Filter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

public class DualDisplaySettings extends ListActivity implements OnClickListener{
      PackageManager mPackageManager;
      IconResizer mIconResizer;
      Intent mIntent;
      ActivityAdapter mAdapter;
      public String mStoredAppName;
      ListView listView;
      public Button btn_ok;
      public Button btn_cancel;
     /**
     * An item in the list
     */
    public static class ListItem {
        public ResolveInfo resolveInfo;
        public CharSequence label;
        public Drawable icon;
        public String packageName;
        public String className;
        public Bundle extras;
        
        ListItem(PackageManager pm, ResolveInfo resolveInfo, IconResizer resizer) {
            this.resolveInfo = resolveInfo;
            label = resolveInfo.loadLabel(pm);
            ComponentInfo ci = resolveInfo.activityInfo;
            if (ci == null) ci = resolveInfo.serviceInfo;
            if (label == null && ci != null) {
                label = resolveInfo.activityInfo.name;
            }
            
            if (resizer != null) {
                icon = resizer.createIconThumbnail(resolveInfo.loadIcon(pm));
            }
            packageName = ci.applicationInfo.packageName;
            className = ci.name;
        }

        public ListItem() {
        }
     	}
	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mStoredAppName = Settings.System.getStringForUser(getContentResolver(),
                Settings.System.APP_DISPLAY_ON_EXTERNAL,UserHandle.USER_CURRENT);
		btn_ok = (Button)findViewById(R.id.btn_ok);
		btn_cancel = (Button)findViewById(R.id.btn_cancel);
		btn_ok.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		mPackageManager = getPackageManager();
		mIconResizer = new IconResizer();
		mIntent = new Intent();
		mIntent.setAction(Intent.ACTION_MAIN);
		mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		
		mAdapter = new ActivityAdapter(mIconResizer,this);
		setListAdapter(mAdapter);

		listView = getListView();
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	}

	public void setItemChecked(int position){
             listView.setItemChecked(position,true);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}


   private class ActivityAdapter extends BaseAdapter {
        private final Object lock = new Object();
        private ArrayList<ListItem> mOriginalValues;

        protected final IconResizer mIconResizer;
        protected final LayoutInflater mInflater;

        protected List<ListItem> mActivitiesList;

        private Filter mFilter;
        private final boolean mShowIcons;

	 private DualDisplaySettings mOwner;
        
        public ActivityAdapter(IconResizer resizer,DualDisplaySettings activity) {
            mIconResizer = resizer;
	        mOwner = activity;
            mInflater = (LayoutInflater)DualDisplaySettings. this.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            mShowIcons = true;
            mActivitiesList = makeListItems();
        }
        
        public int getCount() {
            return mActivitiesList != null ? mActivitiesList.size() : 0;
        }

        public Object getItem(int position) {
            return mActivitiesList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = mInflater.inflate(
                        com.android.internal.R.layout.simple_list_item_multiple_choice, parent, false);
            } else {
                view = convertView;
            }
	     ListItem item = mActivitiesList.get(position);
            bindView(view, item);
	     if(mOwner.mStoredAppName != null && !mOwner.mStoredAppName.isEmpty() && mOwner.mStoredAppName.contains(item.packageName)){ 
	           mOwner.setItemChecked(position);
	     }
            return view;
        }

        private void bindView(View view, ListItem item) {
            TextView text = (TextView) view;
            text.setText(item.label);
            if (mShowIcons) {
                if (item.icon == null) {
                    item.icon = mIconResizer.createIconThumbnail(item.resolveInfo.loadIcon(getPackageManager()));
                }
                text.setCompoundDrawablesWithIntrinsicBounds(item.icon, null, null, null);
            }
        }
       
	  public boolean hasStableIds(){
               return true;
	  }
	  
    }

       /**
     * Utility class to resize icons to match default icon size.  
     */
    public class IconResizer {
        // Code is borrowed from com.android.launcher.Utilities. 
        private int mIconWidth = -1;
        private int mIconHeight = -1;

        private final Rect mOldBounds = new Rect();
        private Canvas mCanvas = new Canvas();
        
        public IconResizer() {
            mCanvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.DITHER_FLAG,
                    Paint.FILTER_BITMAP_FLAG));
            
            final Resources resources = DualDisplaySettings.this.getResources();
            mIconWidth = mIconHeight = (int) resources.getDimension(
                    android.R.dimen.app_icon_size);
        }

        /**
         * Returns a Drawable representing the thumbnail of the specified Drawable.
         * The size of the thumbnail is defined by the dimension
         * android.R.dimen.launcher_application_icon_size.
         *
         * This method is not thread-safe and should be invoked on the UI thread only.
         *
         * @param icon The icon to get a thumbnail of.
         *
         * @return A thumbnail for the specified icon or the icon itself if the
         *         thumbnail could not be created. 
         */
        public Drawable createIconThumbnail(Drawable icon) {
            int width = mIconWidth;
            int height = mIconHeight;

            final int iconWidth = icon.getIntrinsicWidth();
            final int iconHeight = icon.getIntrinsicHeight();

            if (icon instanceof PaintDrawable) {
                PaintDrawable painter = (PaintDrawable) icon;
                painter.setIntrinsicWidth(width);
                painter.setIntrinsicHeight(height);
            }

            if (width > 0 && height > 0) {
                if (width < iconWidth || height < iconHeight) {
                    final float ratio = (float) iconWidth / iconHeight;

                    if (iconWidth > iconHeight) {
                        height = (int) (width / ratio);
                    } else if (iconHeight > iconWidth) {
                        width = (int) (height * ratio);
                    }

                    final Bitmap.Config c = icon.getOpacity() != PixelFormat.OPAQUE ?
                                Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
                    final Bitmap thumb = Bitmap.createBitmap(mIconWidth, mIconHeight, c);
                    final Canvas canvas = mCanvas;
                    canvas.setBitmap(thumb);
                    // Copy the old bounds to restore them later
                    // If we were to do oldBounds = icon.getBounds(),
                    // the call to setBounds() that follows would
                    // change the same instance and we would lose the
                    // old bounds
                    mOldBounds.set(icon.getBounds());
                    final int x = (mIconWidth - width) / 2;
                    final int y = (mIconHeight - height) / 2;
                    icon.setBounds(x, y, x + width, y + height);
                    icon.draw(canvas);
                    icon.setBounds(mOldBounds);
                    icon = new BitmapDrawable(getResources(), thumb);
                    canvas.setBitmap(null);
                } else if (iconWidth < width && iconHeight < height) {
                    final Bitmap.Config c = Bitmap.Config.ARGB_8888;
                    final Bitmap thumb = Bitmap.createBitmap(mIconWidth, mIconHeight, c);
                    final Canvas canvas = mCanvas;
                    canvas.setBitmap(thumb);
                    mOldBounds.set(icon.getBounds());
                    final int x = (width - iconWidth) / 2;
                    final int y = (height - iconHeight) / 2;
                    icon.setBounds(x, y, x + iconWidth, y + iconHeight);
                    icon.draw(canvas);
                    icon.setBounds(mOldBounds);
                    icon = new BitmapDrawable(getResources(), thumb);
                    canvas.setBitmap(null);
                }
            }

            return icon;
        }
    }


/**
 * Perform query on package manager for list items.  The default
 * implementation queries for activities.
 */
protected List<ResolveInfo> onQueryPackageManager(Intent queryIntent) {
	return mPackageManager.queryIntentActivities(queryIntent, /* no flags */ 0);
}

/**
 * @hide
 */
protected void onSortResultList(List<ResolveInfo> results) {
	Collections.sort(results, new ResolveInfo.DisplayNameComparator(mPackageManager));
}

/**
 * Perform the query to determine which results to show and return a list of them.
 */
public List<ListItem> makeListItems() {
	// Load all matching activities and sort correctly
	List<ResolveInfo> list = onQueryPackageManager(mIntent);
	onSortResultList(list);

	ArrayList<ListItem> result = new ArrayList<ListItem>(list.size());
	int listSize = list.size();
	for (int i = 0; i < listSize; i++) {
		ResolveInfo resolveInfo = list.get(i);
		result.add(new ListItem(mPackageManager, resolveInfo, null));
	}

	return result;
}

@Override
public void onClick(View arg0) {
	// TODO Auto-generated method stub
	if(btn_ok == arg0){
	   mStoredAppName = "";
	   long[] selectedId = listView.getCheckItemIds();
	   for(int n = 0;n<selectedId.length;n++){
	   	ListItem item = (ListItem)mAdapter.getItem((int)selectedId[n]);
		mStoredAppName = mStoredAppName + item.packageName + ":";
		
	   }
          Settings.System.putString(getContentResolver(),
                Settings.System.APP_DISPLAY_ON_EXTERNAL,mStoredAppName);
	   finish();
	}else if(btn_cancel == arg0){
		finish();
	}
	
}

}
