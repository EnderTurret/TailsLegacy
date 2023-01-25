package uk.kihira.tails.client.part;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import uk.kihira.tails.common.part.IPartInfo;
import uk.kihira.tails.common.part.PartsData;

public final class ClientPartsData extends PartsData {

	public static final ClientPartsData EMPTY = new ClientPartsData();

	public ClientPartsData() {
		super();
	}

	public ClientPartsData(Set<IPartInfo> parts) {
		super(parts);
	}

	public ClientPartInfo getPartInfo(AttachmentPoint attachment) {
		for (ClientPartInfo info : getParts())
			if (info.getPart().getAttachment().equals(attachment))
				return info;

		return empty();
	}

	public void setPartInfo(AttachmentPoint attachment, ClientPartInfo info) {
		Objects.requireNonNull(attachment);
		Objects.requireNonNull(info);

		for (Iterator<IPartInfo> it = parts.iterator(); it.hasNext(); )
			if (((ClientPartInfo) it.next()).getPart().getAttachment().equals(attachment))
				it.remove();

		addPartInfo(info);
	}

	@Override
	protected ClientPartInfo empty() {
		return ClientPartInfo.empty();
	}

	@Override
	public void addPartInfo(IPartInfo partInfo) {
		super.addPartInfo(ClientPartInfo.coerce(partInfo));
	}

	@SuppressWarnings({ "cast", "unchecked" })
	public Set<ClientPartInfo> getParts() {
		return (Set<ClientPartInfo>) (Set) super.getPartInfos();
	}

	@Override
	public ClientPartsData deepCopy() {
		return ClientPartsData.clone(super.deepCopy());
	}

	public static ClientPartsData clone(PartsData data) {
		return new ClientPartsData(data.getPartInfos());
	}

	@Internal
	public static class Serializer extends PartsData.Serializer {

		@Override
		public PartsData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			return ClientPartsData.clone(super.deserialize(json, typeOfT, context));
		}
	}
}