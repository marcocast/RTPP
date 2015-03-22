package com.rtpp.rtpp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by marcocastigliego on 12/03/2015.
 */
public class ImageAdapter extends BaseAdapter {

    public static Map<String, Integer[]> cardsPerCardType = new HashMap<String, Integer[]>();


    // references to our images
    public static Integer[] standard = {
            R.drawable.card0, R.drawable.cardhalf,
            R.drawable.card1, R.drawable.card2,
            R.drawable.card3, R.drawable.card5,
            R.drawable.card8, R.drawable.card13,
            R.drawable.card20, R.drawable.card40,
            R.drawable.card100, R.drawable.cardinfinite,
            R.drawable.cardquestion, R.drawable.cardcoffee
    };


    // references to our images
    public static Integer[] fibonacci = {
            R.drawable.card0,
            R.drawable.card1, R.drawable.card2,
            R.drawable.card3, R.drawable.card5,
            R.drawable.card8, R.drawable.card13,
            R.drawable.card21, R.drawable.card34,
            R.drawable.card55, R.drawable.card89,
            R.drawable.card144,R.drawable.cardinfinite,
            R.drawable.cardquestion, R.drawable.cardcoffee
    };


    // references to our images
    public static Integer[] tshirt = {
            R.drawable.cardxs,
            R.drawable.cards, R.drawable.cardm,
            R.drawable.cardl, R.drawable.cardxl,
            R.drawable.cardxxl,R.drawable.cardinfinite,
            R.drawable.cardquestion, R.drawable.cardcoffee
    };

    static {
        Map<String, Integer[]> tempCardsPerCardType = new HashMap<String, Integer[]>();
        tempCardsPerCardType.put("Standard", standard);
        tempCardsPerCardType.put("Fibonacci", fibonacci);
        tempCardsPerCardType.put("T-Shirt", tshirt);

        cardsPerCardType = Collections.unmodifiableMap(tempCardsPerCardType);
    }


    private final Context mContext;
    private final String cardsType;

    public ImageAdapter(Context mContext, String cardsType) {
        this.mContext = mContext;
        this.cardsType = cardsType;
    }

    public int getCount() {
        return cardsPerCardType.get(cardsType).length;
    }

    public Object getItem(int position) {
        return cardsPerCardType.get(cardsType)[position];
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

        imageView.setImageResource(cardsPerCardType.get(cardsType)[position]);
        return imageView;
    }


}