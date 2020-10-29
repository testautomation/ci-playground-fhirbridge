package org.ehrbase.fhirbridge.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.context.support.IValidationSupport;
import ca.uhn.fhir.narrative.DefaultThymeleafNarrativeGenerator;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.CorsInterceptor;
import ca.uhn.fhir.rest.server.interceptor.RequestValidatingInterceptor;
import org.ehrbase.fhirbridge.FhirBridgeException;
import org.ehrbase.fhirbridge.fhir.provider.AbstractResourceProvider;
import org.ehrbase.fhirbridge.fhir.validation.RemoteTerminologyServerValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.CachingValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.CommonCodeSystemsTerminologyService;
import org.hl7.fhir.common.hapi.validation.support.InMemoryTerminologyServerValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.PrePopulatedValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.cors.CorsConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Configuration
public class FhirConfiguration {

    private final Logger logger = LoggerFactory.getLogger(FhirConfiguration.class);

    private final FhirProperties fhirProperties;

    private final ResourcePatternResolver resourceLoader;

    private final ListableBeanFactory beanFactory;

    public FhirConfiguration(FhirProperties fhirProperties, ResourcePatternResolver resourceLoader,
                             ListableBeanFactory beanFactory) {
        this.fhirProperties = fhirProperties;
        this.resourceLoader = resourceLoader;
        this.beanFactory = beanFactory;
    }

    public FhirProperties getFhirProperties() {
        return this.fhirProperties;
    }

    @Bean
    public FhirContext fhirContext() {
        FhirContext context = FhirContext.forR4();
        if (fhirProperties.isNarrativeGeneration()) {
            context.setNarrativeGenerator(new DefaultThymeleafNarrativeGenerator());
        }
        return context;
    }

    @Bean
    public ServletRegistrationBean<RestfulServer> fhirServletRegistration() {
        ServletRegistrationBean<RestfulServer> bean = new ServletRegistrationBean<>(fhirServlet(), fhirProperties.getUrlMapping());
        bean.setLoadOnStartup(1);
        return bean;
    }

    @Bean
    public RestfulServer fhirServlet() {
        RestfulServer server = new RestfulServer(fhirContext());
        server.registerProviders(
                beanFactory.getBeansOfType(AbstractResourceProvider.class).values()
        );
        server.registerInterceptor(requestValidatingInterceptor());
        server.registerInterceptor(corsValidatingInterceptor());
        return server;
    }

    @Bean
    public RequestValidatingInterceptor requestValidatingInterceptor() {
        // Configure the ValidationSupportChain
        ValidationSupportChain supportChain = new ValidationSupportChain();
        supportChain.addValidationSupport(new DefaultProfileValidationSupport(fhirContext()));
        supportChain.addValidationSupport(prePopulatedValidationSupport());

        // Support for embedded terminology validation
        if (fhirProperties.getValidation().getTerminology().getMode() == TerminologyMode.EMBEDDED) {
            supportChain.addValidationSupport(new InMemoryTerminologyServerValidationSupport(fhirContext()));
            supportChain.addValidationSupport(new CommonCodeSystemsTerminologyService(fhirContext()));
        }


        // *********************************************************************************************************
        // Support for remote validation
        //
        if (fhirProperties.getValidation().getTerminology().getMode() == TerminologyMode.REMOTE) {
            supportChain.addValidationSupport(remoteTerminologyServerValidationSupport());
        } else {
            logger.debug(">>>>>>>>>>>>> NOT DOING REMOTE VALIDATION");
        }

        // ValidatorModule configuration
        FhirInstanceValidator validatorModule = new FhirInstanceValidator(new CachingValidationSupport(supportChain));
        validatorModule.setErrorForUnknownProfiles(true);
        validatorModule.setNoTerminologyChecks(fhirProperties.getValidation().getTerminology().getMode() == TerminologyMode.OFF);
        //
        // *********************************************************************************************************

        // Interceptor configuration
        RequestValidatingInterceptor interceptor = new RequestValidatingInterceptor();
        interceptor.addValidatorModule(validatorModule);
        return interceptor;
    }

    @Bean
    public CorsInterceptor corsValidatingInterceptor() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedHeader("x-fhir-starter");
        config.addAllowedHeader("Origin");
        config.addAllowedHeader("Accept");
        config.addAllowedHeader("X-Requested-With");
        config.addAllowedHeader("Content-Type");

        config.addAllowedOrigin("*");

        config.addExposedHeader("Location");
        config.addExposedHeader("Content-Location");
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Create the interceptor and register it
        return new CorsInterceptor(config);
    }

    @Bean
    public PrePopulatedValidationSupport prePopulatedValidationSupport() {
        PrePopulatedValidationSupport validationSupport = new PrePopulatedValidationSupport(fhirContext());
        IParser parser = fhirContext().newXmlParser();
        try {
            for (Resource resource : resourceLoader.getResources("classpath:/profiles/*")) {
                try (InputStream in = resource.getInputStream()) {
                    StructureDefinition structureDefinition = parser.parseResource(StructureDefinition.class, in);
                    validationSupport.addStructureDefinition(structureDefinition);
                    logger.info("Profile {} {{}} loaded for {}", structureDefinition.getName(), structureDefinition.getUrl(), structureDefinition.getType());
                }
            }
        } catch (IOException e) {
            throw new FhirBridgeException("Profiles initialization failed", e);
        }
        return validationSupport;
    }

    @Bean
    @ConditionalOnProperty(name = "fhir-bridge.fhir.validation.terminology.mode", havingValue = "remote")
    public IValidationSupport remoteTerminologyServerValidationSupport() {
        String serverUrl = fhirProperties.getValidation().getTerminology().getRemote().getServerUrl();
        IGenericClient client = fhirContext().newRestfulGenericClient(serverUrl);
        return new RemoteTerminologyServerValidationSupport(fhirContext(), client);
    }

}
