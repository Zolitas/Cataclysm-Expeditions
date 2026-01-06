package de.zolitas.cataclysmexpeditions.world;

import de.zolitas.cataclysmexpeditions.CataclysmExpeditions;
import lombok.Getter;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public class ExpeditionWorldSavedData extends SavedData {
  @Getter
  private int expeditionCounter = 0;

  @Getter
  private boolean hubGenerated = false;

  private static final ResourceLocation expeditionCounterKey = ResourceLocation.fromNamespaceAndPath(CataclysmExpeditions.MODID, "expedition_counter");
  private static final ResourceLocation hubGeneratedKey = ResourceLocation.fromNamespaceAndPath(CataclysmExpeditions.MODID, "hub_generated");

  public void setHubGenerated(boolean hubGenerated) {
    this.hubGenerated = hubGenerated;
    setDirty();
  }

  public void setExpeditionCounter(int expeditionCounter) {
    this.expeditionCounter = expeditionCounter;
    setDirty();
  }

  public void incrementExpeditionCounter() {
    setExpeditionCounter(expeditionCounter + 1);
  }

  public static ExpeditionWorldSavedData load(CompoundTag tag, HolderLookup.@NotNull Provider registries) {
    ExpeditionWorldSavedData data = new ExpeditionWorldSavedData();
    data.expeditionCounter = tag.getInt(expeditionCounterKey.toString());
    data.hubGenerated = tag.getBoolean(hubGeneratedKey.toString());
    return data;
  }

  @Override
  public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
    tag.putInt(expeditionCounterKey.toString(), expeditionCounter);
    tag.putBoolean(hubGeneratedKey.toString(), hubGenerated);
    return tag;
  }
}
