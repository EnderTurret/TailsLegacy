package uk.kihira.tails.common2.client.duck;

public interface TResourceLocation {

	public String t$getNamespace();
	public String t$getPath();

	public TResourceLocation t$withPath(String path);

	public int t$compareTo(TResourceLocation other);
	public int t$compareNamespaced(TResourceLocation other);
}