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

import net.enderturret.tailslegacy.common.network.BasePlayerDataMapMessage;
import net.enderturret.tailslegacy.common.part.PartsData;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

// S → C
@Internal
public final class PlayerDataMapMessage implements BasePlayerDataMapMessage, IMessage {

	private Map<UUID, PartsData> partsDataMap;

	public PlayerDataMapMessage() {}

	public PlayerDataMapMessage(Map<UUID, PartsData> partsDataMap) {
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
	public static final class Handler implements IMessageHandler<PlayerDataMapMessage, IMessage> {

		@Override
		public IMessage onMessage(PlayerDataMapMessage message, MessageContext context) {
			BasePlayerDataMapMessage.handle(message.partsDataMap);
			return null;
		}
	}
}