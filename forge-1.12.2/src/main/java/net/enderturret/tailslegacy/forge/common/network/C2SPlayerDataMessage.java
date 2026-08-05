/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.common.network;

import java.util.UUID;

import org.jetbrains.annotations.ApiStatus.Internal;

import io.netty.buffer.ByteBuf;

import net.minecraft.entity.player.EntityPlayerMP;

import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import net.enderturret.tailslegacy.common.network.BaseC2SPlayerDataMessage;
import net.enderturret.tailslegacy.common.part.PartsData;
import net.enderturret.tailslegacy.common.part.ServerPlayerPartManager;

public final class C2SPlayerDataMessage implements BaseC2SPlayerDataMessage, IMessage {

	private PartsData partsData;

	public C2SPlayerDataMessage() {}

	public C2SPlayerDataMessage(PartsData partsData) {
		this.partsData = partsData;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		partsData = BaseC2SPlayerDataMessage.decodeJson(ByteBufUtils.readUTF8String(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, BaseC2SPlayerDataMessage.encodeJson(partsData));
	}

	@Internal
	public static final class Handler implements IMessageHandler<C2SPlayerDataMessage, IMessage> {

		@Override
		public IMessage onMessage(C2SPlayerDataMessage message, MessageContext context) {
			if (message.partsData == null) return null;

			final EntityPlayerMP sender = context.getServerHandler().player;
			final UUID uuid = sender.getUniqueID();

			ServerPlayerPartManager.get().set(uuid, message.partsData);

			// Tell other clients about the change.
			final S2CPlayerDataMessage msg = new S2CPlayerDataMessage(uuid, message.partsData);
			for (EntityPlayerMP player : sender.server.getPlayerList().getPlayers())
				if (player != sender)
					TailsNetworkManager.get().sendTo(msg, player);

			return null;
		}
	}
}