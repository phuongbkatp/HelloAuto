package com.appian.manchesterunitednews.app.detailnews;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appian.manchesterunitednews.R;
import com.appian.manchesterunitednews.util.ImageLoader;
import com.appian.manchesterunitednews.util.PreviewVideoLoadingTask;
import com.appian.manchesterunitednews.util.Utils;
import com.appnet.android.ads.widget.FbAdRecyclerAdapter;
import com.appnet.android.football.fbvn.data.ContentDetailNewsAuto;
import com.appnet.android.football.fbvn.data.DetailNewsAuto;
import com.appnet.android.football.fbvn.data.NewsAuto;
import com.bumptech.glide.Glide;
import com.marcinmoskala.videoplayview.VideoPlayView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class DetailNewsRecycleAdapter extends FbAdRecyclerAdapter<ContentDetailNewsAuto> {
    private static final int TEXT_VIEW_TYPE = 1;
    private static final int IMAGE_VIEW_TYPE = 2;
    private static final int VIDEO_VIEW_TYPE = 3;
    private static final int COLLUMN_VIEW_TYPE = 4;
    private static final int MAX_FB_ADS = 0;

    private VideoPlayView mVideoView;
    DetailNewsRecycleAdapter(Context context, String unitId) {
        super(context, unitId, MAX_FB_ADS);
    }

    @Override
    protected int getViewType(int position) {
        ContentDetailNewsAuto news = getItem(position);
        if (news == null) {
            return 0;
        }
        switch (news.getType()) {
            case "text":
                return TEXT_VIEW_TYPE;
            case "image":
                return IMAGE_VIEW_TYPE;
            case "video":
                return VIDEO_VIEW_TYPE;
            case "table":
                return COLLUMN_VIEW_TYPE;
            default:
                return TEXT_VIEW_TYPE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TEXT_VIEW_TYPE:
                view = LayoutInflater.from(mContext).inflate(R.layout.custom_text__view, parent, false);

                return new TextViewHolder(view);
            case IMAGE_VIEW_TYPE:
                view = LayoutInflater.from(mContext).inflate(R.layout.custom_image_layout, parent, false);

                return new ImageViewHolder(view);
            case VIDEO_VIEW_TYPE:
                view = LayoutInflater.from(mContext).inflate(R.layout.custom_video_layout, parent, false);

                return new VideoViewHolder(view);
            case COLLUMN_VIEW_TYPE:
/*                view = LayoutInflater.from(mContext).inflate(R.layout.item_news_topic, parent, false);

                return new RecyclerViewHolder(view);*/
            default:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_news_small, parent, false);

                return new TextViewHolder(view);
        }
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        ContentDetailNewsAuto item = getItem(position);
        if (item == null) {
            return;
        }
        if (getViewType(position) == TEXT_VIEW_TYPE) {
            TextViewHolder itemHolder = (TextViewHolder) holder;
            itemHolder.mContent.setText(item.getText());

        } else if (getViewType(position) == IMAGE_VIEW_TYPE) {
            ImageViewHolder itemHolder = (ImageViewHolder) holder;
            itemHolder.textView.setText(item.getText());
            ImageLoader.displayImage(item.getLinkImg(), itemHolder.imageView);
        } else if (getViewType(position) == VIDEO_VIEW_TYPE) {
            final VideoViewHolder itemHolder = (VideoViewHolder) holder;
            PreviewVideoLoadingTask task = new PreviewVideoLoadingTask(item.getLinkImg())
                    .setBitMapLoaded(new PreviewVideoLoadingTask.OnBitmapLoaded() {
                        @Override
                        public void loadBitmap(Bitmap bitmap) {
                            Glide.with(mContext).load(bitmap).into(itemHolder.videoView.getImageView());
                        }
                    });
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            itemHolder.videoView.setVideoUrl(item.getLinkImg());
        }

    }

    private class TextViewHolder extends RecyclerView.ViewHolder {
        TextView mContent;

        TextViewHolder(View view) {
            super(view);
            mContent = view.findViewById(R.id.content);
        }
    }

    private class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        ImageViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.image);
            textView = view.findViewById(R.id.txt_caption);
        }
    }

    private class VideoViewHolder extends RecyclerView.ViewHolder {
        VideoPlayView videoView;

        VideoViewHolder(View view) {
            super(view);
            videoView = view.findViewById(R.id.content_video);
        }
    }

}
