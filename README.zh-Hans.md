<p align="right">

[![en](https://img.shields.io/badge/lang-en-blue.svg)](README.md)
[![zh-Hans](https://img.shields.io/badge/lang-zh--Hans-red.svg)](README.zh-Hans.md)

</p>

# EtoileResurrection

用于 `ArcCreate` 和官谱谱面格式之间相互转换的命令行工具

## 使用方法

- `pack`: 将官谱文件打包成 **.arcpkg** 格式
- `export`: 解包 **.arcpkg** 文件，提取包内的背景文件、转换谱面格式、并自动生成 `songlist` 和 `packlist` 文件

### `export` 导出

使用 `EtoileResurrection export -h` 命令来获取帮助信息:

<details><summary>帮助信息</summary>

```
Usage: EtoileResurrection export [<options>] [<arcpkgs>]...

Options:
  -p, --prefix=<text>       曲目名称的前缀，用于对背景文件的自动重命名
  --export-bg-mode, --mode=(simplified|precise|overwrite|auto_rename)
                            请看下表
  -s, --pack, --set=<text>  导出到的目标曲包，默认为 single，即单曲曲包
  -v, --version=<text>      曲目更新版本，默认为 1.0
  -t, --time=<text>         曲目添加的日期，默认为系统当前时间，格式为 Unix 时间戳
  -o, --output=<path>       导出路径，默认为当前目录下的 result 文件夹内
  -h, --help                显示此信息并退出

Arguments:
  <arcpkgs>  要导出的单个 .arcpkg 文件路径

```

</details>

#### `--export-bg-mode`

| 背景导出策略               | 描述                                                                                      |
|----------------------|-----------------------------------------------------------------------------------------|
| simplified           | 忽略名称重复的背景文件，并发出警告                                                                       |
| precise              | 以树状结构提取背景文件                                                                             |
| overwrite            | 当有同名背景文件时，用新的背景覆盖写入，并发出警告                                                               |
| auto_rename __(\*)__ | 背景文件名将被添加前缀以避免重名问题，例如: 在 img/bg 文件夹中，**pragmatism.jpg** 将被更名为 **prefix.pragmatism.jpg** |

> __(\*)__: Default, and most commonly used.

#### 用例:

```
$ EtoileResurrection export N0N_ame.badapple.arcpkg --prefix default 
```

### `pack` 打包

使用 `EtoileResurrection pack -h` 命令来获取帮助信息:

<details><summary>帮助信息</summary>

```
Usage: EtoileResurrection pack [<options>] <songlist>

Options:
  -o, --outputDir=<path>     打包结果的输出目录，默认为当前目录
  -p, --prefix=<text>        曲目名称的前缀
  -s, --songId, --id=<text>  要打包的曲目 songId，必须存在于 songlist 中
  -re, --regex / --noregex   使用正则匹配 songId
  -h, --help                 显示此信息并退出

Arguments:
  <songlist>  要导出的曲目的 songlist 文件路径

```

</details>

#### `--regex`

使用 `--regex` 选项来正则匹配 songId，例如下面这个命令会将 **songlist_aprilfools** 中的所有曲目导出为单独的 .arcpkg 文件:

```
$ EtoileResurrection pack songs\songlist_aprilfools --songId=.* -re --prefix lowiro -o result\
Packed successfully to: .\result\lowiro.ignotusafterburn.arcpkg
Packed successfully to: .\result\lowiro.redandblueandgreen.arcpkg
Packed successfully to: .\result\lowiro.singularityvvvip.arcpkg
...
```

#### 用例:

```
$ EtoileResurrection pack songs\songlist --songId=mismal --prefix lowiro -o result\
```