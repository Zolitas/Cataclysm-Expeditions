package de.zolitas.cataclysmexpeditions.blocks;

import de.zolitas.cataclysmexpeditions.CataclysmExpeditions;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static de.zolitas.cataclysmexpeditions.items.ItemsRegister.ITEMS;

public class BlocksRegister {
  public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CataclysmExpeditions.MODID);
  public static final DeferredBlock<ExpeditionAnchorBlock> EXPEDITION_ANCHOR_BLOCK = BLOCKS.register("expedition_anchor", ExpeditionAnchorBlock::new);
  public static final DeferredItem<BlockItem> EXPEDITION_ANCHOR_ITEM = ITEMS.registerSimpleBlockItem(EXPEDITION_ANCHOR_BLOCK);
}
