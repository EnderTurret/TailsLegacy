/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.part;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

import net.minecraft.resources.ResourceLocation;

import net.neoforged.neoforge.registries.DeferredHolder;

import uk.kihira.tails.client.PartRenderRegistry;
import uk.kihira.tails.client.api.RegisterPartRenderersEvent;
import uk.kihira.tails.common.Tails;

/**
 * Contains all of the parts read from the {@link PartLoadingManager}.
 * @see RegisterPartRenderersEvent
 * @see #get(ResourceLocation)
 * @author EnderTurret
 */
public final class PartRegistry {

	private static final Map<ResourceLocation, Part> PART_REGISTRY = new TreeMap<>();
	private static final ListMultimap<AttachmentPoint, Part> BY_TYPE = MultimapBuilder.hashKeys().arrayListValues().build();

	public static final PartLoadingManager MANAGER = new PartLoadingManager(() -> {
		PART_REGISTRY.clear();
		BY_TYPE.clear();
		AttachmentPoints.clear();
	}, (parts, ordering) -> {
		Tails.LOGGER.debug("Registering {} parts.", parts.size());

		for (Part part : parts)
			PART_REGISTRY.put(part.getId(), part);

		for (Map.Entry<AttachmentPoint, List<ResourceLocation>> entry : ordering.entrySet()) {
			final List<Part> ordered = entry.getValue().stream()
					.map(PART_REGISTRY::get)
					.filter(p -> p != null && p.getAttachment() == entry.getKey())
					.collect(Collectors.toList());

			for (Part part : parts)
				if (part.getAttachment() == entry.getKey() && !ordered.contains(part))
					ordered.add(part);

			BY_TYPE.putAll(entry.getKey(), List.copyOf(ordered));
		}

		for (Part part : parts)
			if (!BY_TYPE.containsEntry(part.getAttachment(), part))
				BY_TYPE.put(part.getAttachment(), part);

		PartRenderRegistry.reload();

		LocalPartManager.reload();
		Tails.PROXY.getLibraryManager().reload(false);
	});

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
	public static PartReference reference(ResourceLocation id) {
		return new PartReference(id);
	}

	/**
	 * Equivalent to {@link #reference(ResourceLocation)} with "tails" as the namespace.
	 * @param id The id of the part.
	 * @return The new reference.
	 */
	public static PartReference reference(String id) {
		return reference(ResourceLocation.fromNamespaceAndPath(Tails.MOD_ID, id));
	}

	/**
	 * Retrieves the part with the given id from the registry.
	 * @param id The id of the part to retrieve.
	 * @return The retrieved part, or {@code null} if no such part exists.
	 */
	@Nullable
	public static Part get(ResourceLocation id) {
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
	 * Like a {@link DeferredHolder} but for parts.
	 * @author EnderTurret
	 * @see PartRegistry#reference(ResourceLocation)
	 */
	public static final class PartReference implements Supplier<Part> {

		private final ResourceLocation id;

		PartReference(ResourceLocation id) {
			this.id = id;
		}

		/**
		 * @return The id of the referenced part.
		 */
		public ResourceLocation id() {
			return id;
		}

		@Override
		public Part get() {
			return PartRegistry.get(id);
		}
	}
}