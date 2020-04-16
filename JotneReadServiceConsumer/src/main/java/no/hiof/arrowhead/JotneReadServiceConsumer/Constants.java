package no.hiof.arrowhead.JotneReadServiceConsumer;

public class Constants {
	
	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "no.hiof";
	
	public static final String INTERFACE_SECURE = "HTTPS-SECURE-JSON";
	public static final String INTERFACE_INSECURE = "HTTP-INSECURE-JSON";
	public static final String HTTP_METHOD = "http-method";
	
	public static final String TRUEPLM_GET_SENSOR_SERVICE_DEFINITION = "trueplm-get-sensor-data-sevice";
	
	public static final String TRUEPLM_ADD_SENSOR_SERVICE_DEFINITION = "trueplm-add-sensor-data-sevice";
	
	
	//=================================================================================================
	// assistant methods
		
	//-------------------------------------------------------------------------------------------------
	private Constants() {
		throw new UnsupportedOperationException();
	}
}
