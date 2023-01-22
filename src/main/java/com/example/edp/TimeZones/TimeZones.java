package com.example.edp.TimeZones;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "dstOffset",
        "rawOffset",
        "status",
        "timeZoneId",
        "timeZoneName"
})
@Generated("jsonschema2pojo")
public class TimeZones {

    @JsonProperty("dstOffset")
    private Integer dstOffset;
    @JsonProperty("rawOffset")
    private Integer rawOffset;
    @JsonProperty("status")
    private String status;
    @JsonProperty("timeZoneId")
    private String timeZoneId;
    @JsonProperty("timeZoneName")
    private String timeZoneName;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("dstOffset")
    public Integer getDstOffset() {
        return dstOffset;
    }

    @JsonProperty("dstOffset")
    public void setDstOffset(Integer dstOffset) {
        this.dstOffset = dstOffset;
    }

    @JsonProperty("rawOffset")
    public Integer getRawOffset() {
        return rawOffset;
    }

    @JsonProperty("rawOffset")
    public void setRawOffset(Integer rawOffset) {
        this.rawOffset = rawOffset;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("timeZoneId")
    public String getTimeZoneId() {
        return timeZoneId;
    }

    @JsonProperty("timeZoneId")
    public void setTimeZoneId(String timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    @JsonProperty("timeZoneName")
    public String getTimeZoneName() {
        return timeZoneName;
    }

    @JsonProperty("timeZoneName")
    public void setTimeZoneName(String timeZoneName) {
        this.timeZoneName = timeZoneName;
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