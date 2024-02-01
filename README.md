> [!WARNING]
> This project is in early stage of development.
> Its use in production is not recommended.

<div align="center">
  <img alt="Cinnamon banner" src=".github/docs/pictures/cinnamon-banner.svg">
  <br/>
  <img alt="MIT License badge" src="https://img.shields.io/badge/License-MIT-blue">
</div>

# Overview
- [What is Cinnamon?](#what-is-cinnamon)
- [Building Cinnamon](#building-cinnamon)


# What is Cinnamon?
Cinnamon is a framework for [PaperMC](https://papermc.io/), which tries to allow
developers code "extended vanilla" experience, providing tools
for easier management of resource packs, creation of custom items
and even own custom blocks all in vanilla Minecraft!


# Building Cinnamon
Cinnamon has three modules: `:core`, `:api` and `:dummy-plugin`

- `:core` contains code of the Cinnamon plugin itself.
- `:api` contains the code of Cinnamon API. Cinnamon's core
  shades API in its own jar.
- `:dummy-plugin` contains the code of a "dummy" plugin, that utilizes
  Cinnamon API. It is a good example, if you want to see Cinnamon in
  live use.

To build the Cinnamon, run the next commands:
```shell
git clone https://github.com/Avatcher/Cinnamon.git
gradle build
```

Built jar locations:
- **Plugin**: `core/build/libs/cinnamon-${VERSION}.jar`
- **API**: `api/build/libs/cinnamon-api-${VERSION}.jar`
- **Dummy plugin**: `dummy-plugin/build/libs/cinnamon-dummy-${VERSION}.jar`
