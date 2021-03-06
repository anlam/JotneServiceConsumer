package no.hiof.arrowhead.JotneWriteServiceConsumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;

import eu.arrowhead.client.library.ArrowheadService;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.SSLProperties;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.dto.shared.OrchestrationFlags.Flag;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO.Builder;
import eu.arrowhead.common.dto.shared.OrchestrationResponseDTO;
import eu.arrowhead.common.dto.shared.OrchestrationResultDTO;
import eu.arrowhead.common.dto.shared.ServiceInterfaceResponseDTO;
import eu.arrowhead.common.dto.shared.ServiceQueryFormDTO;
import eu.arrowhead.common.exception.InvalidParameterException;
import no.hiof.arrowhead.JotneServiceConsumerCommon.Constants;
import no.hiof.arrowhead.JotneServiceConsumerCommon.Constants.SensorType;
import no.hiof.tellu.connector.NewMQTTClient;
import no.hiof.tellu.model.JotneSensorDataDTO;

@SpringBootApplication
@ComponentScan(basePackages = { CommonConstants.BASE_PACKAGE, Constants.BASE_PACKAGE })
public class ConsumerWriteMain implements ApplicationRunner {

	// =================================================================================================
	// members

	@Autowired
	private ArrowheadService arrowheadService;

	@Autowired
	protected SSLProperties sslProperties;

	private OrchestrationResultDTO orchestrationResult;

	private final Logger logger = LogManager.getLogger(ConsumerWriteMain.class);

	// =================================================================================================
	// methods

	// ------------------------------------------------------------------------------------------------
	public static void main(final String[] args) {
		SpringApplication.run(ConsumerWriteMain.class, args);
	}

	// -------------------------------------------------------------------------------------------------
	@Override
	public void run(final ApplicationArguments args) throws Exception {

		orchestrationResult = getWriteServiceInfo();
		
		if(orchestrationResult != null)
		{
			logger.info("Starting MQTT Client");
			NewMQTTClient mqtt_client = new NewMQTTClient();
			mqtt_client.setConsumerWrite(this);
			mqtt_client.initialize();
		}

		
	}

	public boolean writeSensorData(JotneSensorDataDTO jotneSensorDataDTO, SensorType sensorType, String sensorID) {
		
		
		String proj = "Bike";
		String sn = sensorID.trim();
		String prop = "urn:rdl:Bike:point list";
		
		if(sensorType == SensorType.GPS_ALTITUDE)
			prop = "urn:rdl:Bike:altitude list"; 
		else if(sensorType == SensorType.GPS_POSITION)
			prop = "urn:rdl:Bike:position list";
		
		if(sensorType != SensorType.RUUVI)
			if(sn.equals("P4GW1002"))
			{
				sn = "1180322-02701"; 
				jotneSensorDataDTO.setId(sn);
			}
		
		
		String serviceURI = "/" + proj + "/" + sn + "/" + prop;
			
		logger.info("Writing data");
		final String token = orchestrationResult.getAuthorizationTokens() == null ? null
				: orchestrationResult.getAuthorizationTokens().get(getInterface());

		try {
			final String result = arrowheadService.consumeServiceHTTP(String.class,
					HttpMethod.valueOf(orchestrationResult.getMetadata().get(Constants.HTTP_METHOD)),
					orchestrationResult.getProvider().getAddress(), orchestrationResult.getProvider().getPort(),
					orchestrationResult.getServiceUri() + serviceURI, getInterface(), token, jotneSensorDataDTO,
					new String[0]);

			logger.info("Result: " + result);
		} catch (Exception e) {
			logger.error("Error while writing sensor data: " + e.getMessage());
			logger.info("Updating service registry");
			orchestrationResult = getWriteServiceInfo();
			
			/*
			 * logger.info("Re-writing data"); final String result =
			 * arrowheadService.consumeServiceHTTP(String.class,
			 * HttpMethod.valueOf(orchestrationResult.getMetadata().get(Constants.
			 * HTTP_METHOD)), orchestrationResult.getProvider().getAddress(),
			 * orchestrationResult.getProvider().getPort(),
			 * orchestrationResult.getServiceUri() + serviceURI, getInterface(), token,
			 * jotneSensorDataDTO, new String[0]); logger.info("Result: " + result);
			 */
			return false;
		}
		return true;

	}

	private OrchestrationResultDTO getWriteServiceInfo() {
		final ServiceQueryFormDTO serviceQueryForm = new ServiceQueryFormDTO.Builder(
				Constants.TRUEPLM_ADD_SENSOR_SERVICE_DEFINITION).interfaces(getInterface()).build();

		final Builder orchestrationFormBuilder = arrowheadService.getOrchestrationFormBuilder();
		final OrchestrationFormRequestDTO orchestrationFormRequest = orchestrationFormBuilder
				.requestedService(serviceQueryForm).flag(Flag.TRIGGER_INTER_CLOUD, true).flag(Flag.OVERRIDE_STORE, true)
				.flag(Flag.ENABLE_INTER_CLOUD, true).build();

		logger.info("Orchestration request for " + Constants.TRUEPLM_ADD_SENSOR_SERVICE_DEFINITION + " service:");
		printOut(orchestrationFormRequest);

		final OrchestrationResponseDTO orchestrationResponse = arrowheadService
				.proceedOrchestration(orchestrationFormRequest);

		logger.info("Orchestration response:");
		printOut(orchestrationResponse);

		if (orchestrationResponse == null) {
			logger.info("No orchestration response received");
		} else if (orchestrationResponse.getResponse().isEmpty()) {
			logger.info("No provider found during the orchestration");
		} else {
			final OrchestrationResultDTO orchestrationResult = orchestrationResponse.getResponse().get(0);
			validateOrchestrationResult(orchestrationResult, Constants.TRUEPLM_ADD_SENSOR_SERVICE_DEFINITION);

			return orchestrationResult;
		}

		return null;
	}

	// =================================================================================================
	// assistant methods

	// -------------------------------------------------------------------------------------------------
	private String getInterface() {
		return sslProperties.isSslEnabled() ? Constants.INTERFACE_SECURE : Constants.INTERFACE_INSECURE;
	}

	// -------------------------------------------------------------------------------------------------
	private void validateOrchestrationResult(final OrchestrationResultDTO orchestrationResult,
			final String serviceDefinition) {
		if (!orchestrationResult.getService().getServiceDefinition().equalsIgnoreCase(serviceDefinition)) {
			throw new InvalidParameterException("Requested and orchestrated service definition do not match");
		}

		boolean hasValidInterface = false;
		for (final ServiceInterfaceResponseDTO serviceInterface : orchestrationResult.getInterfaces()) {
			if (serviceInterface.getInterfaceName().equalsIgnoreCase(getInterface())) {
				hasValidInterface = true;
				break;
			}
		}
		if (!hasValidInterface) {
			throw new InvalidParameterException("Requested and orchestrated interface do not match");
		}
	}

	// -------------------------------------------------------------------------------------------------
	private void printOut(final Object object) {
		System.out.println(Utilities.toPrettyJson(Utilities.toJson(object)));
	}
}
