package de.zolitas.cataclysmexpeditions.items;

import de.zolitas.cataclysmexpeditions.CataclysmExpeditions;
import de.zolitas.cataclysmexpeditions.blocks.BlocksRegister;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = CataclysmExpeditions.MODID)
public class ItemsRegister {
  public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CataclysmExpeditions.MODID);

  public static final DeferredItem<BlockItem> EXPEDITION_ANCHOR_ITEM = ITEMS.registerSimpleBlockItem(BlocksRegister.EXPEDITION_ANCHOR_BLOCK);
  public static final DeferredItem<BlockItem> RETURN_ANCHOR_ITEM = ITEMS.registerSimpleBlockItem(BlocksRegister.RETURN_ANCHOR_BLOCK);
  public static final DeferredItem<BlockItem> HUB_ANCHOR_ITEM = ITEMS.registerSimpleBlockItem(BlocksRegister.HUB_ANCHOR_BLOCK);

  public static final DeferredItem<BlockItem> EXPEDITION_PORTAL_ITEM = ITEMS.registerSimpleBlockItem(BlocksRegister.EXPEDITION_PORTAL_BLOCK);

  public static final DeferredItem<BlockItem> VOID_DEATH_ITEM = ITEMS.registerSimpleBlockItem(BlocksRegister.VOID_DEATH_BLOCK);

  @SubscribeEvent
  public static void addItemsToCreativeTabs(BuildCreativeModeTabContentsEvent event) {
    if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
      event.accept(HUB_ANCHOR_ITEM.get());
    }
  }
}
