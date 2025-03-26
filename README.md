# EtoileResurrection

Command-line tool for convertion of **.aff** charts between [`ArcCreate`](https://github.com/Arcthesia/ArcCreate) and the official Arcaea
format.

## Usage

There are two command currenly:

- `pack`: To pack the official Arcaea chart format into ArcCreate **.arcpkg** files.
- `export`: To unpack ArcCreate **.arcpkg** files, extract background images, convert chart format, and generate `songlist`, `packlist`
  file.

### `export`

Use `etoile export -h` to get help message:

<details><summary>Help</summary>

```
Usage: etoile export [<options>] [<arcpkgs>]...

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

Example:

```
EtoileRessurrection export N0N_ame.badapple.arcpkg --prefix default 
```

### `pack`

Use `etoile pack -h` to get help message:

<details><summary>Help</summary>

```
Usage: etoile pack [<options>] <songsdir>

Options:
  -o, --outputDir=<path>     The path to the .arcpkg file to be packed
  -p, --prefix=<text>        The prefix of the song id
  -s, --songId, --id=<text>  The identity of the song to be packed
  -c, --constants=<float>    The list of song constants
  -h, --help                 Show this message and exit

Arguments:
  <songsdir>  songs dir to be processed on

```

</details>

#### a

a