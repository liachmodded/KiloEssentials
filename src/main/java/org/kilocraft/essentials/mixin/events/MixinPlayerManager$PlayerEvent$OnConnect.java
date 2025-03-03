package org.kilocraft.essentials.mixin.events;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Logger;
import org.kilocraft.essentials.api.KiloServer;
import org.kilocraft.essentials.events.player.PlayerConnectEventImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class MixinPlayerManager$PlayerEvent$OnConnect {

    @Shadow @Final private static Logger LOGGER;

    @Inject(at = @At("HEAD"), method = "onPlayerConnect", cancellable = true)
    private void oky$onPlayerConnect(ClientConnection connection, ServerPlayerEntity playerEntity, CallbackInfo ci) {
        PlayerConnectEventImpl e = KiloServer.getServer().triggerEvent(
                new PlayerConnectEventImpl(
                        connection,
                        playerEntity
                )
        );

        if (e.isCancelled()) {
            connection.disconnect(new LiteralText(e.getCancelReason()));
            ci.cancel();
        }

        LOGGER.info("{} with a entity id of \"{}\" joined the server.", playerEntity.getName(), playerEntity.getEntityId());

    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendToAll(Lnet/minecraft/text/Text;)V"), method = "onPlayerConnect")
    private void oky$onPlayerConnect$sendToAll(PlayerManager playerManager, Text text_1) {

    }
}
