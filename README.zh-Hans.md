# EtoileResurrection

*Reborn of [Étoile](https://github.com/freeze-dolphin/EtoileLegacy)*

![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/freeze-dolphin/EtoileResurrection/build.yml)

---

[English](README.md) | [简体中文](README.zh-Hans.md)

---

用于 `ArcCreate` 和官谱谱面格式之间相互转换的命令行工具

**Java 版本要求：≥ 17**

## 下载

构建产物命名格式：`EtoileResurrection.{interface}-{platform}-{hash}.zip`

- `interface`:
    - **Console**: 控制台版本，适合批量处理或嵌入其他程序运行
    - **Swing**: 使用 Swing 框架构建的窗口程序
- `platform`:
    - **universal**: 通过启动脚本自动搜索主机上安装的 Java 并运行程序
    - **win64**: 使用 jlink + launch4j 构建的、适用于 64位 Windows 操作系统的版本
    - **win64-noJre**: 使用 launch4j 构建的、适用于 64位 Windows 操作系统的版本，需要安装 Java 才可运行

请在 [Releases](https://github.com/freeze-dolphin/EtoileResurrection/releases) 页面选择合适的版本下载

## 功能（指令）

- `pack`: 将官谱文件打包成 **.arcpkg** 格式
- `export`: 解包 **.arcpkg** 文件，提取包内的背景文件、转换谱面格式、并自动生成 `songlist` 和 `packlist` 文件
- `combine`: 根据 `packlist` 文件将多个 **.arcpkg** 单曲档案合并为单个 **.arcpkg** 曲包
- `convert`: 将单个 **.aff** 谱面文件转换至指定格式（可用格式为：Arcaea 格式、ArcCreate 格式）

## 用法

使用 `EtoileResurrection <command> -h` 命令来获取帮助信息

以下是一些较复杂参数的详细解释

### `pack` 打包

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

### `export` 导出

使用 `EtoileResurrection export -h` 命令来获取帮助信息:

#### `--export-bg-mode`

| 背景导出策略               | 描述                                                                                      |
|----------------------|-----------------------------------------------------------------------------------------|
| simplified           | 忽略名称重复的背景文件，并发出警告                                                                       |
| precise              | 以树状结构提取背景文件                                                                             |
| overwrite            | 当有同名背景文件时，用新的背景覆盖写入，并发出警告                                                               |
| auto_rename __(\*)__ | 背景文件名将被添加前缀以避免重名问题，例如: 在 img/bg 文件夹中，**pragmatism.jpg** 将被更名为 **prefix.pragmatism.jpg** |

> __(\*)__: 默认行为

#### 用例:

```
$ EtoileResurrection export N0N_ame.badapple.arcpkg --prefix default 
```