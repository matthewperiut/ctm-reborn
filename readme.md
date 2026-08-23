# CTM Reborn

A connected textures and baked model library for Fabric and NeoForge.

CTM Reborn is a fork of [Athena](https://github.com/terrarium-earth/Athena) by ThatGravyBoat and
Terrarium Earth, used under the MIT license. It keeps connected textures working on current
Minecraft versions, primarily for **Chisel Reborn**, while upstream Athena is between releases.

**The fork is meant to be temporary.** Once Athena updates to these Minecraft versions itself, CTM
Reborn will be deprecated in favour of it.

## This is Athena's implementation

The code here is Athena's code. The connected texture logic, the model types, the format parsing,
the quad generation and the platform hookup were all written by ThatGravyBoat and the Terrarium
Earth contributors. CTM Reborn carries that implementation onto newer Minecraft versions and keeps
it building. The changes are version compatibility and maintenance.

## Branches

| Branch | Minecraft | Mod version |
| --- | --- | --- |
| `main` | 26.3 | 4.9.x |
| `26.2` | 26.2 | 4.8.x |

## Compatibility with Athena

The mod id is `ctm_reborn`, but it stays compatible with resource packs written for Athena:

- Model files are still read from `assets/<namespace>/athena/<block>.json`.
- The `athena:loader` key in blockstate files still works, alongside `ctm_reborn:loader`.
- Every model type is registered under both `athena:` and `ctm_reborn:`, so `athena:ctm`,
  `athena:mural` and the rest all still resolve.
- On Fabric the mod declares `provides: ["athena"]`, so mods that depend on Athena are satisfied.
  NeoForge has no equivalent, so mods there that hard require the `athena` mod id will not see it.

## Building

Requires JDK 25.

```
./gradlew build
```

## Credits

Athena was written by ThatGravyBoat and contributors at Terrarium Earth. All original credit for the
library belongs to them. See `LICENSE` for the MIT license it is distributed under.
