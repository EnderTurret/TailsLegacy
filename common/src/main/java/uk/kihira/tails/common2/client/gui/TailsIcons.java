package uk.kihira.tails.common2.client.gui;

public enum TailsIcons {

	UNDO(0, 0),
	QUESTION(16, 0),
	EYEDROPPER(32, 0),
	SAVE(48, 0),
	DELETE(64, 0),
	COPY(80, 0),
	STAR(96, 0),
	EDIT(112, 0),
	UPLOAD(128, 0),
	DOWNLOAD(144, 0),
	SEARCH(160, 0),
	SERVER(176, 0),
	IMPORT(192, 0),
	EXPORT(208, 0);

	public final int u;
	public final int v;

	private TailsIcons(int u, int v) {
		this.u = u;
		this.v = v;
	}
}