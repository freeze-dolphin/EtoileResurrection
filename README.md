# EtoileResurrection

*Reborn of [Étoile](https://github.com/freeze-dolphin/EtoileLegacy)*

![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/freeze-dolphin/EtoileResurrection/build.yml)

---

[English](README.md) | [简体中文](README.zh-Hans.md)

---

Command-line tool for convertion of **.aff** charts between [`ArcCreate`](https://github.com/Arcthesia/ArcCreate) and the official Arcaea
format.

**Requires Java version ≥ 17**

## Download

Build artifacts filename：`EtoileResurrection.{interface}-{platform}-{hash}.zip`

- `interface`:
    - **Console**: Runs inside a terminal
    - **Swing**: GUI program powered by Swing framework
- `platform`:
    - **universal**: Launch script that runs well on most platforms
    - **win64**: Built with jlink + launch4j, runs on 64bit Windows
    - **win64-noJre**: Built with launch4j, runs on 64bit Windows, requires a Java 17+ installation

Download pre-built artifacts on [Releases](https://github.com/freeze-dolphin/EtoileResurrection/releases) page

## Features

### Available commands:

- `pack`: **(Arcaea -> ArcCreate)** to pack the official Arcaea chart format into ArcCreate **.arcpkg** files.
- `export`: **(ArcCreate -> Arcaea)** to unpack ArcCreate **.arcpkg** files, extract background images, convert chart format, and generate
  `songlist`, `packlist` files.
- `combine`: **(ArcCreate -> ArcCreate)** to combine multiple ArcCreate **.arcpkg** files into one according to `packlist`.
- `convert`: **(Bidirectional)** to convert a single chart file into specific format.

<details><summary>Technical features</summary>

#### Common

- [x] hitsound waveform files extraction
- [x] var-len ArcTaps convertion
- [x] ArcResolution conversion (Experimental)

#### `export`

- [x] audio, jacket, background files extraction
- [x] generation for `songlist` & `packlist` according to `project.arcproj`

#### `pack`

- [x] bundled background
- [x] generation for `project.arcproj` according to `songlist`
- [x] scenecontrol serialization (.sc.json generation)
- [x] track skinning logic from GameScene.cpp
- [ ] gray ArcNotes support (force gracePeriod)

</details>

## Usage

Use `EtoileResurrection <command> -h` to get help messages

Below is listing some detailed explanations of few complex arguments

### `pack`

#### `--regex`

Use `--regex` to make `songId` option regex matching, for example this packs all the songs separately in **songlist**:

```
$ EtoileResurrection pack songs\songlist_aprilfools --songId=.* -re --prefix lowiro -o result\
Packed successfully to: .\result\lowiro.ignotusafterburn.arcpkg
Packed successfully to: .\result\lowiro.redandblueandgreen.arcpkg
Packed successfully to: .\result\lowiro.singularityvvvip.arcpkg
...
```

#### Example:

```
$ EtoileResurrection pack songs\songlist --songId=mismal --prefix lowiro -o result\
```

### `export`

#### `--export-bg-mode`

This option defines how to export background files:

| Strategies           | Description                                                                                                                                                                     |
|----------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| simplified           | Ignore the duplicated files, and warn the user.                                                                                                                                 |
| precise              | Extract backgrounds files with tree structure, **which cannot be read directly in Arcaea**.                                                                                     |
| overwrite            | Overwrite the duplicated files with the new one, and warn the user.                                                                                                             |
| auto_rename __(\*)__ | Background files are qualified with pack prefix name to avoid conflicts, for example, **pragmatism.jpg** will be **prefix\_of\_the\_pack.pragmatism.jpg** in the `imgs` folder. |

> __(\*)__: By default

#### Example:

```
$ EtoileResurrection export N0N_ame.badapple.arcpkg --prefix default 
```
