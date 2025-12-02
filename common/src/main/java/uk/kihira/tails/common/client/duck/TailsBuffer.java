package uk.kihira.tails.common.client.duck;

public interface TailsBuffer {

	public TailsBuffer t$beginVertex(TailsPoseStack pose, float x, float y, float z);
	public TailsBuffer t$color(int color);
	public TailsBuffer t$uv(float u, float v);
	public TailsBuffer t$overlay(int overlay);
	public TailsBuffer t$light(int light);
	public TailsBuffer t$normal(TailsPoseStack pose, float x, float y, float z);
	public TailsBuffer t$endVertex();
}