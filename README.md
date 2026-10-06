# Spike BTA 8.0.1 | Warning: All versions of the mod are considered unstable.

## All
* ### Features:
* MixinExtras 0.5.5
* ### Fix:
* Languages from mods (any except en_US) are loaded again.
* Leaf decay radius 4 → 7 + BFS search, fancytree foliage still finds logs after player chunk updates.
* WeatherChunkLoad (StackOverflowError).
* MC-2025.
* Chat command: moving the cursor no longer crashes or kicks the player.
* ### Optimizations:
* PacketBlockRegionUpdate fast compression.
## Server side
* ### Features:
* Spectator players are not sent to non-spectators. Works even for clients without Spike.
* ### Fix:
* Fix unban username.
* ban ip / unban ip accept IPv6.
* ip ban check on login correctly handles IPv6 (vanilla cut at first ':').
* server.properties/default-gamemode.
* New players can use creative inventory when default-gamemode is creative.
* Recursion protection disabled (clipped structures).
* world-type: Nether now receives the matching world type (e.g. skyblock/beta_173).
* Player spawn.
* ### Optimizations:
* Spawn chunks disabled.
* Async terrain generation. Decorate stays on main thread.
* Chunk send order better after teleports/login.
## Client side
* ### Features:
* Lower minimum OpenGL 4.1 → 3.3 (old GPU support).
* Spectator players are invisible to non-spectators; other spectators see them translucent.
* Support launch argument `--accessToken` (alias for `--session`).
* ### Fix:
* Language pack GUI: when several packs share the same id, only the selected one is highlighted and loaded (selection stored in options.txt as languagePackKey).
* Fix subtitles for rubyglass-blocks.
* Fix read server icons.
* Proper text cursor behavior (e.g., IPv6 input).
* LAN server list shows a clean host:port (IPv4-mapped / zone id).
* CameraFrustum.cubeInFrustum now respects FRUSTUM_CULLING option.
