package com.icebem.akt.overlay;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

public class OverlayView {
    private int x, y;
    private boolean mobilizable, handled, showing;
    private View view;
    private WindowManager manager;
    private WindowManager.LayoutParams params;

    public OverlayView(Context context, View view) {
        this.view = view;
        manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();
        params.x = params.y = 0;
        params.width = params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        params.format = PixelFormat.RGBA_8888;
        params.type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE;
        params.windowAnimations = android.R.style.Animation_Toast;
    }

    public View getView() {
        return view;
    }

    public void show() {
        if (!showing) {
            manager.addView(view, params);
            showing = true;
        }
    }

    public void remove() {
        if (showing) {
            manager.removeView(view);
            showing = false;
        }
    }

    public void resize(int width, int height) {
        params.width = width;
        params.height = height;
        if (showing)
            manager.updateViewLayout(view, params);
    }

    public void setGravity(int gravity) {
        params.gravity = gravity;
        if (showing)
            manager.updateViewLayout(view, params);
    }

    public void setMobilizable(boolean mobilizable) {
        this.mobilizable = mobilizable;
        view.setOnTouchListener(mobilizable ? this::onTouch : null);
    }

    public void onConfigurationChanged(Configuration cfg) {
        if (mobilizable)
            params.x = params.y = 0;
        if (showing)
            manager.updateViewLayout(view, params);
    }

    private boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (!handled)
                    handled = view.performClick();
                break;
            case MotionEvent.ACTION_DOWN:
                handled = false;
                x = (int) event.getRawX();
                y = (int) event.getRawY();
                view.postDelayed(() -> {
                    if (!handled)
                        handled = view.performLongClick();
                }, ViewConfiguration.getLongPressTimeout());
                break;
            case MotionEvent.ACTION_MOVE:
                if (!handled && Math.abs((int) event.getRawX() - x) < view.getWidth() / 4 && Math.abs((int) event.getRawY() - y) < view.getHeight() / 4)
                    break;
                handled = true;
                params.x += (int) event.getRawX() - x;
                params.y += (int) event.getRawY() - y;
                x = (int) event.getRawX();
                y = (int) event.getRawY();
                manager.updateViewLayout(view, params);
                break;
        }
        return true;
    }
}