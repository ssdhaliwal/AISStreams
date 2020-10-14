package elsu.ais.monitor;

import java.io.IOException;

import org.joda.time.Instant;
import org.joda.time.format.ISOPeriodFormat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import elsu.ais.messages.data.VesselDimensions;

public class TrackStatusDeserializer extends JsonDeserializer<TrackStatus> {

	@Override
	public TrackStatus deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		ObjectCodec oc = jp.getCodec();
		JsonNode node = oc.readTree(jp);
		
		TrackStatus status = new TrackStatus();
		status.setTransponderType(node.get("transponder").asText());
		status.setType(node.get("type").asInt());
		status.setRepeat(node.get("repeat").asInt());
		status.setMmsi(node.get("mmsi").asInt());
		status.setStatus(node.get("status").asInt());
		status.setRateOfTurn(node.get("rateOfTurn").asDouble());
		status.setSpeed((float) node.get("speed").asDouble());
		status.setAccuracy(node.get("accuracy").asBoolean());
		status.setLongitude((float) node.get("longitude").asDouble());
		status.setLatitude((float) node.get("latitude").asDouble());
		status.setCourse((float) node.get("course").asDouble());
		status.setHeading(node.get("heading").asInt());
		status.setSecond(node.get("second").asInt());
		status.setManeuver(node.get("maneuver").asInt());
		status.setRaim(node.get("raim").asBoolean());
		status.setRadio(node.get("radio").asInt());

		//JsonNode node2 = oc.readTree(node.get("commState").traverse());
		//CommunicationState commState = new CommunicationState();
		//status.setCommState(commState);

		status.setAisVersion(node.get("aisVersion").asInt());
		status.setImo(node.get("imo").asInt());
		status.setCallSign(node.get("callSign").asText());
		status.setShipName(node.get("shipName").asText());
		status.setShipType(node.get("shipType").asInt());

		JsonNode node3 = oc.readTree(node.get("dimension").traverse());
		try {
			VesselDimensions dimension = new VesselDimensions();
			dimension.setToBow(node.get("toBow").asInt(0));
			dimension.setToPort(node.get("toPort").asInt(0));
			dimension.setToStarboard(node.get("toStarboard").asInt(0));
			dimension.setToStern(node.get("toStern").asInt(0));
			status.setDimension(dimension);
		} catch (Exception exi) { }

		status.setEpfd(node.get("epfd").asInt());
		status.setMonth(node.get("month").asInt());
		status.setHour(node.get("hour").asInt());
		status.setDay(node.get("day").asInt());
		status.setMinute(node.get("minute").asInt());
		status.setDraught(node.get("draught").asInt());
		status.setDestination(node.get("destination").asText());
		status.setDte(node.get("dte").asInt());
		status.setAssignedMode(node.get("assignedMode").asInt());
		status.setRegional(node.get("regional").asInt());
		status.setCs(node.get("cs").asBoolean());
		status.setDisplay(node.get("display").asBoolean());
		status.setDsc(node.get("dsc").asBoolean());
		status.setBand(node.get("band").asBoolean());
		status.setMsg22(node.get("msg22").asBoolean());
		status.setAssigned(node.get("assigned").asBoolean());
		status.setCommFlag(node.get("commFlag").asInt());

		//node.set("positionHistory", SentenceBase.objectMapper.readTree(getPositionHistoryAsString())); // 48
		status.setCreateTime(Instant.parse(node.get("createTime").asText().replace(" UTC", "Z").replace(" ", "T")));
		//node.put("createTime", SentenceBase.formatEPOCHToUTC((int)(getCreateTime().getMillis() / 1000))); // 49
		//node.put("updateCounter", getUpdateCounter()); // 50
		//node.put("period", ISOPeriodFormat.standard().print(getPeriod())); // 51

		return status;
	}

}
