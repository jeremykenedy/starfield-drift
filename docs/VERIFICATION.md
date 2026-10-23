# Device verification

Device checks distinguish app rendering from the vendor's screensaver manager. An emulator preview does not prove automatic idle activation, vendor settings behavior, native 4K composition, sustained thermal behavior, or performance on physical hardware.

| Platform | Device and OS | Result |
| --- | --- | --- |
| Android TV emulator | `sdk_google_atv64_arm64`, Android 12/API 31, 1920x1080 | App installed and launched. Settings activity, exported settings-provider schema/update, and animated full-screen renderer were checked. The emulator has no `dream` system service, so DreamService activation through system settings was not available. |
| Fire TV | Physical device not tested for this release | Not verified. We are looking for a Fire TV owner to test installation, screensaver selection and activation, and remote settings, then report the device model, Fire OS/API, resolution, and results in the [issue tracker](https://github.com/jeremykenedy/starfield-drift/issues). |
| Google TV | Physical device not tested for this release | Not verified. We are looking for a Google TV owner to run the same checks and report the device model, OS/API, resolution, and results in the [issue tracker](https://github.com/jeremykenedy/starfield-drift/issues). |

The [Starfield Drift preview](screenshots/starfield-preview.png) and [settings screen](screenshots/settings-android-tv.png) were captured from the running app on the Android TV emulator. The screensaver's in-app animation preview uses the same renderer. The built APK declares the `android.service.dreams.DreamService` action and dream metadata, and the provider schema was queried and updated; this does not establish support in vendor screensaver menus.

The Android TV emulator exposes a 1920x1080 surface. Native 4K output has not been measured. Frame pacing, CPU/GPU use, memory, power draw, and sustained temperature have not been measured on a physical television. Do not infer device performance from the emulator.

## Optional clock regression, 2026-10-22

An isolated Android TV emulator fixture compiled the production scene and provider classes from this change. The combined five-app run passed 123 actual Canvas/provider assertions. It checked the existing appearance default (OFF), explicit ON/OFF under scene randomization, portable boolean storage and schema, and invalid-write retention. OFF produced zero time/date/seconds text draws while the scene remained drawn.

This isolated regression does not establish production DreamService binding, physical-TV performance or acceptance of the existing artwork. Signed release and both-TV installation gates remain pending.
