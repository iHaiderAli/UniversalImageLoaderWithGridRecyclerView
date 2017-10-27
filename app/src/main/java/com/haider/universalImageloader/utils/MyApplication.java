package com.haider.universalImageloader.utils;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.os.Build;

import com.haider.universalImageloader.imageChahelib.cache.disc.naming.Md5FileNameGenerator;
import com.haider.universalImageloader.imageChahelib.core.ImageLoader;
import com.haider.universalImageloader.imageChahelib.core.ImageLoaderConfiguration;
import com.haider.universalImageloader.imageChahelib.core.assist.QueueProcessingType;


public class MyApplication extends Application {
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressWarnings("unused")
	@Override
	public void onCreate() {
		super.onCreate();

		initImageLoader(getApplicationContext());
	}

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.denyCacheImageMultipleSizesInMemory();
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		config.writeDebugLogs(); // Remove for release app

		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config.build());
	}
}