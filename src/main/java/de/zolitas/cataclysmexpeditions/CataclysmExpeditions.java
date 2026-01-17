package de.zolitas.cataclysmexpeditions;

import com.mojang.logging.LogUtils;
import de.zolitas.cataclysmexpeditions.blocks.BlocksRegister;
import de.zolitas.cataclysmexpeditions.config.CataclysmExpeditionsConfig;
import de.zolitas.cataclysmexpeditions.entities.AttachmentTypesRegister;
import de.zolitas.cataclysmexpeditions.items.ItemsRegister;
import de.zolitas.cataclysmexpeditions.network.NetworkRegister;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(CataclysmExpeditions.MODID)
public class CataclysmExpeditions {
  public static final String MODID = "cataclysm_expeditions";
  private static final Logger LOGGER = LogUtils.getLogger();

  public CataclysmExpeditions(IEventBus modEventBus, ModContainer modContainer) {
    LOGGER.info("Cataclysm Expeditions initialized!");

    modContainer.registerConfig(ModConfig.Type.SERVER, CataclysmExpeditionsConfig.CONFIG_SPEC);

    BlocksRegister.BLOCKS.register(modEventBus);
    ItemsRegister.ITEMS.register(modEventBus);
    AttachmentTypesRegister.ATTACHMENT_TYPES.register(modEventBus);

    // Register networking payloads
    modEventBus.addListener(NetworkRegister::register);
  }
}
