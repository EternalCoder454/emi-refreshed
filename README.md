# EMI Refreshed, NeoForge 26.1.2 port

> **Original author: [Emi](https://github.com/emilyploszaj/emi)**
> EMI is Emi's work and all credit for it belongs to them. This is an unofficial port, not
> affiliated with or endorsed by the author.
>
> Built on top of [link-fgfgui's 26.1 branch](https://github.com/link-fgfgui/emi), whose porting work
> this builds directly on.
>
> **Licence: MIT** (see `LICENSE`), which permits this fork and its redistribution.

## What this fork changes

- Runs on **NeoForge 26.1.2** / Java 25.
- Search runs on a dedicated single thread executor rather than spawning a thread per query, and the
  bake loop skips work that the `Identifier` contract already guarantees.
- Mod name lookups cached, using the default locale deliberately so it matches query side casing.
- **AE2 integration built in**: inscriber and charger categories, with recipe transfer so the plus
  button fills an inscriber directly. Guarded so it is inert without AE2 installed.
- Search bake timing logged, so changes can be measured rather than guessed at.
---

# EMI
EMI is a featureful and accessible item and recipe viewer for Minecraft.

![EMI Interface](https://user-images.githubusercontent.com/14813658/224562247-1588064e-39ef-475a-9108-d7a357af6939.png)

![Recipe Tree](https://user-images.githubusercontent.com/14813658/224562258-1a5ee67a-fd7f-489f-9eed-ae67c184ddac.png)

## Developers
To add EMI to your project as a dependency you need to add the following to your `build.gradle`:
```gradle
repositories {
	maven {
		name = "TerraformersMC"
		url = "https://maven.terraformersmc.com/"
	}
}
```

How EMI gets added to your dependencies varies based on modloader and setup.
The Gradle property `emi_version` should be something like `1.0.0+1.19.4` with EMI's version and Minecraft's version.
Here are common dependency setups for different loaders and build systems.

```gradle
dependencies {
	// Fabric
	modCompileOnly "dev.emi:emi-fabric:${emi_version}:api"
	modLocalRuntime "dev.emi:emi-fabric:${emi_version}"

	// Forge (see below block as well if you use Forge Gradle)
	compileOnly fg.deobf("dev.emi:emi-forge:${emi_version}:api")
	runtimeOnly fg.deobf("dev.emi:emi-forge:${emi_version}") 

	// NeoForge
	compileOnly "dev.emi:emi-neoforge:${emi_version}:api"
	runtimeOnly "dev.emi:emi-neoforge:${emi_version}" 

	// Architectury
	modCompileOnly "dev.emi:emi-xplat-intermediary:${emi_version}:api"

	// MultiLoader Template/VanillaGradle
	compileOnly "dev.emi:emi-xplat-mojmap:${emi_version}:api"
}
```

For Forge Gradle users, you will need to enable Mixin refmaps in your client sourceset. This can be done by adding 2 lines inside of your client runs, to look like below.

```gradle
runs {
	client {
		// Add these two lines
		property 'mixin.env.remapRefMap', 'true'
		property 'mixin.env.refMapRemappingFile', "${projectDir}/build/createSrgToMcp/output.srg"

		// The rest of the code that was already here
		// ...
	}
}
```
