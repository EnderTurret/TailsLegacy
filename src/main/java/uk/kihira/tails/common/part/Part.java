package uk.kihira.tails.common.part;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;
import uk.kihira.tails.client.gui.panel.PartsPanel;
import uk.kihira.tails.client.render.PartRenderer;
import uk.kihira.tails.common.Tails;

public class Part {

	protected final PartType type;
	protected final ResourceLocation id;
	protected final String[] textureNames;
	protected final int maxSubType;
	protected final String[][] authors;

	public Part(Builder builder) {
		this.type = builder.type;
		this.id = builder.id;
		this.maxSubType = builder.maxSubType;
		this.textureNames = builder.textureNames.toArray(new String[0]);
		authors = builder.authors;
	}

	public ResourceLocation getId() {
		return id;
	}

	public PartType getType() {
		return type;
	}

	/**
	 * Returns the textures available for the given subtype.<br>
	 * This can be overriden for more control over texture names for a particular subtype.
	 * @param subid The part subtype.
	 * @return The available textures.
	 */
	public String[] getTextureNames(int subid) {
		return textureNames;
	}

	/**
	 * Returns the maximum subtype id.
	 * @return The subtype id.
	 */
	public int getAvailableSubTypes() {
		return maxSubType;
	}

	/**
	 * @return The translation key.
	 */
	public String getTranslationKey() {
		return id.getNamespace() + ".part." + id.getPath();
	}

	public String getAuthor(int subType, int textureID) {
		return authors[subType][textureID];
	}

	public boolean hasAuthor(int subType, int textureID) {
		return getAuthor(subType, textureID) != null;
	}

	/**
	 * Returns a default {@link PartInfo} for the {@link PartsPanel} to display.
	 * @param subType The sub type of this part.
	 * @return The default {@link PartInfo}.
	 */
	public PartInfo makeDefaultPartInfo(int subType) {
		return new PartInfo(id, subType, 0, 0xFFFF0000, 0xFF00FF00, 0xFF0000FF, null);
	}

	public static class Builder {

		private final PartType type;
		private final ResourceLocation id;
		private List<String> textureNames = new ArrayList<>(1);
		private int maxSubType;
		private String[][] authors = new String[1][0];

		protected Builder(PartType type, ResourceLocation id) {
			this.type = type;
			this.id = id;
			texture(id.getPath());
		}

		public static Builder builder(PartType type, ResourceLocation id) {
			return new Builder(type, id);
		}

		static Builder tail(String id) {
			return builder(PartType.TAIL, new ResourceLocation(Tails.MOD_ID, id));
		}

		static Builder ears(String id) {
			return builder(PartType.EARS, new ResourceLocation(Tails.MOD_ID, id));
		}

		static Builder wings(String id) {
			return builder(PartType.WINGS, new ResourceLocation(Tails.MOD_ID, id));
		}

		static Builder muzzle(String id) {
			return builder(PartType.MUZZLE, new ResourceLocation(Tails.MOD_ID, id));
		}

		public Builder subType() {
			maxSubType++;

			authors = new String[maxSubType + 1][textureNames.size()];

			return this;
		}

		public Builder texture(String... names) {
			Collections.addAll(textureNames, names);

			authors = new String[maxSubType + 1][textureNames.size()];

			return this;
		}

		public Builder author(String author, int subType, int textureID) {
			authors[subType][textureID] = author;

			return this;
		}

		public Builder author(String author, int subType) {
			for (int i = 0; i < textureNames.size(); i++)
				author(author, subType, i);

			return this;
		}

		public Builder author(String author) {
			for (int i = 0; i <= maxSubType; i++)
				author(author, i);

			return this;
		}

		public Part build() {
			return build(Part::new);
		}

		public <T extends Part> T build(Function<Part.Builder,T> ctor) {
			return ctor.apply(this);
		}

		public Part register() {
			return register(Part::new);
		}

		public <T extends Part> T register(Function<Part.Builder,T> ctor) {
			final T t = ctor.apply(this);
			PartRegistry.register(t);
			return t;
		}
	}
}