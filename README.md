# Spike BTA 8.0.1

## Core
* ### Features:
* -
* ### Optimizations:
* PacketBlockRegionUpdate fast compression.
## Server side
* ### Fix:
* fix unban username.
* ban ip / unban ip accept IPv6.
* ip ban check on login correctly handles IPv6 (vanilla cut at first ':').
* server.properties/default-gamemode.
* Recursion protection disabled (clipped structures) - **Traveling in the Nether causes noticeable server freezes**.
* ### Optimizations:
* Increased server lag warning timeout - flooding log.
* Spawn chunks disabled.
## Client side
* ### Fix:
* Proper text cursor behavior (e.g., IPv6 input).
* LAN server list shows a clean host:port (IPv4-mapped / zone id).
