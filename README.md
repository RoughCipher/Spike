# Spike BTA 8.0.1 | Warning: All versions of the mod are considered unstable.

## All
* ### Features:
* MixinExtras 0.5.5
* ### Fix:
* Languages from mods (any except en_US) are loaded again.
* Leaf decay radius 4 -> 7 + BFS search, fancytree foliage still finds logs after player chunk updates.
* WeatherChunkLoad (StackOverflowError).
* ### Optimizations:
* PacketBlockRegionUpdate fast compression.
## Server side
* ### Fix:
* Fix unban username.
* ban ip / unban ip accept IPv6.
* ip ban check on login correctly handles IPv6 (vanilla cut at first ':').
* server.properties/default-gamemode.
* New players can use creative inventory when default-gamemode is creative.
* Recursion protection disabled (clipped structures).
* Spectator players are not sent to non-spectators. Works even for clients without Spike.
* ### Optimizations:
* Spawn chunks disabled.
* Async terrain generation. Decorate stays on main thread.
* Chunk send order better after teleports/login.
## Client side
* ### Fix:
* Fix subtitles for rubyglass-blocks.
* Fix read server icons.
* Proper text cursor behavior (e.g., IPv6 input).
* LAN server list shows a clean host:port (IPv4-mapped / zone id).
* CameraFrustum.cubeInFrustum now respects FRUSTUM_CULLING option.
* Spectator players are invisible to non-spectators other spectators see them translucent.
