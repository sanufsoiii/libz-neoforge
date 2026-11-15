# LibZ: Unofficial NeoForge Port

Unofficial port of **[LibZ](https://github.com/Globox1997/LibZ)** by **Globox_Z** (Fabric 1.21.1, version 1.2.0) to **NeoForge 1.21.1**.

LibZ is the shared library of Globox_Z mods (inventory tabs, config sync). The port keeps the public API of the original
(`net.libz.*`) unchanged, so NeoForge ports of the other Globox_Z mods can be built against it.

## Status: early alpha

The port builds, starts on a dedicated server and reaches the title screen in the client. The library registers no blocks,
items or entities of its own; the inventory-tab and config-sync API is the same as in the original. Please report anything
that behaves differently from the original (see below).

## Requirements

- Minecraft 1.21.1, NeoForge 21.1.200 or newer
- [Cloth Config](https://modrinth.com/mod/cloth-config) for NeoForge 15.0 or newer

Jankson 1.2.0 is shipped inside the jar (jar-in-jar), exactly like the original Fabric build.

## Assets

All LibZ assets (icon, inventory-tab icons, translations) are licensed under **MIT** by Globox_Z and are therefore
**included** in this port. Nothing has to be downloaded; `assets/libz/` is the original set of four files.

## Building

Requires JDK 21.

```bash
./gradlew build
```

The mod jar is in `build/libs/`. To use the library from another port:

```bash
./gradlew publishToMavenLocal
```

This publishes `net.libz:libz-neoforge:1.2.0-neoforge.0.1` to the local Maven repository.

## Reporting bugs

This is an **unofficial** port. Please **do not** report its bugs to Globox_Z or on the original issue tracker. Use this
repository instead: https://github.com/sanufsoiii/libz-neoforge/issues

## License and credits

- LibZ was created by **Globox_Z**: https://github.com/Globox1997/LibZ. All design, art and names belong to them.
- This port is not affiliated with or endorsed by the original author.
- Code and assets: **MIT**, same as the original (see `LICENSE`).
- Folder `_excluded/` keeps original Fabric-only code (ModMenu integration) for reference; it is not compiled.
