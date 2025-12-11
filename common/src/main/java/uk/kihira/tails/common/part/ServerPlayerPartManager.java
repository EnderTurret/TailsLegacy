package uk.kihira.tails.common.part;

import uk.kihira.tails.common.api.IPlayerPartManager;

public final class ServerPlayerPartManager {

	private static IPlayerPartManager instance;

	public static IPlayerPartManager get() {
		if (instance == null) instance = new PlayerPartManager();
		return instance;
	}
}