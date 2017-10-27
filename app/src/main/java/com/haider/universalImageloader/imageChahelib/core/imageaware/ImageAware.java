/*******************************************************************************
 * Copyright 2013-2014 Sergey Tarasevich
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
package com.haider.universalImageloader.imageChahelib.core.imageaware;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.haider.universalImageloader.imageChahelib.core.assist.ViewScaleType;

public interface ImageAware {
	/**
	 * Returns width of image aware view. This value is used to define scale size for original image.
	 * Can return 0 if width is undefined.<br />
	 * Is called on UI thread if ImageLoader was called on UI thread. Otherwise - on background thread.
	 */
	int getWidth();

	/**
	 * Returns height of image aware view. This value is used to define scale size for original image.
	 * Can return 0 if height is undefined.<br />
	 * Is called on UI thread if ImageLoader was called on UI thread. Otherwise - on background thread.
	 */
	int getHeight();

	ViewScaleType getScaleType();

	/**
	 * Returns wrapped Android {@link View View}. Can return <b>null</b> if no view is wrapped or view was
	 * collected by GC.<br />
	 * Is called on UI thread if ImageLoader was called on UI thread. Otherwise - on background thread.
	 */
	View getWrappedView();


	boolean isCollected();

	/**
	 * Returns ID of image aware view. Point of ID is similar to Object's hashCode. This ID should be unique for every
	 * image view instance and should be the same for same instances. This ID identifies processing task in ImageLoader
	 * so ImageLoader won't process two image aware views with the same ID in one time. When ImageLoader get new task
	 * it cancels old task with this ID (if any) and starts new task.
	 * <p/>
	 * It's reasonable to return hash code of wrapped view (if any) to prevent displaying non-actual images in view
	 * because of view re-using.
	 */
	int getId();


	boolean setImageDrawable(Drawable drawable);

	/**
	 * Sets image bitmap into this image aware view.<br />
	 * Displays loaded and decoded image {@link Bitmap} in this image view aware.
	 * Actually it's used only in
	 * Is called on UI thread if ImageLoader was called on UI thread. Otherwise - on background thread.
	 *
	 * @return <b>true</b> if bitmap was set successfully; <b>false</b> - otherwise
	 */
	boolean setImageBitmap(Bitmap bitmap);
}
