package pl.xdevices.simulator;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Random;

@Component
public class TemperatureSimulator {

    private static final int TEMPERATURE_SENSOR_1_DELAY_SECONDS = 10;
    private static final int TEMPERATURE_SENSOR_2_DELAY_SECONDS = 20;
    private static final int TEMPERATURE_SENSOR_3_DELAY_SECONDS = 5;

    private static final String SENSOR_1_UUID = "b2b4c9d4-8990-41cf-9dfc-c659dd28a8c3";
    private static final String SENSOR_2_UUID = "7f668e16-3124-4f30-88c2-169c361b1733";
    private static final String SENSOR_3_UUID = "invalid-uuid";

    private static final double MULTIPLY_FACTOR = 1.15;

    @Autowired
    private Logger logger;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Value("${API_HOST:xdevicesdev.home}")
    private String apiHost;

    @Value("${API_PORT:8000}")
    private String apiPort;

    private String resourceUrl;

    private RestTemplate restTemplate;

    @PostConstruct
    public void post() {
        restTemplate = restTemplateBuilder.errorHandler(new BadRequestResponseErrorHandler(logger)).build();
        resourceUrl = buildResourceUrl();
    }

    private String buildResourceUrl() {
        // NOTE: http://xdevicesdev.home:8000
        String hostUrl = String.join(":", "http", "//" + apiHost, apiPort);

        // NOTE: http://xdevicesdev.home:8000/api/dispatcher/temperature
        return String.join("/", hostUrl, "api", "dispatcher", "temperature");
    }

    @Scheduled(fixedDelay = TEMPERATURE_SENSOR_1_DELAY_SECONDS * 1000)
    public void postTemperatureSensor1() {
        logger.info("Fireing Temperature Sensor 1");
        postRequestForSensor(SENSOR_1_UUID);
    }

    @Scheduled(fixedDelay = TEMPERATURE_SENSOR_2_DELAY_SECONDS * 1000)
    public void postTemperatureSensor2() {
        logger.info("Fireing Temperature Sensor 2");
        postRequestForSensor(SENSOR_2_UUID);
    }

    @Scheduled(fixedDelay = TEMPERATURE_SENSOR_3_DELAY_SECONDS * 1000)
    public void postTemperatureSensor3() {
        logger.info("Fireing Temperature Sensor 3");
        postRequestForSensor(SENSOR_3_UUID);
    }

    private void postRequestForSensor(String uuid) {
        restTemplate.postForLocation(buildRandomizedTemperature(uuid), null);
    }

    private String buildRandomizedTemperature(String uuid) {
        StringBuilder sb = new StringBuilder();
        // NOTE: http://xdevicesdev.home:8000/api/dispatcher/temperature/b2b4c9d4-8990-41cf-9dfc-c659dd28a8c3/
        sb.append(resourceUrl);
        sb.append("/");
        sb.append(uuid);
        sb.append("/");

        Random rand = new Random();
        int r = rand.nextInt(40);
        // NOTE: r = 21
        double result = r * MULTIPLY_FACTOR;
        // NOTE: result = 21 * 1.15 = 24.15
        BigDecimal value = new BigDecimal(result, MathContext.DECIMAL32);
        sb.append(value.toString());

        // NOTE:  http://xdevicesdev.home:8000/api/dispatcher/temperature/b2b4c9d4-8990-41cf-9dfc-c659dd28a8c3/24.15
        return sb.toString();
    }
}
