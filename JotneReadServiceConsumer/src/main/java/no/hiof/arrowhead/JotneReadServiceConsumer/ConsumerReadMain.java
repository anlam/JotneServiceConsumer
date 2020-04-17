package no.hiof.arrowhead.JotneReadServiceConsumer;

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
import no.hiof.tellu.model.JotneSensorDataDTO;

@SpringBootApplication
@ComponentScan(basePackages = {CommonConstants.BASE_PACKAGE, Constants.BASE_PACKAGE})
public class ConsumerReadMain implements ApplicationRunner {
    
    //=================================================================================================
	// members
	
    @Autowired
	private ArrowheadService arrowheadService;
    
    @Autowired
	protected SSLProperties sslProperties;
    
	private final Logger logger = LogManager.getLogger( ConsumerReadMain.class );
    
    //=================================================================================================
	// methods

	//------------------------------------------------------------------------------------------------
    public static void main( final String[] args ) {
    	SpringApplication.run(ConsumerReadMain.class, args);
    }

    //-------------------------------------------------------------------------------------------------
    @Override
	public void run(final ApplicationArguments args) throws Exception {
    	final ServiceQueryFormDTO serviceQueryForm = new ServiceQueryFormDTO.Builder(Constants.TRUEPLM_GET_SENSOR_SERVICE_DEFINITION)
																			.interfaces(getInterface())
																			.build();
    	
    	final Builder orchestrationFormBuilder = arrowheadService.getOrchestrationFormBuilder();
		final OrchestrationFormRequestDTO orchestrationFormRequest = orchestrationFormBuilder.requestedService(serviceQueryForm)
																					   		 .flag(Flag.TRIGGER_INTER_CLOUD, true)
																					   		 .flag(Flag.OVERRIDE_STORE, true)
																					   		 .flag(Flag.ENABLE_INTER_CLOUD, true)
																					   		 .build();
		
		logger.info("Orchestration request for " + Constants.TRUEPLM_GET_SENSOR_SERVICE_DEFINITION + " service:");
		printOut(orchestrationFormRequest);
		
		final OrchestrationResponseDTO orchestrationResponse = arrowheadService.proceedOrchestration(orchestrationFormRequest);
		
		logger.info("Orchestration response:");
		printOut(orchestrationResponse);
		
		if (orchestrationResponse == null) {
			logger.info("No orchestration response received");
		} else if (orchestrationResponse.getResponse().isEmpty()) {
			logger.info("No provider found during the orchestration");
		} else {
			final OrchestrationResultDTO orchestrationResult = orchestrationResponse.getResponse().get(0);
			validateOrchestrationResult(orchestrationResult, Constants.TRUEPLM_GET_SENSOR_SERVICE_DEFINITION);
			final String token = orchestrationResult.getAuthorizationTokens() == null ? null : orchestrationResult.getAuthorizationTokens().get(getInterface());
			
			//final String[] queryParamEurHuf = {orchestrationResult.getMetadata().get(Constants.REQUEST_PARAM_META_CURRENCY_RELATION), orchestrationResult.getMetadata().get(Constants.REQUEST_PARAM_META_EUR_HUF_VALUE)};
			@SuppressWarnings("unchecked")
	
			String address = "/Bike/13483027/urn:rdl:Bike:point list";
			final JotneSensorDataDTO exchangeRateEurHuf = arrowheadService.consumeServiceHTTP(JotneSensorDataDTO.class, HttpMethod.valueOf(orchestrationResult.getMetadata().get(Constants.HTTP_METHOD)),
																					orchestrationResult.getProvider().getAddress(), orchestrationResult.getProvider().getPort(), orchestrationResult.getServiceUri() + address,
																					getInterface(), token, null, new String[0]);
			logger.info("Get sensor data:");
			printOut(exchangeRateEurHuf);
			
			//final String[] queryParamHufEur = {orchestrationResult.getMetadata().get(Constants.REQUEST_PARAM_META_CURRENCY_RELATION), orchestrationResult.getMetadata().get(Constants.REQUEST_PARAM_META_HUF_EUR_VALUE)};
			//@SuppressWarnings("unchecked")
			//final String exchangeRateHufEur = arrowheadService.consumeServiceHTTP(String.class, HttpMethod.valueOf(orchestrationResult.getMetadata().get(Constants.HTTP_METHOD)),
			//																	  orchestrationResult.getProvider().getAddress(), orchestrationResult.getProvider().getPort(), orchestrationResult.getServiceUri(),
			//																	  getInterface(), token, null, queryParamHufEur);
			//logger.info("Get HUF-EUR exchange rate:");
			//printOut(exchangeRateHufEur);
		}
	}
    
    //=================================================================================================
	// assistant methods
    
    //-------------------------------------------------------------------------------------------------
    private String getInterface() {
    	return sslProperties.isSslEnabled() ? Constants.INTERFACE_SECURE : Constants.INTERFACE_INSECURE;
    }
    
    //-------------------------------------------------------------------------------------------------
    private void validateOrchestrationResult(final OrchestrationResultDTO orchestrationResult, final String serviceDefinition) {
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
    
    //-------------------------------------------------------------------------------------------------
    private void printOut(final Object object) {
    	System.out.println(Utilities.toPrettyJson(Utilities.toJson(object)));
    }
}
