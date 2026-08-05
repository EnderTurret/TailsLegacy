/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.common.network;

import java.util.Map;
import java.util.UUID;

import org.jetbrains.annotations.ApiStatus.Internal;

import io.netty.buffer.ByteBuf;

import net.enderturret.tailslegacy.common.network.BaseS2CBulkPlayerDataMessage;
import net.enderturret.tailslegacy.common.part.PartsData;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

@Internal
public final class S2CBulkPlayerDataMessage implements BaseS2CBulkPlayerDataMessage, IMessage {

	private Map<UUID, PartsData> partsDataMap;

	public S2CBulkPlayerDataMessage() {}

	public S2CBulkPlayerDataMessage(Map<UUID, PartsData> partsDataMap) {
		this.partsDataMap = partsDataMap;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		partsDataMap = BaseS2CBulkPlayerDataMessage.decodeJson(ByteBufUtils.readUTF8String(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, BaseS2CBulkPlayerDataMessage.encodeJson(partsDataMap));
	}

	@Internal
	public static final class Handler implements IMessageHandler<S2CBulkPlayerDataMessage, IMessage> {

		@Override
		public IMessage onMessage(S2CBulkPlayerDataMessage message, MessageContext context) {
			BaseS2CBulkPlayerDataMessage.handle(message.partsDataMap);
			return null;
		}
	}
}