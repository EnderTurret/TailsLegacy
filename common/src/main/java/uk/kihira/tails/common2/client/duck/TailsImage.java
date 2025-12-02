package uk.kihira.tails.common2.client.duck;

public interface TailsImage {

	public int getWidth();
	public int getHeight();

	public int getRGBA(int x, int y);
	public void putRGBA(int x, int y, int pixel);
}