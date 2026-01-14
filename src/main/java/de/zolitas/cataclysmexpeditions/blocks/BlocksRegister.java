package de.zolitas.cataclysmexpeditions.blocks;

import de.zolitas.cataclysmexpeditions.CataclysmExpeditions;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlocksRegister {
  public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CataclysmExpeditions.MODID);

  public static final DeferredBlock<ExpeditionAnchorBlock> EXPEDITION_ANCHOR_BLOCK = BLOCKS.register("expedition_anchor", () -> new ExpeditionAnchorBlock());
  public static final DeferredBlock<ReturnAnchorBlock> RETURN_ANCHOR_BLOCK = BLOCKS.register("return_anchor", ReturnAnchorBlock::new);
  public static final DeferredBlock<ExpeditionAnchorBlock> HUB_ANCHOR_BLOCK = BLOCKS.register("hub_anchor", HubAnchorBlock::new);

  public static final DeferredBlock<ExpeditionPortalBlock> EXPEDITION_PORTAL_BLOCK = BLOCKS.register("expedition_portal", ExpeditionPortalBlock::new);
}
