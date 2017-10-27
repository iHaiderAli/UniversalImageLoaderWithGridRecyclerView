package com.haider.universalImageloader.utils;

import android.view.View;

/**
 * Created by support on 10/27/17.
 */

public interface ClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
