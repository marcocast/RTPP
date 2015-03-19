package com.rtpp.rtpp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by marcocastigliego on 12/03/2015.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return mThumbIds[position];
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    // references to our images
    public static Integer[] mThumbIds = {
            R.drawable.card0, R.drawable.cardhalf,
            R.drawable.card1, R.drawable.card2,
            R.drawable.card3, R.drawable.card5,
            R.drawable.card8, R.drawable.card13,
            R.drawable.card20, R.drawable.card40,
            R.drawable.card100, R.drawable.cardinfinite,
            R.drawable.cardquestion, R.drawable.cardcoffee
    };
}