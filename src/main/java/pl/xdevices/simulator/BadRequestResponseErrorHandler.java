package pl.xdevices.simulator;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

public class BadRequestResponseErrorHandler implements ResponseErrorHandler {

    private Logger logger;

    public BadRequestResponseErrorHandler(Logger logger) {
        this.logger = logger;
    }

    @Override
    public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
        return clientHttpResponse.getStatusCode().equals(HttpStatus.BAD_REQUEST);
    }

    @Override
    public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
        ErrorMsg errorMsg = new ErrorMsg(clientHttpResponse.getRawStatusCode(), "Something went wrong!", clientHttpResponse.getStatusText());
        logger.info(errorMsg.toString());

    }
}
