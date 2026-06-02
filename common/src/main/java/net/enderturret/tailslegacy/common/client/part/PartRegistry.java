/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.client.part;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.common.client.TailsClientPlatform;
import net.enderturret.tailslegacy.common.client.duck.TResourceLocation;
import net.enderturret.tailslegacy.common.client.render.PartRenderRegistry;

/**
 * Contains all of the parts read from the {@link PartLoadingManager}.
 * @see #get(TResourceLocation)
 * @author EnderTurret
 */
public final class PartRegistry {

	private static final Map<TResourceLocation, Part> PART_REGISTRY = new TreeMap<>(TResourceLocation::t$compareNamespaced);
	private static final Map<AttachmentPoint, List<Part>> BY_TYPE = new LinkedHashMap<>();

	public static final PartLoadingManager MANAGER = new PartLoadingManager(PartRegistry::clear, PartRegistry::register);

	private static void clear() {
		PART_REGISTRY.clear();
		BY_TYPE.clear();
		AttachmentPoints.clear();
		ClientPlayerPartManager.releaseAnimatorStorages();
	}

	private static void register(List<Part> parts, Map<AttachmentPoint, List<TResourceLocation>> ordering) {
		TailsPlatform.get().logDebug("Registering {} parts.", parts.size());

		for (Part part : parts)
			PART_REGISTRY.put(part.getId(), part);

		for (Map.Entry<AttachmentPoint, List<TResourceLocation>> entry : ordering.entrySet()) {
			final List<Part> ordered = entry.getValue().stream()
					.map(PART_REGISTRY::get)
					.filter(p -> p != null && p.getAttachment() == entry.getKey())
					.collect(Collectors.toList());

			for (Part part : parts)
				if (part.getAttachment() == entry.getKey() && !ordered.contains(part))
					ordered.add(part);

			BY_TYPE.put(entry.getKey(), Collections.unmodifiableList(new ArrayList<>(ordered)));
		}

		for (Part part : parts)
			if (!BY_TYPE.getOrDefault(part.getAttachment(), Collections.emptyList()).contains(part))
				BY_TYPE.computeIfAbsent(part.getAttachment(), k -> new ArrayList<>()).add(part);

		PartRenderRegistry.reload();

		LocalPartManager.reload();
		TailsClientPlatform.get().getLibraryManager().reload(false);
	}

	public static final PartReference FLUFFY_TAIL = reference("tail/fluffy_tail");
	public static final PartReference DRAGON_TAIL = reference("tail/dragon_tail");
	public static final PartReference RACCOON_TAIL = reference("tail/raccoon_tail");
	public static final PartReference DEVIL_TAIL = reference("tail/devil_tail");
	public static final PartReference CAT_TAIL = reference("tail/cat_tail");
	public static final PartReference BIRD_TAIL = reference("tail/bird_tail");
	public static final PartReference SHARK_TAIL = reference("tail/shark_tail");
	public static final PartReference BUNNY_TAIL = reference("tail/bunny_tail");
	public static final PartReference BEE_ABDOMEN = reference("tail/bee_abdomen");
	public static final PartReference SCORPION_TAIL = reference("tail/scorpion_tail");
	public static final PartReference THICK_TAIL = reference("tail/thick_tail");

	public static final PartReference FOX_EARS = reference("ears/fox_ears");
	public static final PartReference BLAZE_CROWN = reference("ears/blaze_crown");
	public static final PartReference ELF_EARS = reference("ears/elf_ears");
	public static final PartReference ANTLERS = reference("ears/antlers");

	public static final PartReference BIG_WINGS = reference("wings/big_wings");

	public static final PartReference STANDARD_MUZZLE = reference("muzzle/standard_muzzle");
	public static final PartReference SLIM_MUZZLE = reference("muzzle/slim_muzzle");
	public static final PartReference THIN_MUZZLE = reference("muzzle/thin_muzzle");

	/**
	 * Equivalent to {@code new PartReference(id)}.
	 * @param id The id of the part.
	 * @return The new reference.
	 */
	public static PartReference reference(TResourceLocation id) {
		return new PartReference(id);
	}

	/**
	 * Equivalent to {@link #reference(TResourceLocation)} with "tailslegacy" as the namespace.
	 * @param id The id of the part.
	 * @return The new reference.
	 */
	public static PartReference reference(String id) {
		return reference(TailsPlatform.get().newResourceLocation(id));
	}

	/**
	 * Retrieves the part with the given id from the registry.
	 * @param id The id of the part to retrieve.
	 * @return The retrieved part, or {@code null} if no such part exists.
	 */
	@Nullable
	public static Part get(TResourceLocation id) {
		return PART_REGISTRY.get(id);
	}

	/**
	 * Returns a list of {@link Part Parts} under the given attachment point.
	 * @param attachment The desired attachment point of the parts.
	 * @return The list.
	 */
	public static List<Part> getParts(AttachmentPoint attachment) {
		return BY_TYPE.get(attachment);
	}

	/**
	 * Like a {@code DeferredHolder} but for parts.
	 * @author EnderTurret
	 * @see PartRegistry#reference(TResourceLocation)
	 */
	public static final class PartReference implements Supplier<Part> {

		private final TResourceLocation id;

		PartReference(TResourceLocation id) {
			this.id = id;
		}

		/**
		 * @return The id of the referenced part.
		 */
		public TResourceLocation id() {
			return id;
		}

		@Override
		public Part get() {
			return PartRegistry.get(id);
		}
	}
}