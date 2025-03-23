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
Usage: etoile export [<options>] [<arcpkgs>]... <prefix>

Options:
  --export-bg-mode, --mode=(simplified|precise|overwrite|auto_rename)
  -p, --pack=<text>     The name of the pack to export, defaults to single
  -v, --version=<text>  The version of the songs, defaults to 1.0
  -t, --time=<text>     The time when these songs are added, defaults to
                        current system time
  -h, --help            Show this message and exit

Arguments:
  <arcpkgs>
  <prefix>   The prefix of the song id

```

</details>

#### `--export-bg-mode`

| Strategies           | Description                                                                                                                                                                       |
|----------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| simplified           | Ignore the duplicated files, and warn the user.                                                                                                                                   |
| precise              | Extract backgrounds files with tree structure, **which cannot be read directly in Arcaea**.                                                                                       |
| overwrite            | Overwrite the duplicated files with the new one, and warn the user.                                                                                                               |
| auto_rename __(\*)__ | Background files are qualified with pack prefix name to avoid conflicts, for example, **pragmatism.jpg** will be **prefix\_of\_the\_pack.pragmatism.jpg** in the **imgs** folder. |

> __(\*)__: Most commonly used.

### `pack`

todo

<!--

Use `etoile pack -h` to get help message:


<details><summary>Help</summary>

```

```

</details>

-->