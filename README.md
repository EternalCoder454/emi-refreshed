# EMI Refreshed, NeoForge 26.1.2

<img src="xplat/src/main/resources/icon.png" alt="EMI Refreshed" width="180">

EMI is an item and recipe viewer: search every item in the game, see how anything is crafted, and
work backwards from a result to what you need.

A personal NeoForge port with faster search and **built-in Applied Energistics 2 support**.
**Minecraft 26.1.2 · Java 25**

## What you get

Everything EMI normally does, plus:

- **Faster search.** Typing no longer stutters on a big modpack.
- **AE2 built in.** Inscriber and Charger recipes show up without a separate bridge mod, and the
  **plus button fills an inscriber directly** from a recipe.

## Installing

Drop the jar in your `mods` folder. AE2 support switches itself on if AE2 is present and stays
completely out of the way if it is not.

## Using it

- **Search** with the box at the bottom right. `@mod` filters by mod, `#tag` by tag, `$` by item tag.
- **Left click** a recipe result to see how it is made; **right click** to see what it is used in.
- **The plus button** on a recipe fills the open crafting grid, or an AE2 inscriber, with what it
  needs.
- **The recipe tree** works backwards from something you want to everything required for it.

---

## This is a fork

**EMI is created and maintained by [Emi](https://github.com/emilyploszaj/emi).** All credit for it
belongs to them. This repository is an unofficial personal port, not affiliated with or endorsed by
the author. It also builds directly on the porting work in
[link-fgfgui's 26.1 branch](https://github.com/link-fgfgui/emi).

**Download EMI from the official project** or its Modrinth listing. Builds here are untested outside
my own setup and will lag upstream. EMI is MIT licensed and this fork is distributed under the same
terms; see `LICENSE`.

Notes on what this fork changes are in [DEV.md](DEV.md).
