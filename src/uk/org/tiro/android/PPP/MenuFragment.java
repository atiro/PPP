package uk.org.tiro.android.PPP;

import android.os.Bundle;

import android.content.Context;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.content.res.TypedArray;

import android.widget.Gallery;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ImageView;
import android.widget.CursorAdapter;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView.OnItemClickListener;

import android.util.Log;


public class MenuFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
				 Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.menu_fragment, container, false);
		final Context c = getActivity().getApplicationContext();

		Gallery gallery = (Gallery) v.findViewById(R.id.menu);
		gallery.setAdapter(new ImageAdapter(c));

		gallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View v,
				int pos, long id) {
					Toast.makeText(c, "Switch screen", Toast.LENGTH_SHORT).show();
				}
		});

		return v;
	}

	public class ImageAdapter extends BaseAdapter {
	    int mGalleryItemBackground;
    	    private Context mContext;

//            private Integer[] mImageIds = {
 //           R.drawable.menu_diary,
  //          R.drawable.menu_notices,
   //         R.drawable.menu_watching,
    //        R.drawable.menu_bills,
     //       R.drawable.menu_acts,
      //      R.drawable.menu_about
    //	    };
            private Integer[] mImageIds = {
         	   R.drawable.menu_diary3,
         	   R.drawable.menu_diary2,
         	   R.drawable.menu_diary,
    	    };

 	   public ImageAdapter(Context c) {
        	mContext = c;
        	TypedArray attr = mContext.obtainStyledAttributes(R.styleable.Menu);
        	mGalleryItemBackground = attr.getResourceId(
        	        R.styleable.Menu_android_galleryItemBackground, 0);
        	attr.recycle();
    	}

 	   public int getCount() {
 	       return mImageIds.length;
 	   }

 	   public Object getItem(int position) {
 	       return position;
 	   }

 	   public long getItemId(int position) {
 	       return position;
 	   }

 	   public View getView(int position, View convertView, ViewGroup parent) {
 	       ImageView imageView = new ImageView(mContext);

 	       imageView.setImageResource(mImageIds[position]);
 	       imageView.setLayoutParams(new Gallery.LayoutParams(80, 80));
 	       imageView.setScaleType(ImageView.ScaleType.FIT_XY);
 	       imageView.setBackgroundResource(mGalleryItemBackground);

 	       return imageView;
 	   }
	}	

}


