package de.zolitas.cataclysmexpeditions.world;

import de.zolitas.cataclysmexpeditions.CataclysmExpeditions;
import de.zolitas.cataclysmexpeditions.expeditions.Expedition;
import lombok.Getter;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ExpeditionWorldSavedData extends SavedData {
  @Getter
  private int expeditionCounter = 0;

  @Getter
  private boolean hubGenerated = false;

  private Map<Expedition, String> lobbyDisplayUUIDs = new HashMap<>();

  private static final ResourceLocation expeditionCounterKey = ResourceLocation.fromNamespaceAndPath(CataclysmExpeditions.MODID, "expedition_counter");
  private static final ResourceLocation hubGeneratedKey = ResourceLocation.fromNamespaceAndPath(CataclysmExpeditions.MODID, "hub_generated");
  private static final ResourceLocation lobbyDisplayUUIDsKey = ResourceLocation.fromNamespaceAndPath(CataclysmExpeditions.MODID, "lobby_display_uuids");

  public void setHubGenerated(boolean hubGenerated) {
    this.hubGenerated = hubGenerated;
    setDirty();
  }

  public void setExpeditionCounter(int expeditionCounter) {
    this.expeditionCounter = expeditionCounter;
    setDirty();
  }

  public void setLobbyDisplay(Expedition expedition, String lobbyDisplayUUID) {
    lobbyDisplayUUIDs.put(expedition, lobbyDisplayUUID);
    setDirty();
  }

  public String getLobbyDisplayUUID(Expedition expedition) {
    return lobbyDisplayUUIDs.get(expedition);
  }

  public void incrementExpeditionCounter() {
    setExpeditionCounter(expeditionCounter + 1);
  }

  public static ExpeditionWorldSavedData load(CompoundTag tag, HolderLookup.@NotNull Provider registries) {
    ExpeditionWorldSavedData data = new ExpeditionWorldSavedData();
    data.expeditionCounter = tag.getInt(expeditionCounterKey.toString());
    data.hubGenerated = tag.getBoolean(hubGeneratedKey.toString());

    data.lobbyDisplayUUIDs = new HashMap<>();

    CompoundTag lobbyDisplayUUIDsTag = tag.getCompound(lobbyDisplayUUIDsKey.toString());
    lobbyDisplayUUIDsTag.getAllKeys().forEach(key -> {
      data.lobbyDisplayUUIDs.put(Expedition.getById(key), lobbyDisplayUUIDsTag.getString(key));
    });

    return data;
  }

  @Override
  public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
    tag.putInt(expeditionCounterKey.toString(), expeditionCounter);
    tag.putBoolean(hubGeneratedKey.toString(), hubGenerated);

    CompoundTag lobbyDisplaysTag = new CompoundTag();
    lobbyDisplayUUIDs.forEach((expedition, textDisplayUUID) -> {
      lobbyDisplaysTag.putString(expedition.getId(), textDisplayUUID);
    });
    tag.put(lobbyDisplayUUIDsKey.toString(), lobbyDisplaysTag);

    return tag;
  }
}
