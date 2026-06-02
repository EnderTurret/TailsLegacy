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

import net.enderturret.tailslegacy.common.network.BaseS2CPlayerDataMessage;
import net.enderturret.tailslegacy.common.part.PartsData;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

@Internal
public final class S2CPlayerDataMessage implements BaseS2CPlayerDataMessage, IMessage {

	private UUID uuid;
	private PartsData partsData;

	public S2CPlayerDataMessage() {}

	public S2CPlayerDataMessage(UUID uuid, PartsData partsData) {
		this.uuid = uuid;
		this.partsData = partsData;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		uuid = new UUID(buf.readLong(), buf.readLong());
		final String tailInfoJson = ByteBufUtils.readUTF8String(buf);

		partsData = BaseS2CPlayerDataMessage.decodeJson(uuid, tailInfoJson);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(uuid.getMostSignificantBits());
		buf.writeLong(uuid.getLeastSignificantBits());
		ByteBufUtils.writeUTF8String(buf, BaseS2CPlayerDataMessage.encodeJson(partsData));
	}

	@Internal
	public static final class Handler implements IMessageHandler<S2CPlayerDataMessage, IMessage> {

		@Override
		public IMessage onMessage(S2CPlayerDataMessage message, MessageContext context) {
			BaseS2CPlayerDataMessage.handle(message.uuid, message.partsData);
			return null;
		}
	}
}