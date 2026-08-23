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
| `main` | 26.3 snapshots | Tracks the latest 26.3 snapshot |
| `26.2` | 26.2 | Release |

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
