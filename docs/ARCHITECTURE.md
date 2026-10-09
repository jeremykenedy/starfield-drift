# Architecture

Starfield Drift is a native Android app with one DreamService, a remote-friendly settings activity, and an exported settings provider. The Canvas renderer creates a procedural sky and deterministic star field, then animates parallax drift, twinkle, and optional meteor trails. No visual assets or third-party runtime libraries are required.

The DreamService creates a scene when attached, starts its frame loop while the dream runs, and stops scheduled drawing when the dream pauses or detaches. The preview activity uses the same renderer. Shared preferences store visual choices locally. The provider exposes only validated, documented visual settings to host apps.

The application requests no network permission and includes no reporting clients. Pure option resolution and setting validation are tested independently; Android graphics, activity behavior, and DreamService lifecycle are verified on emulators and devices.
