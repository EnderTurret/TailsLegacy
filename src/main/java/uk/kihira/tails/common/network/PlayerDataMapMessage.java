/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.network;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonSyntaxException;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.network.NetworkEvent;
import uk.kihira.tails.common.PartsData;
import uk.kihira.tails.common.Tails;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class PlayerDataMapMessage {

    private Map<UUID, PartsData> partsDataMap;

    public PlayerDataMapMessage() {}
    @SuppressWarnings("unchecked")
    public PlayerDataMapMessage(Map partsDataMap) {
        this.partsDataMap = partsDataMap;
    }

    @SuppressWarnings("unchecked")
    public static PlayerDataMapMessage fromBytes(PacketBuffer buf) {
        String tailInfoJson = buf.readString(Short.MAX_VALUE);
        PlayerDataMapMessage msg = new PlayerDataMapMessage();
        try {
            msg.partsDataMap = Tails.gson.fromJson(tailInfoJson, new TypeToken<Map<UUID, PartsData>>() {}.getType());
        } catch (JsonSyntaxException e) {
            Tails.logger.catching(e);
        }
        return msg;
    }

    public static void toBytes(PlayerDataMapMessage msg, PacketBuffer buf) {
        String tailInfoJson = Tails.gson.toJson(msg.partsDataMap);
        buf.writeString(tailInfoJson, Short.MAX_VALUE);
    }

        public static void onMessage(PlayerDataMapMessage message, Supplier<NetworkEvent.Context> ctx) {
            for (Map.Entry<UUID, PartsData> entry : message.partsDataMap.entrySet()) {
                Tails.proxy.addPartsData(entry.getKey(), entry.getValue());
            }
            ctx.get().setPacketHandled(true);
        }
}
