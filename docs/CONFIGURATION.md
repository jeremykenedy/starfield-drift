# Configuration

Open Starfield Drift from the TV launcher and use the remote to set preferences. Choices persist locally and are applied at the next screensaver start.

| Setting | Choices | Default |
| --- | --- | --- |
| Star density | Sparse (180 stars), balanced (360), dense (620), packed (1,100), random | Balanced |
| Drift speed | Slow, natural, fast, random | Natural |
| Star colors | Natural, cool blue, warm amber, random | Natural |
| Star brightness | Dim, standard, bright, random | Standard |
| Meteors | Off, occasional, frequent, random | Occasional |
| Randomize all settings each start | Off, on | Off |

Per-setting random choices are selected once when each dream starts. Randomize all selects a value for each visual setting at the same time.

Star brightness changes rendered star luminance. It does not alter the television's display brightness.

## Host application settings contract

The settings provider authority is `com.jeremykenedy.starfielddrift.settings`:

- `content://com.jeremykenedy.starfielddrift.settings/schema` returns setting keys, titles, types, defaults, choices, and random support.
- `content://com.jeremykenedy.starfielddrift.settings/settings` returns current key/value pairs.

Update one supported choice through `ContentResolver.update()` on the `settings` URI with `ContentValues` named `key` and `value`. Unsupported keys and values throw `IllegalArgumentException`. Boolean values are the strings `true` or `false`. Query the schema rather than assuming new options exist.

The provider exposes visual preferences only. It does not expose device or account data and it does not make network requests.
