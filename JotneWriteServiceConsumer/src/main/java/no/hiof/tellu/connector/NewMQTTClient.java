package no.hiof.tellu.connector;

import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.google.gson.Gson;

import eu.arrowhead.common.dto.shared.OrchestrationResultDTO;
import no.hiof.arrowhead.JotneServiceConsumerCommon.Constants.SensorType;
import no.hiof.arrowhead.JotneWriteServiceConsumer.ConsumerWriteMain;
import no.hiof.tellu.model.GPSAltitude;
import no.hiof.tellu.model.GPSPosition;
import no.hiof.tellu.model.GPSStatus;
import no.hiof.tellu.model.GatewayHearbeatACK;
import no.hiof.tellu.model.GatewayHeartbeat;
import no.hiof.tellu.model.JotneSensorDataDTO;
import no.hiof.tellu.model.RuuviMeasurement;
import no.hiof.tellu.model.SensorDataDTO;
import no.hiof.tellu.model.SensorMeasurementDTO;


public class NewMQTTClient implements MqttCallback, IMqttActionListener {

	MqttAsyncClient client;
	String brokerUrl;
	MemoryPersistence persistence;
	MqttConnectOptions opts = new MqttConnectOptions();

	
	private ConsumerWriteMain consumerWrite;
	//private OrchestrationResultDTO orchestrationResult;
	
	
	public ConsumerWriteMain getConsumerWrite() {
		return consumerWrite;
	}

	public void setConsumerWrite(ConsumerWriteMain consumerWrite) {
		this.consumerWrite = consumerWrite;
	}

	//public OrchestrationResultDTO getOrchestrationResult() {
	//	return orchestrationResult;
	//}

	//public void setOrchestrationResult(OrchestrationResultDTO orchestrationResult) {
	//	this.orchestrationResult = orchestrationResult;
	//}




	public void initialize() throws Exception {

		brokerUrl = "ssl://mqtt.stagegw.tellucloud.com:8883";
		persistence = new MemoryPersistence();

		opts.setCleanSession(true);
		opts.setAutomaticReconnect(true);
		opts.setConnectionTimeout(30);

		String caFilePath = "tellucloud_ca_cacert.pem";
		String clientCrtFilePath = "P4Arrowhead_cert.pem";
		String clientKeyFilePath = "P4Arrowhead_key.pem";

		SSLSocketFactory socketFactory = getSocketFactory(caFilePath, clientCrtFilePath, clientKeyFilePath, "");
		opts.setSocketFactory(socketFactory);

		client = new MqttAsyncClient(brokerUrl, "P4Arrowhead_Client" + System.currentTimeMillis(), persistence);
		client.setCallback(this);

		IMqttToken token = client.connect(opts, null, this);
		token.waitForCompletion();

		// Subscribe to all the gateways
		//for (int i = 0; i < 6; i++) {
		//	client.subscribe("tellu/P4GW100" + i + "/#", 0);
		//}
		
		//client.subscribe("tellu/P4GW1004" +  "/#", 0);
		//client.subscribe("tellu/P4GW1003" +  "/#", 0);
		client.subscribe("tellu/P4GW1002"  + "/#", 0);

	}

	@Override
	public void onSuccess(IMqttToken imt) {

	}

	@Override
	public void onFailure(IMqttToken imt, Throwable thrwbl) {
		Logger.getLogger(NewMQTTClient.class.getName()).log(Level.SEVERE, "MQTT Failure.", thrwbl);
	}

	protected void cleanupAndDie() {
		try {
			client.disconnect();
		} catch (MqttException ex) {
			Logger.getLogger(NewMQTTClient.class.getName()).log(Level.SEVERE, null, ex);
		}
		System.exit(0);
	}
	
	
	protected void cleanupAndReconnect() {
		try {
			client.disconnect();
		} catch (MqttException ex) {
			Logger.getLogger(NewMQTTClient.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		try {
			NewMQTTClient mqtt_cl = new NewMQTTClient();
			mqtt_cl.setConsumerWrite(consumerWrite);
			//mqtt_cl.setOrchestrationResult(orchestrationResult);
			mqtt_cl.initialize();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
			
		}
		
	}

	@Override
	public void connectionLost(Throwable arg0) {
		cleanupAndReconnect();
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {

	}

	/*
	 * @Override public void messageArrived(String topic, MqttMessage msg) throws
	 * Exception { System.out.println(topic); System.out.println(new
	 * String(msg.getPayload())); }
	 */

	private static SSLSocketFactory getSocketFactory(final String caCrtFile, final String crtFile, final String keyFile,
			final String password) throws Exception {

		Security.addProvider(new BouncyCastleProvider());

		// load CA certificate
		X509Certificate caCert = null;

		BufferedInputStream bis = new BufferedInputStream(
				NewMQTTClient.class.getClassLoader().getResourceAsStream(caCrtFile));
		CertificateFactory cf = CertificateFactory.getInstance("X.509");

		while (bis.available() > 0) {
			caCert = (X509Certificate) cf.generateCertificate(bis);
			// System.out.println(caCert.toString());
		}

		// load client certificate
		bis = new BufferedInputStream(NewMQTTClient.class.getClassLoader().getResourceAsStream(crtFile));
		X509Certificate cert = null;
		while (bis.available() > 0) {
			cert = (X509Certificate) cf.generateCertificate(bis);
			// System.out.println(caCert.toString());
		}

		// load client private key
		PEMParser pemParser = new PEMParser(
				new InputStreamReader(NewMQTTClient.class.getClassLoader().getResourceAsStream(keyFile)));
		Object object = pemParser.readObject();
		PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder().build(password.toCharArray());
		JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
		KeyPair key;
		if (object instanceof PEMEncryptedKeyPair) {
			System.out.println("Encrypted key - we will use provided password");
			key = converter.getKeyPair(((PEMEncryptedKeyPair) object).decryptKeyPair(decProv));
		} else {
			System.out.println("Unencrypted key - no password needed");
			key = converter.getKeyPair((PEMKeyPair) object);
		}
		pemParser.close();

		// CA certificate is used to authenticate server
		KeyStore caKs = KeyStore.getInstance(KeyStore.getDefaultType());
		caKs.load(null, null);
		caKs.setCertificateEntry("ca-certificate", caCert);
		TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
		tmf.init(caKs);

		// client key and certificates are sent to server so it can authenticate
		// us
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(null, null);
		ks.setCertificateEntry("certificate", cert);
		ks.setKeyEntry("private-key", key.getPrivate(), password.toCharArray(),
				new java.security.cert.Certificate[] { cert });
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(ks, password.toCharArray());

		// finally, create SSL socket factory
		SSLContext context = SSLContext.getInstance("TLSv1.2");
		context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

		return context.getSocketFactory();
	}

	public static void main(String[] args) throws Exception {

		new NewMQTTClient().initialize();

	}


	public void messageArrived(String arg0, MqttMessage arg1) throws Exception {

		
		//System.out.println(new String(arg1.getPayload()));

		String topic = arg0;
		String payload = new String(arg1.getPayload());

		String[] strs = topic.split("/");
		//String item_prefix = topic.replaceAll("/", ".");
		
		
		//	item_prefix = item_prefix.substring(6);

		//String serviceURI = "/Bike/13483027/urn:rdl:Bike:point list";

		String sub_topic = strs[strs.length - 1];
		
		Gson gson = new Gson();
		
		String gatewayID = "";
		
		if(topic.startsWith("tellu"))
			gatewayID = strs[1];
		

		
		

		if (sub_topic.equals("ruuvi_measurement")) {
			RuuviMeasurement pl = gson.fromJson(payload, RuuviMeasurement.class);

			//String dv = String.valueOf(pl.getDeviceID());
			
			//System.out.println(arg0);
			System.out.println(new String(arg1.getPayload()));
			
			List<SensorMeasurementDTO> sensorMeasurments = new ArrayList<SensorMeasurementDTO>();
			sensorMeasurments.add(new SensorMeasurementDTO("ax", String.valueOf(pl.getAx())));
			sensorMeasurments.add(new SensorMeasurementDTO("ay", String.valueOf(pl.getAy())));
			sensorMeasurments.add(new SensorMeasurementDTO("az", String.valueOf(pl.getAz())));
			sensorMeasurments.add(new SensorMeasurementDTO("battery", String.valueOf(pl.getBattery())));
			sensorMeasurments.add(new SensorMeasurementDTO("humidity", String.valueOf(pl.getHumidity())));
			sensorMeasurments.add(new SensorMeasurementDTO("pressure", String.valueOf(pl.getPressure())));
			sensorMeasurments.add(new SensorMeasurementDTO("rssi", String.valueOf(pl.getRssi())));
			sensorMeasurments.add(new SensorMeasurementDTO("temperature", String.valueOf(pl.getTemperature())));
 			
			List<SensorDataDTO> sensorData = new ArrayList<SensorDataDTO>();
			sensorData.add(new SensorDataDTO(String.valueOf(pl.getTimestamp()), sensorMeasurments));
			JotneSensorDataDTO jotneSensorData = new JotneSensorDataDTO("RUUVi", String.valueOf(pl.getDeviceID()), sensorData);
			
			consumerWrite.writeSensorData(jotneSensorData, SensorType.RUUVI, String.valueOf(pl.getDeviceID()));
			
			


		} else if (sub_topic.equals("gateway_heartbeat")) {
			GatewayHeartbeat pl = gson.fromJson(payload, GatewayHeartbeat.class);


		} else if (sub_topic.equals("gateway_heartbeat_ack")) {
			GatewayHearbeatACK pl = gson.fromJson(payload, GatewayHearbeatACK.class);


		} else if (sub_topic.equals("gps_status")) {
			GPSStatus pl = gson.fromJson(payload, GPSStatus.class);

		} else if (sub_topic.equals("gps_position")) {
			GPSPosition pl = gson.fromJson(payload, GPSPosition.class);
			
			System.out.println(new String(arg1.getPayload()));
			
			List<SensorMeasurementDTO> sensorMeasurments = new ArrayList<SensorMeasurementDTO>();
			sensorMeasurments.add(new SensorMeasurementDTO("gpstime_diff", String.valueOf(pl.getGpstime())));
			sensorMeasurments.add(new SensorMeasurementDTO("latitude", String.valueOf(pl.getLatitude())));
			sensorMeasurments.add(new SensorMeasurementDTO("latitude_err", String.valueOf(pl.getLatitude_err())));
			sensorMeasurments.add(new SensorMeasurementDTO("longitude", String.valueOf(pl.getLongitude())));
			sensorMeasurments.add(new SensorMeasurementDTO("longitude_err", String.valueOf(pl.getLongitude_err())));
			sensorMeasurments.add(new SensorMeasurementDTO("speed", String.valueOf(pl.getSpeed())));
			sensorMeasurments.add(new SensorMeasurementDTO("speed_err", String.valueOf(pl.getSpeed_err())));
			sensorMeasurments.add(new SensorMeasurementDTO("track", String.valueOf(pl.getTrack())));
			sensorMeasurments.add(new SensorMeasurementDTO("track_err", String.valueOf(pl.getTrack_err())));
 			
			List<SensorDataDTO> sensorData = new ArrayList<SensorDataDTO>();
			sensorData.add(new SensorDataDTO(String.valueOf(pl.getTimestamp()), sensorMeasurments));
			JotneSensorDataDTO jotneSensorData = new JotneSensorDataDTO("urn:plcs:rdl:ArrowHead:USB_GPS", gatewayID, sensorData);
			
			consumerWrite.writeSensorData(jotneSensorData, SensorType.GPS_POSITION, gatewayID);


		} else if (sub_topic.equals("gps_altitude")) {
			GPSAltitude pl = gson.fromJson(payload, GPSAltitude.class);
			
			System.out.println(new String(arg1.getPayload()));
			
			List<SensorMeasurementDTO> sensorMeasurments = new ArrayList<SensorMeasurementDTO>();
			sensorMeasurments.add(new SensorMeasurementDTO("altitude", String.valueOf(pl.getAltitude())));
			sensorMeasurments.add(new SensorMeasurementDTO("altitude_err", String.valueOf(pl.getAltitude_err())));
			sensorMeasurments.add(new SensorMeasurementDTO("vspeed", String.valueOf(pl.getVspeed())));
			sensorMeasurments.add(new SensorMeasurementDTO("vspeed_err", String.valueOf(pl.getVspeed_err())));

 			
			List<SensorDataDTO> sensorData = new ArrayList<SensorDataDTO>();
			sensorData.add(new SensorDataDTO(String.valueOf(pl.getTimestamp()), sensorMeasurments));
			JotneSensorDataDTO jotneSensorData = new JotneSensorDataDTO("urn:plcs:rdl:ArrowHead:USB_GPS", gatewayID, sensorData);
			
			consumerWrite.writeSensorData(jotneSensorData, SensorType.GPS_ALTITUDE, gatewayID);


		} else {
			System.err.println("Unknown topic: " + topic);
			System.err.println("Payload: " + payload);
		}

		/*
		 * if (!items.isEmpty()) { String sValue = gson.toJson(items); Response
		 * getResponse = Utility.sendRequest(updateURL, "PUT", sValue);
		 * 
		 * if (getResponse.getStatus() != 200) { System.err.println("Write Failed");
		 * System.err.println("Topic: " + topic); System.err.println("Payload: " +
		 * payload); } }
		 */

	}

	// Response getResponse = Utility.sendRequest(providerUrl, "GET", null);

}
