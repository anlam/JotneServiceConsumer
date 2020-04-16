package no.hiof.tellu.model;

public class SensorMeasurmentDTO {

	private String measurement;
	private String value;
	
	public String getMeasurement() {
		return measurement;
	}
	public void setMeasurement(String measurement) {
		this.measurement = measurement;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
	
	public SensorMeasurmentDTO() {
	}
	
	public SensorMeasurmentDTO(String measurement, String value) {
		this.measurement = measurement;
		this.value = value;
	}
	
	
	@Override
	public String toString() {
		return "SensorMeasurment [measurement=" + measurement + ", value=" + value + "]";
	}

}
