import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by joaocambeiro on 09/05/2017.
 */
public class Main {


    private static final String APPLICATION_KEY = "99071a92-4101-33b5-b727-a082ff826ff4";
    private static final String SCHEME = "https";


    //Replace with WSO2 IOT Server IP Address;
    private static final String HOST = "localhost";
    private static final String DEVICE_STATS_PATH = "/device/stats/";
    private static final String API_DEVICE_PATH = "api/device-mgt/v1.0/devices";
    private static final String DEVICE_TYPE_LIGHT = "smartlights";

    //PowerSockets
    private static final String DEVICE_TYPE_POWER_SOCKETS = "powersocketubiquiti";

    //Light and temperature sensors
    private static final String DEVICE_TYPE_ESTIMOTE_BEACONS = "estimotebeacons";


    //Light Bulbs

    // 0 <= Hue <=360   0 <= Saturation <=1     2750 <= Kelvin <= 9000
    private static final String LIGHT_COLOR_SENSOR = "colorSensor";

    //Brightness 0 <= Brightness <= 1
    private static final String LIGHT_BRIGHTNESS_SENSOR = "brightnessSensor";

    //Power {0,1}
    private static final String LIGHT_POWER_SENSOR = "powerSensor";


    private static final String LIGHT_EXAMPLE_ID = "vb9l9ek9zygj";


    //Beacons - Light and Temperature Sensors
    //Unit lux
    private static final String BEACONS_LIGHT_SENSOR = "lightSensor";
    //Unit Degrees Celsius
    private static final String BEACONS_TEMPERATURE_SENSOR = "temperatureSensor";
    //Unit Percentage Left
    private static final String BEACONS_BATTERY_SENSOR = "batterySensor";

    //PowerSockets

    //Unit Watt
    private static final String POWER_SOCKETS_POWER_SENSOR = "powerSensor";

    //Unit Ampere
    private static final String POWER_SOCKETS_CURRENT_SENSOR = "currentSensor";

    //Status {0,1}
    private static final String POWER_SOCKETS_STATUS_SENSOR = "statusSensor";

    //Server Port
    private static final int PORT = 8243;


    /**
     *
     * @param sensorType The SensorType such as powerSensor, currentSensor, brightnessSensor...
     * @param deviceType The device Type such as powersocketubiquiti, smartlights, estimotebeacons
     * @param deviceID The WSO2 Device ID
     * @param from Initial timestamp
     * @param to End timestamp
     * @param httpClient The HTTP Client that will be used to execute the request
     * @throws IOException
     */

    private static void getSensorData(String sensorType, String deviceType, String deviceID, long from, long to,
                                      CloseableHttpClient httpClient) throws IOException, URISyntaxException {
        URIBuilder builder = new URIBuilder();
        builder.setScheme(SCHEME).setHost(HOST).setPort(PORT).setPath(deviceType + DEVICE_STATS_PATH + deviceID)
                .setParameter("sensorType", sensorType)
                .setParameter("from", String.valueOf(from))
                .setParameter("to", String.valueOf(to));
        HttpGet request = new HttpGet(builder.build());
        request.addHeader("Authorization", "Bearer " + APPLICATION_KEY);
        request.addHeader("Accept", "application/json");
        CloseableHttpResponse response = httpClient.execute(request);
        if(response.getStatusLine().getStatusCode() == 200)
            System.out.println(EntityUtils.toString(response.getEntity()));
    }


    /**
     *
     * @param deviceType The Device type such as smartlights, powersocketubiquiti, null value retrieves all device Types
     * @param httpClient The HTTP Client that will be used to execute the request
     * @throws IOException
     * @throws URISyntaxException
     *
     * Each device has the following properties associated used to determine the device's position : DEVICE_X_POS,
     * DEVICE_Y_POS, DEVICE_Z_POS
     */

    private static void getAllByDeviceType(String deviceType, CloseableHttpClient httpClient) throws IOException,
            URISyntaxException {
        URIBuilder builder = new URIBuilder();
        builder.setScheme(SCHEME).setHost(HOST).setPort(PORT).setPath(API_DEVICE_PATH)
                .setParameter("offset", "0")
                .setParameter("limit", "25");
        if(deviceType != null)
            builder.setParameter("type", deviceType);
        HttpGet request = new HttpGet(builder.build());
        request.addHeader("Authorization", "Bearer " + APPLICATION_KEY);
        request.addHeader("Accept", "application/json");
        CloseableHttpResponse response = httpClient.execute(request);
        if(response.getStatusLine().getStatusCode() == 200)
            System.out.println(EntityUtils.toString(response.getEntity()));
    }


    /**
     *
     * @return CloseableHTTPClient that will be used to execute the HTTP requests
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */

    private  static CloseableHttpClient createHTTPCLient() throws KeyStoreException, NoSuchAlgorithmException,
            KeyManagementException {
        SSLContextBuilder sslBuilder = new SSLContextBuilder();
        sslBuilder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslBuilder.build());
        return HttpClients.custom().setSSLSocketFactory(socketFactory).build();
    }

    public static void main(String[] args){
        try {
            CloseableHttpClient client = createHTTPCLient();
            getAllByDeviceType(DEVICE_TYPE_LIGHT, client);
            getSensorData(LIGHT_BRIGHTNESS_SENSOR, DEVICE_TYPE_LIGHT, LIGHT_EXAMPLE_ID, 100, 1494588808754L,
                    client);
            client.close();
        } catch (KeyStoreException | KeyManagementException | NoSuchAlgorithmException e1) {
            System.out.println("Error creating HTTP Client");
            e1.printStackTrace();
        } catch (IOException | URISyntaxException e) {
            System.out.println("Error Retrieving Data");
            e.printStackTrace();
        }
    }


}
