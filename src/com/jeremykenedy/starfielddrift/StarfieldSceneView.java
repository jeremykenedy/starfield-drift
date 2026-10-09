package com.jeremykenedy.starfielddrift;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.View;

import java.util.Random;

final class StarfieldSceneView extends View {
    private static final int[] NATURAL_COLORS = {0xffbfd9ff, 0xffffe3c2, 0xffd8e7ff, 0xfffff5dc, 0xffffffff};
    private static final int[] BLUE_COLORS = {0xffb9d5ff, 0xffe8f2ff, 0xff9bbdff, 0xffcbdcff, 0xffffffff};
    private static final int[] AMBER_COLORS = {0xffffd8a1, 0xfffff0cf, 0xffffb97e, 0xffffe2b7, 0xffffffff};
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Random random = new Random(76091L);
    private final Star[] stars;
    private final Meteor[] meteors;
    private final StarfieldOptions options;
    private LinearGradient background;
    private long startedAt;
    private boolean running;

    StarfieldSceneView(Context context) {
        super(context);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        options = StarfieldOptions.resolve(preferences.getString("density", "balanced"),
                preferences.getString("motion", "normal"), preferences.getString("palette", "natural"),
                preferences.getString("brightness", "standard"), preferences.getString("meteors", "occasional"),
                preferences.getBoolean("randomize_all", false), new Random(System.currentTimeMillis()));
        stars = new Star[options.count];
        for (int i = 0; i < stars.length; i++) {
            stars[i] = new Star(random.nextFloat(), random.nextFloat(), 0.35f + random.nextFloat() * 2.0f,
                    0.25f + random.nextFloat() * 0.75f, random.nextFloat() * 6.28f,
                    random.nextFloat() * 1.6f + 1.1f, random.nextInt(5));
        }
        meteors = new Meteor[options.meteors];
        for (int i = 0; i < meteors.length; i++) {
            meteors[i] = new Meteor(random.nextFloat(), random.nextFloat() * 30_000f, 0.45f + random.nextFloat() * 0.5f);
        }
    }

    void start() {
        if (!running) {
            running = true;
            startedAt = SystemClock.uptimeMillis();
            postInvalidateOnAnimation();
        }
    }

    void stop() {
        running = false;
        removeCallbacks(invalidator);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getWidth() == 0 || getHeight() == 0) return;
        float time = (SystemClock.uptimeMillis() - startedAt) / 1000f;
        drawBackground(canvas);
        drawStars(canvas, time);
        drawMeteors(canvas, time);
        if (running) postDelayed(invalidator, 41L);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        if (width <= 0 || height <= 0) return;
        int top = options.palette == 2 ? 0xff130d12 : options.palette == 1 ? 0xff071426 : 0xff080b16;
        int bottom = options.palette == 2 ? 0xff020207 : options.palette == 1 ? 0xff020610 : 0xff02030a;
        background = new LinearGradient(0, 0, 0, height, top, bottom, Shader.TileMode.CLAMP);
    }

    private final Runnable invalidator = new Runnable() {
        @Override
        public void run() {
            if (running) invalidate();
        }
    };

    private void drawBackground(Canvas canvas) {
        paint.setShader(background);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        paint.setShader(null);
    }

    private void drawStars(Canvas canvas, float time) {
        for (Star star : stars) {
            float drift = time * options.speed * star.depth * 12f;
            float x = (star.x * getWidth() + (float) Math.sin(time * 0.025f + star.phase) * star.depth * 8f) % getWidth();
            float y = (star.y * getHeight() + drift) % getHeight();
            float twinkle = 0.53f + 0.47f * (float) Math.sin(time * star.twinkle + star.phase);
            int alpha = Math.min(255, Math.max(0, (int) (star.alpha * (0.6f + twinkle * 0.4f)
                    * options.brightness * 255f)));
            paint.setColor(starColor(star.color));
            paint.setAlpha(alpha);
            canvas.drawCircle(x, y, star.radius * (0.7f + twinkle * 0.3f), paint);
            if (star.radius > 1.85f && twinkle > 0.92f) {
                paint.setAlpha(alpha / 3);
                canvas.drawCircle(x, y, star.radius * 2.4f, paint);
            }
        }
        paint.setAlpha(255);
    }

    private void drawMeteors(Canvas canvas, float time) {
        for (Meteor meteor : meteors) {
            float cycle = (time + meteor.offset / 1000f) % 58f;
            if (cycle > 2.8f) continue;
            float progress = cycle / 2.8f;
            float x = (meteor.x + progress * 0.42f) * getWidth();
            float y = (0.1f + progress * 0.55f) * getHeight();
            paint.setColor(0xffdceaff);
            paint.setAlpha((int) (options.brightness * 215f * (1f - progress * 0.45f)));
            paint.setStrokeWidth(1.4f + meteor.weight * 1.8f);
            canvas.drawLine(x, y, x - getWidth() * 0.085f, y - getHeight() * 0.075f, paint);
        }
        paint.setAlpha(255);
    }

    private int starColor(int type) {
        if (options.palette == 1) return BLUE_COLORS[type];
        if (options.palette == 2) return AMBER_COLORS[type];
        return NATURAL_COLORS[type];
    }

    private static final class Star {
        final float x, y, radius, alpha, phase, twinkle, depth;
        final int color;

        Star(float x, float y, float radius, float alpha, float phase, float twinkle, int color) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.alpha = alpha;
            this.phase = phase;
            this.twinkle = twinkle;
            this.depth = 0.3f + radius * 0.35f;
            this.color = color;
        }
    }

    private static final class Meteor {
        final float x, offset, weight;

        Meteor(float x, float offset, float weight) {
            this.x = x;
            this.offset = offset;
            this.weight = weight;
        }
    }
}
