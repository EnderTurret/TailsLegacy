package net.enderturret.tailslegacy.common.part;

import net.enderturret.tailslegacy.common.api.IPlayerPartManager;

public final class ServerPlayerPartManager {

	private static IPlayerPartManager instance;

	public static IPlayerPartManager get() {
		if (instance == null) instance = new PlayerPartManager();
		return instance;
	}
}