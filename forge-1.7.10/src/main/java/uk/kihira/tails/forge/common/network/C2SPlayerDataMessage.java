/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.common.network;

import java.util.UUID;

import org.jetbrains.annotations.ApiStatus.Internal;

import io.netty.buffer.ByteBuf;

import net.minecraft.entity.player.EntityPlayerMP;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import uk.kihira.tails.common.network.BaseC2SPlayerDataMessage;
import uk.kihira.tails.common.part.PartsData;
import uk.kihira.tails.common.part.ServerPlayerPartManager;

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

			final EntityPlayerMP sender = context.getServerHandler().playerEntity;
			final UUID uuid = sender.getUniqueID();

			ServerPlayerPartManager.get().set(uuid, message.partsData);

			// Tell other clients about the change.
			// TODO: This sends the user's part data to themself, which is an unnecessary packet (they already have this data).
			TailsNetworkManager.get().sendToAll(new S2CPlayerDataMessage(uuid, message.partsData));

			return null;
		}
	}
}