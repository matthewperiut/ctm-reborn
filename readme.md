# CTM Reborn

A connected-textures / baked model library for Fabric and NeoForge.

CTM Reborn is a fork of [Athena](https://github.com/terrarium-earth/Athena) by ThatGravyBoat and
Terrarium Earth, used under the MIT license. It exists to keep connected textures working on current
Minecraft versions — primarily for **Chisel Reborn** — while upstream Athena is between releases.

**This fork is meant to be temporary.** Once Athena updates to these Minecraft versions itself, CTM
Reborn will be deprecated in favour of it.

## Branches

| Branch | Minecraft | Status |
| --- | --- | --- |
| `main` | 26.3 snapshots | Fabric only - see below |
| `26.2` | 26.2 | Release |

## NeoForge on 26.3

NeoForge has not published a 26.3 build; 26.2.0.66 is the newest that exists. Its userdev artifact
also pins its own NeoForm version, which overrides the one in the version catalog, so building the
NeoForge module on `main` produces a jar compiled against Minecraft 26.2 while claiming to be 26.3.
The module is therefore commented out of `settings.gradle.kts` on `main`. The NeoForge source is kept
current, and both the include and the version pin are one line each to restore once NeoForge ships
26.3. The `26.2` branch builds both platforms.

## Compatibility with Athena

The mod id is `ctm_reborn`, but it deliberately stays compatible with resource packs written for
Athena:

- Model files are still read from `assets/<namespace>/athena/<block>.json`.
- The `athena:loader` key in blockstate files still works, alongside `ctm_reborn:loader`.
- Every model type is registered under both `athena:` and `ctm_reborn:` — `athena:ctm`,
  `athena:mural` and friends all still resolve.
- On Fabric the mod declares `provides: ["athena"]`, so mods that depend on Athena are satisfied.
  NeoForge has no equivalent, so mods there that hard-require the `athena` mod id will not see it.

## Credits

Athena was written by ThatGravyBoat and contributors at Terrarium Earth. All original credit for the
library belongs to them; this fork only carries it forward. See `LICENSE` for the MIT license it is
distributed under.
