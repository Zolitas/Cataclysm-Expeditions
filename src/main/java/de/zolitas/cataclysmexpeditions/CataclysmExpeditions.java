package de.zolitas.cataclysmexpeditions;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(CataclysmExpeditions.MODID)
public class CataclysmExpeditions {
  public static final String MODID = "cataclysm_expeditions";
  private static final Logger LOGGER = LogUtils.getLogger();

  public CataclysmExpeditions(IEventBus modEventBus, ModContainer modContainer) {
    LOGGER.info("Cataclysm Expeditions initialized!");
  }
}
