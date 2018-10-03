package roart;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import roart.exception.NotAllowedException;
import roart.exception.NotFoundException;
import roart.exception.OtherException;

public class RestUtil {
    private static Logger log = LoggerFactory.getLogger(RestUtil.class);

    public static <T> T sendMe(Class<T> myclass, String url, String param, String identifier) throws NotAllowedException, NotFoundException, OtherException {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");
        map.put("Client-Identifier", identifier);

        headers.setAll(map);

        final HttpEntity<String> entity = new HttpEntity<>(headers);
        
        RestTemplate rt = new RestTemplate();
        for (HttpMessageConverter<?> converter : rt.getMessageConverters()) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter jsonConverter = (MappingJackson2HttpMessageConverter) converter;
                ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                jsonConverter.setObjectMapper(objectMapper);
            }           
        }
        if (param != null) {
            url = url + "/" + param;
        }
        ResponseEntity<T> response = null;
        try {
            response = rt.exchange(url , HttpMethod.GET, entity, myclass);
        } catch (HttpClientErrorException ex) {
            log.info("Exception {}", ex.getMessage());
            String msg = ex.getMessage();
            if ("401 Unauthorized".equals(msg)) {
                throw new NotAllowedException("Not authorized");
            }
            if ("404 Not Found".equals(msg)) {
                throw new NotFoundException("URL not found");
            }
            throw new OtherException();
        }
        return response.getBody();
    }


}
