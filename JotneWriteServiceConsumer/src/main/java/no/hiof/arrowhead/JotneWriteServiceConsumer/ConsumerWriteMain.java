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

		logger.info("Test Writing Service");
		String testmsg = "{\r\n" + "	\"id\": \"13483027\",\r\n" + "	\"sensorType\": \"RUUVi\",\r\n"
				+ "	\"sensorData\": [\r\n" + "		{\r\n" + "			\"timestamp\": \"1586944294\",\r\n"
				+ "			\"sensorMeasurment\": [\r\n" + "				{\r\n"
				+ "					\"measurement\": \"ax\",\r\n" + "					\"value\": \"390\"\r\n"
				+ "				},\r\n" + "				{\r\n" + "					\"measurement\": \"ay\",\r\n"
				+ "					\"value\": \"-935\"\r\n" + "				},\r\n" + "				{\r\n"
				+ "					\"measurement\": \"az\",\r\n" + "					\"value\": \"-29\"\r\n"
				+ "				},\r\n" + "				{\r\n" + "					\"measurement\": \"battery\",\r\n"
				+ "					\"value\": \"2977\"\r\n" + "				},\r\n" + "				{\r\n"
				+ "					\"measurement\": \"humidity\",\r\n" + "					\"value\": \"48\"\r\n"
				+ "				},\r\n" + "				{\r\n" + "					\"measurement\": \"pressure\",\r\n"
				+ "					\"value\": \"99555\"\r\n" + "				},\r\n" + "				{\r\n"
				+ "					\"measurement\": \"rssi\",\r\n" + "					\"value\": \"-93\"\r\n"
				+ "				},\r\n" + "				{\r\n"
				+ "					\"measurement\": \"temperature\",\r\n" + "					\"value\": \"1420\"\r\n"
				+ "				}\r\n" + "			]\r\n" + "		}\r\n" + "\r\n" + "	]\r\n" + "}";

		// logger.info(marker, message, p0, p1, p2, p3, p4, p5, p6, p7);
		String address = "/Bike/13483027/urn:rdl:Bike:point info";
		JotneSensorDataDTO jotneSensorData = Utilities.fromJson(testmsg, JotneSensorDataDTO.class);
		printOut(jotneSensorData);
		writeSensorData(jotneSensorData, address);

		logger.info("Starting MQTT Client");
		NewMQTTClient mqtt_client = new NewMQTTClient();
		mqtt_client.setConsumerWrite(this);
		// mqtt_client.setOrchestrationResult(orchestrationResult);
		mqtt_client.initialize();
	}

	public void writeSensorData(JotneSensorDataDTO jotneSensorDataDTO, String serviceURI) {

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
		}

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
