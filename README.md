# Spike BTA 8.0.1

## Core
* ### Fix:
* Languages from mods (any except en_US) are loaded again.
* Leaf decay radius 4 -> 7 fancytree foliage still finds logs after player chunk updates.
* ### Features:
* -
* ### Optimizations:
* PacketBlockRegionUpdate fast compression.
## Server side
* ### Fix:
* Fix unban username.
* ban ip / unban ip accept IPv6.
* ip ban check on login correctly handles IPv6 (vanilla cut at first ':').
* server.properties/default-gamemode.
* New players can use creative inventory when default-gamemode is creative.
* Recursion protection disabled (clipped structures)
* ### Optimizations:
* Increased server lag warning timeout - flooding log.
* Spawn chunks disabled.
## Client side
* ### Fix:
* Fix subtitles for rubyglass-blocks.
* Fix read server icons.
* Proper text cursor behavior (e.g., IPv6 input).
* LAN server list shows a clean host:port (IPv4-mapped / zone id).
