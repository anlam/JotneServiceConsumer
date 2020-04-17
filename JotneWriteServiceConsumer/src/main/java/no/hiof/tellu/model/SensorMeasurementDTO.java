package no.hiof.tellu.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class SensorMeasurementDTO {

	@JsonProperty("Measurement")
	private String Measurement;
	
	private String value;
	
	public String getMeasurement() {
		return Measurement;
	}
	
	@JsonSetter("Measurement")
	public void setMeasurement(String measurement) {
		this.Measurement = measurement;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
	
	public SensorMeasurementDTO() {
	}
	
	public SensorMeasurementDTO(String measurement, String value) {
		this.Measurement = measurement;
		this.value = value;
	}

	@Override
	public String toString() {
		return "SensorMeasurementDTO [Measurement=" + Measurement + ", value=" + value + "]";
	}


	
	

	

}
