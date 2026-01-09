package de.zolitas.cataclysmexpeditions.entities;

import com.mojang.serialization.Codec;
import de.zolitas.cataclysmexpeditions.CataclysmExpeditions;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class AttachmentTypesRegister {
  public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, CataclysmExpeditions.MODID);
  public static final Supplier<AttachmentType<Integer>> EXPEDITION_PORTAL_COOLDOWN = ATTACHMENT_TYPES.register(
      "expedition_portal_cooldown",
      () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build()
  );
}
