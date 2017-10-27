package com.haider.universalImageloader.adapters;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.haider.universalImageloader.Model.UserModel;
import com.haider.universalImageloader.R;
import com.haider.universalImageloader.imageChahelib.core.DisplayImageOptions;
import com.haider.universalImageloader.imageChahelib.core.ImageLoader;
import com.haider.universalImageloader.imageChahelib.core.display.FadeInBitmapDisplayer;
import com.haider.universalImageloader.imageChahelib.core.listener.ImageLoadingListener;
import com.haider.universalImageloader.imageChahelib.core.listener.SimpleImageLoadingListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Haider 5th on 10/27/2017.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

    ArrayList<UserModel> usersList = new ArrayList<>();
    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView userImage;

        public MyViewHolder(View itemView) {
            super(itemView);
            userImage=(ImageView)itemView.findViewById(R.id.userImage);
        }
    }

    public UserAdapter(ArrayList<UserModel> usersList) {

        this.usersList = usersList;

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.placeholder_icon)
                .showImageForEmptyUri(R.drawable.empty_icon)
                .showImageOnFail(R.drawable.error_icon)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        UserModel user = usersList.get(position);
        ImageLoader.getInstance().displayImage(user.getImageUrl(), holder.userImage, options, animateFirstListener);

    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    @Override
    public long getItemId(int position) {
        return (position);
    }

}
