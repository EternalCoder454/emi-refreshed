# EMI Refreshed

Search every item in the game, see how anything is crafted, and work backwards from a result to
everything you need for it.

![Minecraft](https://img.shields.io/badge/minecraft-26.1.2-brightgreen.svg)
![Loader](https://img.shields.io/badge/loader-NeoForge-orange.svg)
![Java](https://img.shields.io/badge/java-25-blue.svg)
![License](https://img.shields.io/badge/license-MIT-blue.svg)

<img src="xplat/src/main/resources/icon.png" alt="EMI Refreshed" width="180">

## 🔍 About

EMI is an item and recipe viewer. This is a personal NeoForge port of it with faster search and
**built-in Applied Energistics 2 support**.

Everything EMI normally does, plus:

- **Faster search.** Typing no longer stutters on a big modpack.
- **AE2 built in.** Inscriber and Charger recipes show up without a separate bridge mod, and the
  **plus button fills an inscriber directly** from a recipe.

## 📦 Installing

Drop the jar in your `mods` folder. AE2 support switches itself on if AE2 is present and stays
completely out of the way if it is not.

**Java 25** is required, which is what NeoForge 26.1 runs on anyway.

## 🎮 Using it

- **Search** with the box at the bottom right. `@mod` filters by mod, `#tag` by tag, `$` by item tag.
- **Left click** a recipe result to see how it is made, **right click** to see what it is used in.
- **The plus button** on a recipe fills the open crafting grid, or an AE2 inscriber, with what it
  needs.
- **The recipe tree** works backwards from something you want to everything required for it.

## 📝 Credit, and this is a fork

**EMI is created and maintained by [Emi](https://github.com/emilyploszaj/emi).** All credit for it
belongs to them. This repository is an unofficial personal port, not affiliated with or endorsed by
the author. It also builds directly on the porting work in
[link-fgfgui's 26.1 branch](https://github.com/link-fgfgui/emi).

**Download EMI from the official project** or its Modrinth listing. Builds here are untested outside
my own setup and will lag upstream. EMI is MIT licensed and this fork is distributed under the same
terms, see `LICENSE`.

## 💻 For developers

| File | Covers |
|---|---|
| [DEV.md](DEV.md) | what this fork changes, and how it is built |
| [PLAYTESTING.md](PLAYTESTING.md) | what to check before calling a build good |
| [CHANGELOG.md](CHANGELOG.md) | what changed, per release |
| [CONTRIBUTING.md](CONTRIBUTING.md) | upstream's contribution guide, unchanged |
