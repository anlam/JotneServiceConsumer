package no.hiof.tellu.model;

import java.util.ArrayList;
import java.util.List;

public class SensorDataDTO {

	private String timestamp;
	List<SensorMeasurmentDTO> sensorMeasurment = new ArrayList<SensorMeasurmentDTO>();
	
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public List<SensorMeasurmentDTO> getSensorMeasurment() {
		return sensorMeasurment;
	}
	public void setSensorMeasurment(List<SensorMeasurmentDTO> sensorMeasurment) {
		this.sensorMeasurment = sensorMeasurment;
	}
	
	
	
	public SensorDataDTO() {
	
	}
	
	public SensorDataDTO(String timestamp, List<SensorMeasurmentDTO> sensorMeasurment) {
	
		this.timestamp = timestamp;
		this.sensorMeasurment = sensorMeasurment;
	}
	
	
	@Override
	public String toString() {
		return "SensorData [timestamp=" + timestamp + ", sensorMeasurment=" + sensorMeasurment + "]";
	}
	
	
	
	
	
}
