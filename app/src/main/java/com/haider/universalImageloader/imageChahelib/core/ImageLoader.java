/*******************************************************************************
 * Copyright 2011-2014 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.haider.universalImageloader.imageChahelib.core;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.haider.universalImageloader.imageChahelib.cache.disc.DiskCache;
import com.haider.universalImageloader.imageChahelib.cache.memory.MemoryCache;
import com.haider.universalImageloader.imageChahelib.core.assist.ImageSize;
import com.haider.universalImageloader.imageChahelib.core.assist.LoadedFrom;
import com.haider.universalImageloader.imageChahelib.core.assist.ViewScaleType;
import com.haider.universalImageloader.imageChahelib.core.imageaware.ImageAware;
import com.haider.universalImageloader.imageChahelib.core.imageaware.ImageViewAware;
import com.haider.universalImageloader.imageChahelib.core.imageaware.NonViewAware;
import com.haider.universalImageloader.imageChahelib.core.listener.ImageLoadingListener;
import com.haider.universalImageloader.imageChahelib.core.listener.ImageLoadingProgressListener;
import com.haider.universalImageloader.imageChahelib.core.listener.SimpleImageLoadingListener;
import com.haider.universalImageloader.imageChahelib.utils.ImageSizeUtils;
import com.haider.universalImageloader.imageChahelib.utils.L;
import com.haider.universalImageloader.imageChahelib.utils.MemoryCacheUtils;

/**
 * Singletone for image loading and displaying at {@link ImageView ImageViews}<br />
 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before any other method.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 */
public class ImageLoader {

	public static final String TAG = ImageLoader.class.getSimpleName();

	static final String LOG_INIT_CONFIG = "Initialize ImageLoader with configuration";
	static final String LOG_DESTROY = "Destroy ImageLoader";
	static final String LOG_LOAD_IMAGE_FROM_MEMORY_CACHE = "Load image from memory cache [%s]";

	private static final String WARNING_RE_INIT_CONFIG = "Try to initialize ImageLoader which had already been initialized before. " + "To re-init ImageLoader with new configuration call ImageLoader.destroy() at first.";
	private static final String ERROR_WRONG_ARGUMENTS = "Wrong arguments were passed to displayImage() method (ImageView reference must not be null)";
	private static final String ERROR_NOT_INIT = "ImageLoader must be init with configuration before using";
	private static final String ERROR_INIT_CONFIG_WITH_NULL = "ImageLoader configuration can not be initialized with null";

	private ImageLoaderConfiguration configuration;
	private ImageLoaderEngine engine;

	private ImageLoadingListener defaultListener = new SimpleImageLoadingListener();

	private volatile static ImageLoader instance;

	/** Returns singleton class instance */
	public static ImageLoader getInstance() {
		if (instance == null) {
			synchronized (ImageLoader.class) {
				if (instance == null) {
					instance = new ImageLoader();
				}
			}
		}
		return instance;
	}

	protected ImageLoader() {
	}

	/**
	 * Initializes ImageLoader instance with configuration.<br />
	 * If configurations was set before ( {@link #isInited()} == true) then this method does nothing.<br />
	 * To force initialization with new configuration you should {@linkplain #destroy() destroy ImageLoader} at first.
	 *
	 * @param configuration {@linkplain ImageLoaderConfiguration ImageLoader configuration}
	 * @throws IllegalArgumentException if <b>configuration</b> parameter is null
	 */
	public synchronized void init(ImageLoaderConfiguration configuration) {
		if (configuration == null) {
			throw new IllegalArgumentException(ERROR_INIT_CONFIG_WITH_NULL);
		}
		if (this.configuration == null) {
			L.d(LOG_INIT_CONFIG);
			engine = new ImageLoaderEngine(configuration);
			this.configuration = configuration;
		} else {
			L.w(WARNING_RE_INIT_CONFIG);
		}
	}

	/**
	 * Returns <b>true</b> - if ImageLoader {@linkplain #init(ImageLoaderConfiguration) is initialized with
	 * configuration}; <b>false</b> - otherwise
	 */
	public boolean isInited() {
		return configuration != null;
	}

	/**
	 * Adds display image task to execution pool. Image will be set to ImageAware when it's turn. <br/>
	 * Default {@linkplain DisplayImageOptions display image options} from {@linkplain ImageLoaderConfiguration
	 * configuration} will be used.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 *
	 * @param uri        Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param imageAware {@linkplain com.nostra13.universalimageloader.core.imageaware.ImageAware Image aware view}
	 *                   which should display image
	 * @throws IllegalStateException    if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 * @throws IllegalArgumentException if passed <b>imageAware</b> is null
	 */
	public void displayImage(String uri, ImageAware imageAware) {
		displayImage(uri, imageAware, null, null, null);
	}

	/**
	 * Adds display image task to execution pool. Image will be set to ImageAware when it's turn.<br />
	 * Default {@linkplain DisplayImageOptions display image options} from {@linkplain ImageLoaderConfiguration
	 * configuration} will be used.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 *
	 * @param uri        Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param imageAware {@linkplain com.nostra13.universalimageloader.core.imageaware.ImageAware Image aware view}
	 *                   which should display image
	 * @param listener   {@linkplain ImageLoadingListener Listener} for image loading process. Listener fires events on
	 *                   UI thread if this method is called on UI thread.
	 * @throws IllegalStateException    if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 * @throws IllegalArgumentException if passed <b>imageAware</b> is null
	 */
	public void displayImage(String uri, ImageAware imageAware, ImageLoadingListener listener) {
		displayImage(uri, imageAware, null, listener, null);
	}


	public void displayImage(String uri, ImageAware imageAware, DisplayImageOptions options) {
		displayImage(uri, imageAware, options, null, null);
	}

	public void displayImage(String uri, ImageAware imageAware, DisplayImageOptions options,
                             ImageLoadingListener listener) {
		displayImage(uri, imageAware, options, listener, null);
	}
public void displayImage(String uri, ImageAware imageAware, DisplayImageOptions options,
                             ImageLoadingListener listener, ImageLoadingProgressListener progressListener) {
		displayImage(uri, imageAware, options, null, listener, progressListener);
	}

	public void displayImage(String uri, ImageAware imageAware, DisplayImageOptions options,
                             ImageSize targetSize, ImageLoadingListener listener, ImageLoadingProgressListener progressListener) {
		checkConfiguration();
		if (imageAware == null) {
			throw new IllegalArgumentException(ERROR_WRONG_ARGUMENTS);
		}
		if (listener == null) {
			listener = defaultListener;
		}
		if (options == null) {
			options = configuration.defaultDisplayImageOptions;
		}

		if (TextUtils.isEmpty(uri)) {
			engine.cancelDisplayTaskFor(imageAware);
			listener.onLoadingStarted(uri, imageAware.getWrappedView());
			if (options.shouldShowImageForEmptyUri()) {
				imageAware.setImageDrawable(options.getImageForEmptyUri(configuration.resources));
			} else {
				imageAware.setImageDrawable(null);
			}
			listener.onLoadingComplete(uri, imageAware.getWrappedView(), null);
			return;
		}

		if (targetSize == null) {
			targetSize = ImageSizeUtils.defineTargetSizeForView(imageAware, configuration.getMaxImageSize());
		}
		String memoryCacheKey = MemoryCacheUtils.generateKey(uri, targetSize);
		engine.prepareDisplayTaskFor(imageAware, memoryCacheKey);

		listener.onLoadingStarted(uri, imageAware.getWrappedView());

		Bitmap bmp = configuration.memoryCache.get(memoryCacheKey);
		if (bmp != null && !bmp.isRecycled()) {
			L.d(LOG_LOAD_IMAGE_FROM_MEMORY_CACHE, memoryCacheKey);

			if (options.shouldPostProcess()) {
				ImageLoadingInfo imageLoadingInfo = new ImageLoadingInfo(uri, imageAware, targetSize, memoryCacheKey,
						options, listener, progressListener, engine.getLockForUri(uri));
				ProcessAndDisplayImageTask displayTask = new ProcessAndDisplayImageTask(engine, bmp, imageLoadingInfo,
						defineHandler(options));
				if (options.isSyncLoading()) {
					displayTask.run();
				} else {
					engine.submit(displayTask);
				}
			} else {
				options.getDisplayer().display(bmp, imageAware, LoadedFrom.MEMORY_CACHE);
				listener.onLoadingComplete(uri, imageAware.getWrappedView(), bmp);
			}
		} else {
			if (options.shouldShowImageOnLoading()) {
				imageAware.setImageDrawable(options.getImageOnLoading(configuration.resources));
			} else if (options.isResetViewBeforeLoading()) {
				imageAware.setImageDrawable(null);
			}

			ImageLoadingInfo imageLoadingInfo = new ImageLoadingInfo(uri, imageAware, targetSize, memoryCacheKey,
					options, listener, progressListener, engine.getLockForUri(uri));
			LoadAndDisplayImageTask displayTask = new LoadAndDisplayImageTask(engine, imageLoadingInfo,
					defineHandler(options));
			if (options.isSyncLoading()) {
				displayTask.run();
			} else {
				engine.submit(displayTask);
			}
		}
	}

	public void displayImage(String uri, ImageView imageView) {
		displayImage(uri, new ImageViewAware(imageView), null, null, null);
	}
	public void displayImage(String uri, ImageView imageView, ImageSize targetImageSize) {
		displayImage(uri, new ImageViewAware(imageView), null, targetImageSize, null, null);
	}
	public void displayImage(String uri, ImageView imageView, DisplayImageOptions options) {
		displayImage(uri, new ImageViewAware(imageView), options, null, null);
	}

public void displayImage(String uri, ImageView imageView, ImageLoadingListener listener) {
		displayImage(uri, new ImageViewAware(imageView), null, listener, null);
	}
	public void displayImage(String uri, ImageView imageView, DisplayImageOptions options,
                             ImageLoadingListener listener) {
		displayImage(uri, imageView, options, listener, null);
	}

	public void displayImage(String uri, ImageView imageView, DisplayImageOptions options,
                             ImageLoadingListener listener, ImageLoadingProgressListener progressListener) {
		displayImage(uri, new ImageViewAware(imageView), options, listener, progressListener);
	}
	public void loadImage(String uri, ImageLoadingListener listener) {
		loadImage(uri, null, null, listener, null);
	}
	public void loadImage(String uri, ImageSize targetImageSize, ImageLoadingListener listener) {
		loadImage(uri, targetImageSize, null, listener, null);
	}
	public void loadImage(String uri, DisplayImageOptions options, ImageLoadingListener listener) {
		loadImage(uri, null, options, listener, null);
	}
	public void loadImage(String uri, ImageSize targetImageSize, DisplayImageOptions options,
                          SimpleImageLoadingListener listener) {
		loadImage(uri, targetImageSize, options, listener, null);
	}
	public void loadImage(String uri, ImageSize targetImageSize, DisplayImageOptions options,
                          ImageLoadingListener listener, ImageLoadingProgressListener progressListener) {
		checkConfiguration();
		if (targetImageSize == null) {
			targetImageSize = configuration.getMaxImageSize();
		}
		if (options == null) {
			options = configuration.defaultDisplayImageOptions;
		}

		NonViewAware imageAware = new NonViewAware(uri, targetImageSize, ViewScaleType.CROP);
		displayImage(uri, imageAware, options, listener, progressListener);
	}

	public Bitmap loadImageSync(String uri) {
		return loadImageSync(uri, null, null);
	}

	public Bitmap loadImageSync(String uri, DisplayImageOptions options) {
		return loadImageSync(uri, null, options);
	}
	public Bitmap loadImageSync(String uri, ImageSize targetImageSize) {
		return loadImageSync(uri, targetImageSize, null);
	}

	public Bitmap loadImageSync(String uri, ImageSize targetImageSize, DisplayImageOptions options) {
		if (options == null) {
			options = configuration.defaultDisplayImageOptions;
		}
		options = new DisplayImageOptions.Builder().cloneFrom(options).syncLoading(true).build();

		SyncImageLoadingListener listener = new SyncImageLoadingListener();
		loadImage(uri, targetImageSize, options, listener);
		return listener.getLoadedBitmap();
	}

	private void checkConfiguration() {
		if (configuration == null) {
			throw new IllegalStateException(ERROR_NOT_INIT);
		}
	}

	public void setDefaultLoadingListener(ImageLoadingListener listener) {
		defaultListener = listener == null ? new SimpleImageLoadingListener() : listener;
	}
	public MemoryCache getMemoryCache() {
		checkConfiguration();
		return configuration.memoryCache;
	}

	public void clearMemoryCache() {
		checkConfiguration();
		configuration.memoryCache.clear();
	}

	public DiskCache getDiscCache() {
		return getDiskCache();
	}

	public DiskCache getDiskCache() {
		checkConfiguration();
		return configuration.diskCache;
	}
	public void clearDiscCache() {
		clearDiskCache();
	}
	public void clearDiskCache() {
		checkConfiguration();
		configuration.diskCache.clear();
	}
	public String getLoadingUriForView(ImageAware imageAware) {
		return engine.getLoadingUriForView(imageAware);
	}
	public String getLoadingUriForView(ImageView imageView) {
		return engine.getLoadingUriForView(new ImageViewAware(imageView));
	}
	public void cancelDisplayTask(ImageAware imageAware) {
		engine.cancelDisplayTaskFor(imageAware);
	}


	public void denyNetworkDownloads(boolean denyNetworkDownloads) {
		engine.denyNetworkDownloads(denyNetworkDownloads);
	}

	public void handleSlowNetwork(boolean handleSlowNetwork) {
		engine.handleSlowNetwork(handleSlowNetwork);
	}

	public void pause() {
		engine.pause();
	}

	public void resume() {
		engine.resume();
	}

	public void stop() {
		engine.stop();
	}

	public void destroy() {
		if (configuration != null) L.d(LOG_DESTROY);
		stop();
		configuration.diskCache.close();
		engine = null;
		configuration = null;
	}

	private static Handler defineHandler(DisplayImageOptions options) {
		Handler handler = options.getHandler();
		if (options.isSyncLoading()) {
			handler = null;
		} else if (handler == null && Looper.myLooper() == Looper.getMainLooper()) {
			handler = new Handler();
		}
		return handler;
	}
	private class SyncImageLoadingListener extends SimpleImageLoadingListener {

		private Bitmap loadedImage;

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			this.loadedImage = loadedImage;
		}

		public Bitmap getLoadedBitmap() {
			return loadedImage;
		}
	}
}
