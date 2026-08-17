# EMI Refreshed: developer notes

What this fork changes and why. For installing and using it, see [README.md](README.md).

## Building

Requires **JDK 25**. Architectury multi-platform project: `xplat` holds the shared code, `neoforge`
and `fabric` hold the loader-specific parts.

```
gradlew :neoforge:build
```

On Windows, if Gradle fails with `Unable to establish loopback connection`, your `TEMP` is resolving
to an 8.3 short path (`C:\Users\ZACHAR~1\...`), which the AF_UNIX socket Gradle uses will not accept.
Point both variables at a short plain path first: `$env:TMP="C:\gtmp"; $env:TEMP="C:\gtmp"`.

Versions live in `gradle.properties`. AE2 is pulled in as a `compileOnly` dependency, so the
integration compiles without it being present at runtime.

## Search performance

Measured on a 56 mod pack: world join reload was 986 ms for 7138 recipes, split as search baking
375 ms, plugin load 223 ms, item groups 141 ms, recipe baking 102 ms. The search bake was the only
reload phase that was not timed, which made it the one phase impossible to compare between runs, so
it now logs a line per reload. No per keystroke logging.

### One search thread, not one per keystroke

`EmiSearch.search` allocated a fresh OS thread on **every keystroke**, so typing a word spawned a
thread per character, and each one kept scanning the stack list until it happened to notice it had
been superseded, which it only checked every 1024 entries.

Now a single reusable daemon thread. The newest query still wins through the same `currentWorker`
check, and superseded queries return before scanning anything at all.

`SearchWorker.run` assigns `searchThread` itself, because `ItemStackMixin` compares against it **by
identity** to suppress the mod id suffix while building tooltips for search. Pointing it at the
thread actually running the query is load-bearing, not cosmetic.

### Memoised mod names

`EmiUtil.getModName` resolved a namespace by walking the loader mod list, twice on a miss, then
allocating through `capitalizeFully`. Search baking calls it once per stack, so it ran many thousands
of times over a few dozen distinct namespaces. Mod names cannot change at runtime, so both the
display name and its lowercase form are cached.

The lowercase form deliberately uses default-locale `toLowerCase` rather than `Locale.ROOT`, matching
how every query lowercases its input. A mismatch would silently break mod name search wherever the
two disagree, such as the dotted and dotless I in a Turkish locale.

### Redundant work in the bake loop

Three changes inside the per-stack loop, which runs once per indexed stack (5018 on the test pack):

- `Identifier` permits only `[a-z0-9._-]` in a namespace and `[a-z0-9/._-]` in a path, verified
  against the `validNamespaceChar` and `validPathChar` source, so both are **already lowercase by
  construction**. Those two `toLowerCase` calls could never change a character and only cost a scan.
- Mod display names do contain uppercase, so that one genuinely allocates, hence the cache above.
- The enchanted book check went through `getItemStack`, which allocates a fresh `ItemStack` and wraps
  the item as a registry holder, to answer a question `getKey` answers directly. That was an
  allocation and a registry lookup per stack.

None of this alters what is displayed or matched.

## AE2 integration

Lives in `neoforge/src/main/java/dev/emi/emi/platform/neoforge/ae2/`:

- `Ae2EmiIntegration` is the plugin entry point and no-ops when AE2 is absent.
- `Ae2Recipes` pulls inscriber and charger recipes from AE2's own recipe types.
- `Ae2InscriberEmiRecipe` / `Ae2ChargerEmiRecipe` are the display recipes.
- `Ae2InscriberRecipeHandler` is what makes the plus button work: it implements EMI's
  `StandardRecipeHandler` against the inscriber menu, mapping top, middle and bottom slots so a
  recipe can be filled straight into an open inscriber rather than only into a crafting grid.

Being built in rather than a bridge mod means it survives AE2 or EMI updating independently, at the
cost of needing a recompile when AE2's recipe classes move.

## Upstream port

This builds on [link-fgfgui's 26.1 branch](https://github.com/link-fgfgui/emi). The commits before
`53c58033` are that porting work: JEI smithing recipe fallback, JEMI scroll grids, `IntEdit` text
position, EMI foreground draw order relative to slot and carried item extraction.
