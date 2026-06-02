package test.platform;

import java.util.Objects;

import net.enderturret.tailslegacy.common.client.duck.TResourceLocation;

public final class SimpleResourceLocation implements TResourceLocation {

	private final String namespace;
	private final String path;

	public SimpleResourceLocation(String namespace, String path) {
		this.namespace = namespace;
		this.path = path;
	}

	public static SimpleResourceLocation parse(String input) {
		int idx = input.indexOf(':');
		if (idx == -1) return new SimpleResourceLocation("minecraft", input);
		return new SimpleResourceLocation(input.substring(0, idx), input.substring(idx + 1));
	}

	@Override
	public String t$getNamespace() {
		return namespace;
	}

	@Override
	public String t$getPath() {
		return path;
	}

	@Override
	public TResourceLocation t$withPath(String path) {
		return new SimpleResourceLocation(namespace, path);
	}

	@Override
	public int t$compareTo(TResourceLocation other) {
		int tmp = t$getPath().compareTo(other.t$getPath());
		if (tmp != 0) return tmp;
		return t$getNamespace().compareTo(other.t$getNamespace());
	}

	@Override
	public int t$compareNamespaced(TResourceLocation other) {
		int tmp = t$getNamespace().compareTo(other.t$getNamespace());
		if (tmp != 0) return tmp;
		return t$getPath().compareTo(other.t$getPath());
	}

	@Override
	public String toString() {
		return namespace + ':' + path;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof SimpleResourceLocation)) return false;
		final SimpleResourceLocation rl = (SimpleResourceLocation) obj;
		return namespace.equals(rl.namespace) && path.equals(rl.path);
	}

	@Override
	public int hashCode() {
		return Objects.hash(namespace, path);
	}
}