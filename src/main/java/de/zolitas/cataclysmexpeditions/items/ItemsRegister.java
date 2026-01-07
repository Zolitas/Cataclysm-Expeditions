package de.zolitas.cataclysmexpeditions.items;

import de.zolitas.cataclysmexpeditions.CataclysmExpeditions;
import de.zolitas.cataclysmexpeditions.blocks.BlocksRegister;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemsRegister {
  public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CataclysmExpeditions.MODID);
  public static final DeferredItem<BlockItem> EXPEDITION_ANCHOR_ITEM = ITEMS.registerSimpleBlockItem(BlocksRegister.EXPEDITION_ANCHOR_BLOCK);
}
