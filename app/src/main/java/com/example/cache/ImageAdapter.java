package com.example.cache;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private static final String TAG = "ImageAdapter";
    private List<String> mImageUrlList;
    private Context mContext;
    private boolean mIsFling;

    public ImageAdapter(Context context, int resource, List<String> objects) {
        mImageUrlList = objects;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mImageUrlList.size();
    }

    @Override
    public Object getItem(int position) {
        return mImageUrlList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setFling(boolean isFling) {
        Log.e("ppppp", String.valueOf(isFling));
        mIsFling = isFling;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.item, null);
            viewHolder = new viewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_item);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.number);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (viewHolder) convertView.getTag();
        }
//        if (!mIsFling){
        viewHolder.imageView.setImageResource(R.drawable.grey);
        viewHolder.textView.setText(String.valueOf(position));
        long beginTime = System.currentTimeMillis();
        ImageUtil.with(mContext).load(mImageUrlList.get(position)).into(viewHolder.imageView);
        long endTime = System.currentTimeMillis();
//        } else {
//            viewHolder.textView.setText(String.valueOf(position + 1));
//            viewHolder.imageView.setImageResource(R.drawable.grey);
//            getFinalView(position, convertView, parent);
//        }
        Log.d(TAG, "hhh图片大小：" + ImageUtil.with(mContext).getBitmapSize());
        //Log.d(TAG, "内存图片命中率：" + String.valueOf(ImageUtil.with(mContext).getHitRate()));
        Log.d(TAG, "hhh总访问时间：" + String.valueOf(ImageUtil.with(mContext).getTotalTime()) + "ms");
        return convertView;
    }


    class viewHolder{
        ImageView imageView;
        TextView textView;
    }

    public View getFinalView(int position, View convertView, ViewGroup parent) {
        if (convertView.getTag() != null) {
            viewHolder viewHolder = (viewHolder) convertView.getTag();
            ImageUtil.with(mContext).load(mImageUrlList.get(position)).into(viewHolder.imageView);
        }
        return convertView;
    }
}
