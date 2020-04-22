package no.hiof.tellu.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class SensorDataDTO {

	private String timestamp;
	
	@JsonProperty("SensorMeasurement")
	List<SensorMeasurementDTO> SensorMeasurement = new ArrayList<SensorMeasurementDTO>();
	
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	
	
	
	public List<SensorMeasurementDTO> getSensorMeasurement() {
		return SensorMeasurement;
	}
	
	@JsonSetter("SensorMeasurement")
	public void setSensorMeasurement(List<SensorMeasurementDTO> sensorMeasurement) {
		SensorMeasurement = sensorMeasurement;
	}
	public SensorDataDTO() {
	
	}
	
	public SensorDataDTO(String timestamp, List<SensorMeasurementDTO> sensorMeasurment) {
	
		this.timestamp = timestamp;
		this.SensorMeasurement = sensorMeasurment;
	}
	
	@Override
	public String toString() {
		return "SensorDataDTO [timestamp=" + timestamp + ", SensorMeasurement=" + SensorMeasurement + "]";
	}
	

	
	
	
	
	
	
}
