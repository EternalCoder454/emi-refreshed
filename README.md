# EMI Refreshed, NeoForge 26.1.2

<img src="xplat/src/main/resources/icon.png" alt="EMI Refreshed" width="180">

A personal port of EMI, the item and recipe viewer, to **Minecraft 26.1.2 on NeoForge** with Java 25,
plus search performance work and built-in Applied Energistics 2 support.

## What this fork changes

**Platform**

- Runs on **NeoForge 26.1.2** / Java 25.

**Search performance**

- Search runs on a **dedicated single-thread executor** rather than spawning a fresh thread per
  query. Typing a word used to start and abandon a thread per keystroke.
- The bake loop skips work the `Identifier` contract already guarantees. A namespace only permits
  `[a-z0-9._-]` and a path `[a-z0-9/._-]`, so both are lowercase by construction and calling
  `toLowerCase` on them was a scan that could never change anything.
- Mod name lookups are cached. Deliberately using the **default locale** rather than `Locale.ROOT`,
  because the query side lowercases with the default locale, and mismatching those two would break
  mod-name search in a Turkish locale in a way that is very hard to trace.
- Bake timing is logged, so changes here can be measured rather than guessed at.

**AE2 integration, built in**

- **Inscriber** and **Charger** categories, so AE2 recipes show up without a separate bridge mod.
- **Recipe transfer**: the plus button fills an inscriber directly. Slots are resolved through
  `SlotSemantics` (`INSCRIBER_PLATE_TOP`, `MACHINE_INPUT`, `INSCRIBER_PLATE_BOTTOM`) rather than raw
  indices, so it does not silently break if AE2 reorders its menu.
- Every recipe is parsed inside its own try/catch, so one malformed recipe logs a warning instead of
  taking the whole plugin down.
- Entirely inert without AE2 installed: the entrypoint references nothing from AE2 until it has
  confirmed the mod is loaded.

## Building

Requires JDK 25.

```
gradlew :neoforge:build
```

---

## This is a fork

**EMI is created and maintained by [Emi](https://github.com/emilyploszaj/emi).** All credit for it
belongs to them. This repository is an unofficial personal port, not affiliated with or endorsed by
the author. It also builds directly on the porting work in
[link-fgfgui's 26.1 branch](https://github.com/link-fgfgui/emi).

**Download EMI from the official project** or its Modrinth listing. Builds here are untested outside
my own setup and will lag upstream. EMI is MIT licensed and this fork is distributed under the same
terms; see `LICENSE`.
