package no.hiof.tellu.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JotneSensorDataDTO {
	
	private String sensorType;
	private String id;
	private List<SensorDataDTO> sensorData = new ArrayList<SensorDataDTO>();
	
	
	
	public JotneSensorDataDTO() {
	}
	
	
	public JotneSensorDataDTO(String sensorType, String id, List<SensorDataDTO> sensorData) {
		this.sensorType = sensorType;
		this.id = id;
		this.sensorData = sensorData;
	}
	public String getSensorType() {
		return sensorType;
	}
	public void setSensorType(String sensorType) {
		this.sensorType = sensorType;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<SensorDataDTO> getSensorData() {
		return sensorData;
	}
	public void setSensorData(List<SensorDataDTO> sensorData) {
		this.sensorData = sensorData;
	}
	
	@Override
	public String toString() {
		return "JotneSensorDataDTO [sensorType=" + sensorType + ", id=" + id + ", sensorData=" + sensorData + "]";
	}
	
	public static void main(final String[] args) {
		String testmsg = "{\r\n" + 
				"	\"id\": \"13274698\",\r\n" + 
				"	\"sensorType\": \"RUUVi\",\r\n" + 
				"	\"sensorData\": [\r\n" + 
				"		{\r\n" + 
				"			\"timestamp\": \"1586944294\",\r\n" + 
				"			\"sensorMeasurment\": [\r\n" + 
				"				{\r\n" + 
				"					\"measurement\": \"ax\",\r\n" + 
				"					\"value\": \"390\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"measurement\": \"ay\",\r\n" + 
				"					\"value\": \"-935\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"measurement\": \"az\",\r\n" + 
				"					\"value\": \"-29\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"measurement\": \"battery\",\r\n" + 
				"					\"value\": \"2977\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"measurement\": \"humidity\",\r\n" + 
				"					\"value\": \"48\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"measurement\": \"pressure\",\r\n" + 
				"					\"value\": \"99555\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"measurement\": \"rssi\",\r\n" + 
				"					\"value\": \"-93\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"measurement\": \"temperature\",\r\n" + 
				"					\"value\": \"1420\"\r\n" + 
				"				}\r\n" + 
				"			]\r\n" + 
				"		},\r\n" + 
				"		{\r\n" + 
				"			\"timestamp\": \"1586944300\",\r\n" + 
				"			\"sensorMeasurment\": [\r\n" + 
				"				{\r\n" + 
				"					\"measurement\": \"ax\",\r\n" + 
				"					\"value\": \"176\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"measurement\": \"ay\",\r\n" + 
				"					\"value\": \"-100\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"measurement\": \"az\",\r\n" + 
				"					\"value\": \"-989\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"measurement\": \"battery\",\r\n" + 
				"					\"value\": \"3223\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"measurement\": \"humidity\",\r\n" + 
				"					\"value\": \"30\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"measurement\": \"pressure\",\r\n" + 
				"					\"value\": \"99581\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"measurement\": \"rssi\",\r\n" + 
				"					\"value\": \"-77\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"measurement\": \"temperature\",\r\n" + 
				"					\"value\": \"2277\"\r\n" + 
				"				}\r\n" + 
				"			]\r\n" + 
				"		},\r\n" + 
				"		{\r\n" + 
				"			\"timestamp\": \"1586944400\",\r\n" + 
				"			\"sensorMeasurment\": [\r\n" + 
				"				{\r\n" + 
				"					\"measurement\": \"ax\",\r\n" + 
				"					\"value\": \"717\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"measurement\": \"ay\",\r\n" + 
				"					\"value\": \"-219\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"measurement\": \"az\",\r\n" + 
				"					\"value\": \"-4\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"measurement\": \"battery\",\r\n" + 
				"					\"value\": \"3079\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"measurement\": \"humidity\",\r\n" + 
				"					\"value\": \"40\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"measurement\": \"pressure\",\r\n" + 
				"					\"value\": \"100800\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"measurement\": \"rssi\",\r\n" + 
				"					\"value\": \"-69\"\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					\"measurement\": \"temperature\",\r\n" + 
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
