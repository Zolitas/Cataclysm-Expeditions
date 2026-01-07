package de.zolitas.cataclysmexpeditions.blocks;

import de.zolitas.cataclysmexpeditions.CataclysmExpeditions;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlocksRegister {
  public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CataclysmExpeditions.MODID);
  public static final DeferredBlock<ExpeditionAnchorBlock> EXPEDITION_ANCHOR_BLOCK = BLOCKS.register("expedition_anchor", ExpeditionAnchorBlock::new);
}
