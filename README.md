# Just Enough Spirits

A [JEI](https://www.curseforge.com/minecraft/mc-mods/jei) integration for [Malum](https://www.curseforge.com/minecraft/mc-mods/malum) that adds a **Spirit Drops** category showing which mobs drop which spirits.

For each mob that has spirit drop data, the category renders the living mob, its spawn egg (as a lookup ingredient), and every spirit it drops. Look up a spirit to see which mobs drop it, or look up a mob's spawn egg to see its spirits.

## Requirements

- Minecraft 1.21.1
- NeoForge
- [Malum](https://www.curseforge.com/minecraft/mc-mods/malum) (`1.8.2`+) and its dependencies (Lodestone, Curios)
- [JEI](https://www.curseforge.com/minecraft/mc-mods/jei) (`19.x`+) — client only

Spirit drop data is read from Malum and synced to clients on connect and on `/reload`, so datapack changes to spirit drops are reflected without a client restart. The mod's display is client-side only and JEI is not required on servers; installing the mod on a server enables live sync of datapack changes to connected clients.

## Building

```bash
./gradlew build
```

The mod jar is produced in `build/libs/`.

## License

MIT — see [LICENSE](LICENSE).
