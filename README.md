# OMMS Connect Desktop Compose

[![Kotlin](https://img.shields.io/badge/5714_lines-Kotlin-7954F6?logo=kotlin)](https://kotlinlang.org/)
[![Compose Multiplatform](https://img.shields.io/badge/Compose_Multiplatform_UI-5383EC?logo=jetpackcompose&logoColor=ffffff)](https://www.jetbrains.com/lp/compose-multiplatform/)

[![GitHub downloads](https://img.shields.io/github/downloads/OhMyMinecraftServer/OMMS-Connect-Desktop-Compose/total?label=Github%20Downloads&logo=github)](https://github.com/OhMyMinecraftServer/OMMS-Connect-Desktop-Compose/releases)
![GitHub Repo stars](https://img.shields.io/github/stars/OhMyMinecraftServer/OMMS-Connect-Desktop-Compose)


**连接到OMMS的桌面客户端**

<!-- TOC -->
* [OMMS Connect Desktop Compose](#omms-connect-desktop-compose)
  * [下载](#下载)
    * [版本说明](#版本说明)
  * [自行编译](#自行编译)
    * [JDK](#jdk)
    * [编译](#编译)
<!-- TOC -->

## 下载

-\> [最新发布版](https://github.com/OhMyMinecraftServer/OMMS-Connect-Desktop-Compose/releases/latest)

-\> [所有版本](https://github.com/OhMyMinecraftServer/OMMS-Connect-Desktop-Compose/releases)

### 版本说明
|                                                  发布版本                                                  | 核心版本  | 协议版本[^1] | 对应 OMMS Central 版本[^2] |
|:------------------------------------------------------------------------------------------------------:|:-----:|:--------:|:----------------------:|
|   [v1.2.x](https://github.com/OhMyMinecraftServer/OMMS-Connect-Desktop-Compose/releases/tag/v1.2.0)    | 1.7.0 |  0x0012  |         1.7.0+         |
|   [v1.1.x](https://github.com/OhMyMinecraftServer/OMMS-Connect-Desktop-Compose/releases/tag/v1.1.4)    | 1.5.0 |  0x0011  |     1.5.0 - 1.5.3      |
| [v1.0.x](https://github.com/OhMyMinecraftServer/OMMS-Connect-Desktop-Compose/releases/tag/v1.0.0-beta) | 1.1.2 |  0x0007  |     0.16.1 - 1.2.0     |

## 自行编译

### JDK

本项目使用了[`jbr (JetBrains Runtime)`](https://github.com/JetBrains/JetBrainsRuntime)的特殊功能，因此必须使用`jbr-17`。

(或许可以使用`jbr-21`，但未经测试。)

### 编译

使用`Gradle`任务`packageDistributionForCurrentOS`编译当前系统的二进制分发

- - -

[^1]: 完整协议版本为 `0xc0000000 + ${协议版本}`

[^2]: 信息来自 OMMS Central 开发者，准确性由其负责
