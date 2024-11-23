package uk.kihira.tails.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

import uk.kihira.tails.client.part.PartRegistry;

public final class TailsKeybinds {

	public static final KeyMapping RELOAD_PARTS = new KeyMapping("key.tails.reload_parts", -1, "key.category.tails");

	static void registerKeys(RegisterKeyMappingsEvent e) {
		e.register(RELOAD_PARTS);
	}

	static void onKeyPressed(InputEvent.Key e) {
		if (RELOAD_PARTS.consumeClick())
			PartRegistry.MANAGER.onResourceManagerReload(Minecraft.getInstance().getResourceManager());
	}
}