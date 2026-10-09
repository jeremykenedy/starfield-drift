package com.jeremykenedy.starfielddrift;

import android.service.dreams.DreamService;

public final class StarfieldDreamService extends DreamService {
    private StarfieldSceneView scene;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setInteractive(false);
        setFullscreen(true);
        setScreenBright(true);
        scene = new StarfieldSceneView(this);
        setContentView(scene);
    }

    @Override
    public void onDreamingStarted() {
        super.onDreamingStarted();
        if (scene != null) scene.start();
    }

    @Override
    public void onDreamingStopped() {
        if (scene != null) scene.stop();
        super.onDreamingStopped();
    }

    @Override
    public void onDetachedFromWindow() {
        if (scene != null) scene.stop();
        scene = null;
        super.onDetachedFromWindow();
    }
}
