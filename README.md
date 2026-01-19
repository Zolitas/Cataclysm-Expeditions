# Cataclysm Expeditions
Cataclysm Expeditions is an addon mod for L_Ender's Cataclysm. It removes the normal structure generation and adds a hub world where players can group up and generate the structures in a rpg style fashion.

You start in this mod by crafting a Hub Anchor which can take you to the hub.
(If you are a server owner or modpack dev and disable this recipe in the config, you have to provide your own way of getting to the hub)

In the hub you can find several portals that lead to the structures.
When you enter a portal, the structure will generate fresh in a dedicated dimension.
Once ready, other players have 30 seconds (configurable) to join your lobby before being teleported together.

# Config

```toml
#Maximum number of players allowed in an expedition. The UI might not look good with more than 4 players
# Default: 4
# Range: 1 ~ 100
maxExpeditionPlayerCount = 4
#Number of ticks that an expedition should be open for others to join. Is displayed as seconds ingame
# Default: 600
# Range: > 1
expeditionLobbyTtl = 600
#Number of ticks that a player has to access the same one again. Is displayed as hours, minutes and seconds ingame
# Default: 1728000
# Range: > 1
expeditionCooldown = 1728000
#Distance between the expedition structures in chunks
# Default: 100
# Range: > 10
distanceBetweenExpeditionStructures = 100
#Enables the recipe for the hub anchor that is needed to enter the hub. If you disable this setting, you will have to implement another way to get to the hub yourself
hubAnchorRecipeEnabled = true
#Number of chunks of a structure that are generated in one batch. Higher values lead to longer loading times but also lower lag spikes
# Default: 2
# Range: 1 ~ 5
structureGenerationBatchSize = 2

```
