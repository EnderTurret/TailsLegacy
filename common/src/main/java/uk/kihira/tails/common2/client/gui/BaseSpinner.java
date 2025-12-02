package uk.kihira.tails.common2.client.gui;

import java.util.NavigableSet;

public interface BaseSpinner<T> {

	public void setValues(NavigableSet<T> values);
	public NavigableSet<T> values();
	public T getSelection();
	public T select(T value);

	public default T next() {
		T next = values().higher(getSelection());
		return select(next == null ? values().first() : next);
	}

	public default T previous() {
		T previous = values().lower(getSelection());
		return select(previous == null ? values().last() : previous);
	}

	/**
	 * Turns a given element into a {@link String}.
	 * @author EnderTurret
	 * @param <T> The type of element.
	 */
	@FunctionalInterface
	public static interface Stringifier<T> {
		public String stringify(T value);
	}

	/**
	 * A callback executed when an element is selected.
	 * @author EnderTurret
	 * @param <T> The type of element.
	 */
	@FunctionalInterface
	public static interface Listener<T> {
		public void onSelected(T selection);
	}
}