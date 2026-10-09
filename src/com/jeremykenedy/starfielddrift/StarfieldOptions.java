package com.jeremykenedy.starfielddrift;

import java.util.Random;

public final class StarfieldOptions {
    public final int count;
    public final float speed;
    public final int palette;
    public final float brightness;
    public final int meteors;

    private StarfieldOptions(int count, float speed, int palette, float brightness, int meteors) {
        this.count = count;
        this.speed = speed;
        this.palette = palette;
        this.brightness = brightness;
        this.meteors = meteors;
    }

    public static StarfieldOptions resolve(String density, String motion, String palette,
            String brightness, String meteorSetting, boolean randomizeAll, Random random) {
        String selectedDensity = choose(density, randomizeAll, random, "balanced", "sparse", "dense", "packed");
        String selectedMotion = choose(motion, randomizeAll, random, "normal", "slow", "fast");
        String selectedPalette = choose(palette, randomizeAll, random, "natural", "blue", "amber");
        String selectedBrightness = choose(brightness, randomizeAll, random, "standard", "dim", "bright");
        String selectedMeteors = choose(meteorSetting, randomizeAll, random, "occasional", "off", "frequent");
        return new StarfieldOptions(countFor(selectedDensity), speedFor(selectedMotion),
                paletteFor(selectedPalette), brightnessFor(selectedBrightness), meteorCount(selectedMeteors));
    }

    static String choose(String selected, boolean randomizeAll, Random random, String... values) {
        if (randomizeAll || "random".equals(selected)) return values[random.nextInt(values.length)];
        for (String value : values) if (value.equals(selected)) return selected;
        return values[0];
    }

    static int countFor(String density) {
        if ("sparse".equals(density)) return 180;
        if ("dense".equals(density)) return 620;
        if ("packed".equals(density)) return 1100;
        return 360;
    }

    static float speedFor(String motion) {
        if ("slow".equals(motion)) return 0.55f;
        if ("fast".equals(motion)) return 1.7f;
        return 1f;
    }

    static int paletteFor(String palette) {
        if ("blue".equals(palette)) return 1;
        if ("amber".equals(palette)) return 2;
        return 0;
    }

    static float brightnessFor(String brightness) {
        if ("dim".equals(brightness)) return 0.48f;
        if ("bright".equals(brightness)) return 1.35f;
        return 0.9f;
    }

    static int meteorCount(String setting) {
        if ("occasional".equals(setting)) return 2;
        if ("frequent".equals(setting)) return 5;
        return 0;
    }
}
