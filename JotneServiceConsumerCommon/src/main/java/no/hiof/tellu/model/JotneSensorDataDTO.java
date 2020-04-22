package no.hiof.tellu.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JotneSensorDataDTO {
	
	@JsonProperty("SensorType")
	private String SensorType;
	
	private String id;
	
	@JsonProperty("SensorData")
	private List<SensorDataDTO> SensorData = new ArrayList<SensorDataDTO>();
	
	
	
	public JotneSensorDataDTO() {
	}
	
	
	public JotneSensorDataDTO(String sensorType, String id, List<SensorDataDTO> sensorData) {
		this.SensorType = sensorType;
		this.id = id;
		this.SensorData = sensorData;
	}
	
	
	
	public String getSensorType() {
		return SensorType;
	}


	@JsonSetter("SensorType")
	public void setSensorType(String sensorType) {
		SensorType = sensorType;
	}


	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<SensorDataDTO> getSensorData() {
		return SensorData;
	}
	
	@JsonSetter("SensorData")
	public void setSensorData(List<SensorDataDTO> sensorData) {
		this.SensorData = sensorData;
	}
	
	
	
	@Override
	public String toString() {
		return "JotneSensorDataDTO [SensorType=" + SensorType + ", id=" + id + ", SensorData=" + SensorData + "]";
	}


	public static void main(final String[] args) {
		String testmsg = "{\r\n" + 
				"	\"id\": \"13483027\",\r\n" + 
				"	\"SensorType\": \"RUUVi\",\r\n" + 
				"	\"SensorData\": [\r\n" + 
				"		{\r\n" + 
				"			\"timestamp\": \"1586944294\",\r\n" + 
				"			\"SensorMeasurement\": [\r\n" + 
				"				{\r\n" + 
				"					\"Measurement\": \"ax\",\r\n" + 
				"					\"value\": \"390\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"Measurement\": \"ay\",\r\n" + 
				"					\"value\": \"-935\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"Measurement\": \"az\",\r\n" + 
				"					\"value\": \"-29\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"Measurement\": \"battery\",\r\n" + 
				"					\"value\": \"2977\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"Measurement\": \"humidity\",\r\n" + 
				"					\"value\": \"48\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"Measurement\": \"pressure\",\r\n" + 
				"					\"value\": \"99555\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"Measurement\": \"rssi\",\r\n" + 
				"					\"value\": \"-93\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"Measurement\": \"temperature\",\r\n" + 
				"					\"value\": \"1420\"\r\n" + 
				"				}\r\n" + 
				"			]\r\n" + 
				"		},\r\n" + 
				"		{\r\n" + 
				"			\"timestamp\": \"1586944300\",\r\n" + 
				"			\"SensorMeasurement\": [\r\n" + 
				"				{\r\n" + 
				"					\"Measurement\": \"ax\",\r\n" + 
				"					\"value\": \"176\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"Measurement\": \"ay\",\r\n" + 
				"					\"value\": \"-100\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"Measurement\": \"az\",\r\n" + 
				"					\"value\": \"-989\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"Measurement\": \"battery\",\r\n" + 
				"					\"value\": \"3223\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"Measurement\": \"humidity\",\r\n" + 
				"					\"value\": \"30\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"Measurement\": \"pressure\",\r\n" + 
				"					\"value\": \"99581\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"Measurement\": \"rssi\",\r\n" + 
				"					\"value\": \"-77\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"Measurement\": \"temperature\",\r\n" + 
				"					\"value\": \"2277\"\r\n" + 
				"				}\r\n" + 
				"			]\r\n" + 
				"		},\r\n" + 
				"		{\r\n" + 
				"			\"timestamp\": \"1586944400\",\r\n" + 
				"			\"SensorMeasurement\": [\r\n" + 
				"				{\r\n" + 
				"					\"Measurement\": \"ax\",\r\n" + 
				"					\"value\": \"717\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"Measurement\": \"ay\",\r\n" + 
				"					\"value\": \"-219\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"Measurement\": \"az\",\r\n" + 
				"					\"value\": \"-4\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"Measurement\": \"battery\",\r\n" + 
				"					\"value\": \"3079\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"Measurement\": \"humidity\",\r\n" + 
				"					\"value\": \"40\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"Measurement\": \"pressure\",\r\n" + 
				"					\"value\": \"100800\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"Measurement\": \"rssi\",\r\n" + 
				"					\"value\": \"-69\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"Measurement\": \"temperature\",\r\n" + 
				"					\"value\": \"1464\"\r\n" + 
				"				}\r\n" + 
				"			]\r\n" + 
				"		}\r\n" + 
				"	]\r\n" + 
				"}";
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		JotneSensorDataDTO dataDTO = gson.fromJson(testmsg, JotneSensorDataDTO.class);
		System.out.println(dataDTO);
		
		System.out.println(gson.toJson(dataDTO));
	}
	

}
