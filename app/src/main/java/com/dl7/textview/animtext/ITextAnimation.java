package com.dl7.textview.animtext;

/**
 * Created by long on 2016/7/5.
 * 动画接口
 */
public interface ITextAnimation {
    /**
     * Starts the animation.
     */
    void start();

    /**
     * Stops the animation.
     */
    void stop();

    /**
     * Indicates whether the animation is running.
     *
     * @return True if the animation is running, false otherwise.
     */
    boolean isRunning();
}
