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

import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import net.enderturret.tailslegacy.common.network.BasePlayerDataMapMessage;
import net.enderturret.tailslegacy.common.part.PartsData;

@Internal
public final class S2CBulkPlayerDataMessage implements BasePlayerDataMapMessage, IMessage {

	private Map<UUID, PartsData> partsDataMap;

	public S2CBulkPlayerDataMessage() {}

	public S2CBulkPlayerDataMessage(Map<UUID, PartsData> partsDataMap) {
		this.partsDataMap = partsDataMap;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		partsDataMap = BasePlayerDataMapMessage.decodeJson(ByteBufUtils.readUTF8String(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, BasePlayerDataMapMessage.encodeJson(partsDataMap));
	}

	@Internal
	public static final class Handler implements IMessageHandler<S2CBulkPlayerDataMessage, IMessage> {

		@Override
		public IMessage onMessage(S2CBulkPlayerDataMessage message, MessageContext context) {
			BasePlayerDataMapMessage.handle(message.partsDataMap);
			return null;
		}
	}
}