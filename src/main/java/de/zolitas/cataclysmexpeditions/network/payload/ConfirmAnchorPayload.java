package de.zolitas.cataclysmexpeditions.network.payload;

import de.zolitas.cataclysmexpeditions.CataclysmExpeditions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class ConfirmAnchorPayload implements CustomPacketPayload {
  @Getter
  private final BlockPos anchorPos;
  
  public static final Type<ConfirmAnchorPayload> TYPE = new Type<>(
      ResourceLocation.fromNamespaceAndPath(CataclysmExpeditions.MODID, "confirm_anchor"));

  public static final StreamCodec<RegistryFriendlyByteBuf, ConfirmAnchorPayload> CODEC = new StreamCodec<>() {
    @Override
    public @NotNull ConfirmAnchorPayload decode(RegistryFriendlyByteBuf buf) {
      BlockPos pos = buf.readBlockPos();
      return new ConfirmAnchorPayload(pos);
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf, ConfirmAnchorPayload payload) {
      buf.writeBlockPos(payload.getAnchorPos());
    }
  };

  @Override
  public @NotNull Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
