package de.zolitas.cataclysmexpeditions;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(CataclysmExpeditions.MODID)
public class CataclysmExpeditions {
  public static final String MODID = "cataclysm_expeditions";
  private static final Logger LOGGER = LogUtils.getLogger();

  public static final ResourceLocation EXPEDITION_DIMENSION_LOCATION = ResourceLocation.fromNamespaceAndPath(MODID, "expedition_world");
  public static final ResourceLocation EXPEDITION_DIMENSION_LOCATION_NETHER = ResourceLocation.fromNamespaceAndPath(MODID, "expedition_world_nether");

  public CataclysmExpeditions(IEventBus modEventBus, ModContainer modContainer) {
    LOGGER.info("Cataclysm Expeditions initialized!");
  }
}
