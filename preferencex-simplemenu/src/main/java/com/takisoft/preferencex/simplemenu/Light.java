package com.takisoft.preferencex.simplemenu;

import android.annotation.SuppressLint;
import android.graphics.HardwareRenderer;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.widget.PopupWindow;

@SuppressWarnings({"unchecked", "ConstantConditions"})
@SuppressLint("PrivateApi")
class Light {

    /**
     * Android uses <code>displaySize.x / 2 - windowLeft</code> as the x-coordinate of light source (<a href="http://androidxref.com/9.0.0_r3/xref/frameworks/base/core/java/android/view/ThreadedRenderer.java#1021">source code</a>).
     * <br>If the window is on the left of the screen, the light source will be at the right to the window
     * causing shadow on the left side. This make our PopupWindow looks weird.
     * <p>This method reset the x-coordinate of light source to <code>windowLeft + 56dp</code> by using multiply reflections.
     *
     * @param window PopupWindow
     */
    static void resetLightCenterForPopupWindow(PopupWindow window) {
        try {
            Class threadedRendererClass = Class.forName("android.view.ThreadedRenderer");
            Class attachInfoClass = Class.forName("android.view.View$AttachInfo");

            View view = window.getContentView().getRootView();
            Object threadedRenderer = Hack.into(View.class)
                    .method("getThreadedRenderer")
                    .returning(threadedRendererClass)
                    .withoutParams()
                    .invoke()
                    .on(view);

            Object attachInfo = Hack.into(View.class)
                    .field("mAttachInfo").ofType(attachInfoClass).get(view);

            Point displaySize = (Point) Hack.into(attachInfoClass)
                    .field("mPoint").ofType(Point.class).get(attachInfo);

            Display display = (Display) Hack.into(attachInfoClass)
                    .field("mDisplay").ofType(Display.class).get(attachInfo);

            display.getRealSize(displaySize);

            int mWindowLeft = (int) Hack.into(attachInfoClass)
                    .field("mWindowLeft").ofType(int.class).get(attachInfo);

            int mWindowTop = (int) Hack.into(attachInfoClass)
                    .field("mWindowTop").ofType(int.class).get(attachInfo);

            float mLightY = (float) Hack.into(threadedRendererClass)
                    .field("mLightY").ofType(float.class).get(threadedRenderer);

            float mLightZ = (float) Hack.into(threadedRendererClass)
                    .field("mLightZ").ofType(float.class).get(threadedRenderer);

            float mLightRadius = (float) Hack.into(threadedRendererClass)
                    .field("mLightRadius").ofType(float.class).get(threadedRenderer);

            final float lightX = mWindowLeft;
            final float lightY = mLightY - mWindowTop;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ((HardwareRenderer) threadedRenderer).setLightSourceGeometry(
                        lightX, lightY, mLightZ, mLightRadius
                );
            } else {
                long mNativeProxy = (long) Hack.into(threadedRendererClass)
                        .field("mNativeProxy").ofType(long.class).get(threadedRenderer);

                Hack.into(threadedRendererClass)
                        .staticMethod("nSetLightCenter")
                        .withParams(long.class, float.class, float.class, float.class)
                        .invoke(mNativeProxy, lightX, lightY, mLightZ)
                        .statically();
            }
        } catch (Throwable tr) {
            tr.printStackTrace();
        }
    }
}
