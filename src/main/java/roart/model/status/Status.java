package roart.model.status;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"all_stations_closed",
"stations_closed"
})
public class Status {

@JsonProperty("all_stations_closed")
private Boolean allStationsClosed;
@JsonProperty("stations_closed")
private List<Integer> stationsClosed = null;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("all_stations_closed")
public Boolean getAllStationsClosed() {
return allStationsClosed;
}

@JsonProperty("all_stations_closed")
public void setAllStationsClosed(Boolean allStationsClosed) {
this.allStationsClosed = allStationsClosed;
}

@JsonProperty("stations_closed")
public List<Integer> getStationsClosed() {
return stationsClosed;
}

@JsonProperty("stations_closed")
public void setStationsClosed(List<Integer> stationsClosed) {
this.stationsClosed = stationsClosed;
}

@JsonAnyGetter
public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

@JsonAnySetter
public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}

}