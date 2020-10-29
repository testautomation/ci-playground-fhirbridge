package org.ehrbase.fhirbridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;

@SpringBootApplication(exclude = ElasticsearchRestClientAutoConfiguration.class)
public class FhirBridgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(FhirBridgeApplication.class, args);
    }
}