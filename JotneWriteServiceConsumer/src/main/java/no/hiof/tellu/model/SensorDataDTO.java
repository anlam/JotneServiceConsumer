package no.hiof.tellu.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class SensorDataDTO {

	private String timestamp;
	
	@JsonProperty("SensorMeasurment")
	List<SensorMeasurmentDTO> SensorMeasurment = new ArrayList<SensorMeasurmentDTO>();
	
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public List<SensorMeasurmentDTO> getSensorMeasurment() {
		return SensorMeasurment;
	}
	
	@JsonSetter("SensorMeasurment")
	public void setSensorMeasurment(List<SensorMeasurmentDTO> sensorMeasurment) {
		this.SensorMeasurment = sensorMeasurment;
	}
	
	
	
	public SensorDataDTO() {
	
	}
	
	public SensorDataDTO(String timestamp, List<SensorMeasurmentDTO> sensorMeasurment) {
	
		this.timestamp = timestamp;
		this.SensorMeasurment = sensorMeasurment;
	}
	
	@Override
	public String toString() {
		return "SensorDataDTO [timestamp=" + timestamp + ", SensorMeasurment=" + SensorMeasurment + "]";
	}
	

	
	
	
	
	
	
}
