package uk.kihira.tails.client.part;

import java.lang.reflect.Type;
import java.util.Map;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import uk.kihira.tails.common.part.IPartInfo;
import uk.kihira.tails.common.part.PartType;
import uk.kihira.tails.common.part.PartsData;

public final class ClientPartsData extends PartsData {

	public static final ClientPartsData EMPTY = new ClientPartsData();

	public ClientPartsData() {
		super();
	}

	public ClientPartsData(Map<String, IPartInfo> map) {
		super(map);
	}

	public ClientPartInfo getPartInfo(PartType partType) {
		return getPartInfo(partType.getId());
	}

	public void setPartInfo(PartType partType, IPartInfo partInfo) {
		setPartInfo(partType.getId(), partInfo);
	}

	public boolean hasPartInfo(PartType partType) {
		return hasPartInfo(partType.getId());
	}

	@Override
	protected IPartInfo empty() {
		return ClientPartInfo.empty();
	}

	@Override
	public ClientPartInfo getPartInfo(String partType) {
		return ClientPartInfo.coerce(super.getPartInfo(partType));
	}

	@Override
	public ClientPartsData deepCopy() {
		return ClientPartsData.clone(super.deepCopy());
	}

	public static ClientPartsData clone(PartsData data) {
		return new ClientPartsData(data.getPartInfoMap());
	}

	@Internal
	public static class Serializer extends PartsData.Serializer {

		@Override
		public PartsData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			return ClientPartsData.clone(super.deserialize(json, typeOfT, context));
		}
	}
}