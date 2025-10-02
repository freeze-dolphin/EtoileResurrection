# EtoileResurrection
*Reborn of [Etoile](https://github.com/freeze-dolphin/EtoileLegacy)*

---

[English](README.md) | [简体中文](README.zh-Hans.md)

---

Command-line tool for convertion of **.aff** charts between [`ArcCreate`](https://github.com/Arcthesia/ArcCreate) and the official Arcaea
format.

Available commands:

- `export`: **(ArcCreate -> Arcaea)** to unpack ArcCreate **.arcpkg** files, extract background images, convert chart format, and generate
  `songlist`, `packlist`
  file.
- `pack`: **(Arcaea -> ArcCreate)** to pack the official Arcaea chart format into ArcCreate **.arcpkg** files.

## Features

### Common

- [x] hitsound waveform files extraction
- [x] var-len ArcTaps convertion
- [x] ArcResolution conversion (Experimental)

### `export`

- [x] audio, jacket, background files extraction
- [x] generation for `songlist` & `packlist` according to `project.arcproj`

### `pack`

- [x] bundled background
- [x] generation for `project.arcproj` according to `songlist`
- [x] scenecontrol serialization (.sc.json generation)
- [x] track skinning logic from GameScene.cpp
- [ ] gray ArcNotes support (force gracePeriod)

## Usage

### `export`

Use `EtoileResurrection export -h` to get help message:

<details><summary>Help</summary>

```
Usage: EtoileResurrection export [<options>] [<arcpkgs>]...

Options:
  -p, --prefix=<text>       The prefix of the song id
  --export-bg-mode, --mode=(simplified|precise|overwrite|auto_rename)
                            Please refer to the README file
  -s, --pack, --set=<text>  The name of the pack to export, defaults to single
  -v, --version=<text>      The version of the songs, defaults to 1.0
  -t, --time=<text>         The time when these songs are added, defaults to
                            current system time
  -o, --output=<path>       The output of the song output, defaults to
                            './result'
  -h, --help                Show this message and exit

Arguments:
  <arcpkgs>  .arcpkg files to be processed on

```

</details>

#### `--export-bg-mode`

| Strategies           | Description                                                                                                                                                                       |
|----------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| simplified           | Ignore the duplicated files, and warn the user.                                                                                                                                   |
| precise              | Extract backgrounds files with tree structure, **which cannot be read directly in Arcaea**.                                                                                       |
| overwrite            | Overwrite the duplicated files with the new one, and warn the user.                                                                                                               |
| auto_rename __(\*)__ | Background files are qualified with pack prefix name to avoid conflicts, for example, **pragmatism.jpg** will be **prefix\_of\_the\_pack.pragmatism.jpg** in the **imgs** folder. |

> __(\*)__: Default, and most commonly used.

#### Example:

```
$ EtoileResurrection export N0N_ame.badapple.arcpkg --prefix default 
```

### `pack`

Use `EtoileResurrection pack -h` to get help message:

<details><summary>Help</summary>

```
Usage: EtoileResurrection pack [<options>] <songlist>

Options:
  -o, --outputDir=<path>     The output path of the result
  -p, --prefix=<text>        The prefix of the song id
  -s, --songId, --id=<text>  The identity of the song to be packed
  -re, --regex / --noregex   Enable regex matching mode for songId
  -h, --help                 Show this message and exit

Arguments:
  <songlist>  songlist file to be processed on

```

</details>

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