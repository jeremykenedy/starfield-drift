package com.jeremykenedy.starfielddrift;

import java.util.Random;

public final class StarfieldOptionsTest {
    public static void main(String[] args) {
        verifyDensityAndSpeed();
        verifyPaletteBrightnessAndMeteors();
        verifyExplicitAndRandomSelection();
        verifySettingsValidation();
        System.out.println("Starfield settings tests passed.");
    }

    private static void verifyDensityAndSpeed() {
        check(StarfieldOptions.countFor("sparse") == 180, "sparse density");
        check(StarfieldOptions.countFor("balanced") == 360, "balanced density");
        check(StarfieldOptions.countFor("unknown") == 360, "density fallback");
        check(StarfieldOptions.countFor("dense") == 620, "dense density");
        check(StarfieldOptions.countFor("packed") == 1100, "packed density");
        check(StarfieldOptions.speedFor("slow") == 0.55f, "slow speed");
        check(StarfieldOptions.speedFor("normal") == 1f, "normal speed");
        check(StarfieldOptions.speedFor("unknown") == 1f, "speed fallback");
        check(StarfieldOptions.speedFor("fast") == 1.7f, "fast speed");
    }

    private static void verifyPaletteBrightnessAndMeteors() {
        check(StarfieldOptions.paletteFor("natural") == 0, "natural palette");
        check(StarfieldOptions.paletteFor("blue") == 1, "blue palette");
        check(StarfieldOptions.paletteFor("amber") == 2, "amber palette");
        check(StarfieldOptions.paletteFor("unknown") == 0, "palette fallback");
        check(StarfieldOptions.brightnessFor("dim") == 0.48f, "dim brightness");
        check(StarfieldOptions.brightnessFor("standard") == 0.9f, "standard brightness");
        check(StarfieldOptions.brightnessFor("unknown") == 0.9f, "brightness fallback");
        check(StarfieldOptions.brightnessFor("bright") == 1.35f, "bright brightness");
        check(StarfieldOptions.meteorCount("off") == 0, "meteors off");
        check(StarfieldOptions.meteorCount("unknown") == 0, "meteor fallback");
        check(StarfieldOptions.meteorCount("occasional") == 2, "occasional meteors");
        check(StarfieldOptions.meteorCount("frequent") == 5, "frequent meteors");
    }

    private static void verifyExplicitAndRandomSelection() {
        StarfieldOptions explicit = StarfieldOptions.resolve(
                "packed", "fast", "amber", "dim", "frequent", false, new Random(1));
        check(explicit.count == 1100 && explicit.speed == 1.7f && explicit.palette == 2
                && explicit.brightness == 0.48f && explicit.meteors == 5, "explicit settings");
        StarfieldOptions fallback = StarfieldOptions.resolve(
                "invalid", "invalid", "invalid", "invalid", "invalid", false, new Random(1));
        check(fallback.count == 360 && fallback.speed == 1f && fallback.palette == 0
                && fallback.brightness == 0.9f && fallback.meteors == 2, "safe fallback settings");
        StarfieldOptions random = StarfieldOptions.resolve(
                "random", "random", "random", "random", "random", false, new FixedRandom(2));
        check(random.count == 620 && random.speed == 1.7f && random.palette == 2
                && random.brightness == 1.35f && random.meteors == 5, "per-setting random choices");
        StarfieldOptions all = StarfieldOptions.resolve(
                "sparse", "slow", "natural", "dim", "off", true, new FixedRandom(1));
        check(all.count == 180 && all.speed == 0.55f && all.palette == 1
                && all.brightness == 0.48f && all.meteors == 0, "randomize all selections");
    }

    private static void verifySettingsValidation() {
        check(!SettingsValues.isSupported(null, "dark"), "null key rejected");
        check(!SettingsValues.isSupported("density", null), "null value rejected");
        for (String value : new String[] {"sparse", "balanced", "dense", "packed", "random"})
            check(SettingsValues.isSupported("density", value), "density accepted: " + value);
        for (String value : new String[] {"slow", "normal", "fast", "random"})
            check(SettingsValues.isSupported("motion", value), "motion accepted: " + value);
        for (String value : new String[] {"natural", "blue", "amber", "random"})
            check(SettingsValues.isSupported("palette", value), "palette accepted: " + value);
        for (String value : new String[] {"dim", "standard", "bright", "random"})
            check(SettingsValues.isSupported("brightness", value), "brightness accepted: " + value);
        for (String value : new String[] {"off", "occasional", "frequent", "random"})
            check(SettingsValues.isSupported("meteors", value), "meteor setting accepted: " + value);
        check(SettingsValues.isSupported("randomize_all", "true"), "randomize on accepted");
        check(SettingsValues.isSupported("randomize_all", "false"), "randomize off accepted");
        check(!SettingsValues.isSupported("density", "impossible"), "invalid density rejected");
        check(!SettingsValues.isSupported("motion", "instant"), "invalid motion rejected");
        check(!SettingsValues.isSupported("palette", "green"), "invalid palette rejected");
        check(!SettingsValues.isSupported("brightness", "blinding"), "invalid brightness rejected");
        check(!SettingsValues.isSupported("meteors", "all"), "invalid meteor setting rejected");
        check(!SettingsValues.isSupported("randomize_all", "yes"), "invalid boolean rejected");
        check(!SettingsValues.isSupported("unknown", "value"), "unknown setting rejected");
    }

    private static void check(boolean result, String message) {
        if (!result) throw new AssertionError(message);
    }

    private static final class FixedRandom extends Random {
        private final int value;

        FixedRandom(int value) {
            this.value = value;
        }

        @Override
        public int nextInt(int bound) {
            return Math.min(value, bound - 1);
        }
    }
}
