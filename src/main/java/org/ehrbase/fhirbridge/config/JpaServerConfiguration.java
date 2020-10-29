package org.ehrbase.fhirbridge.config;

import ca.uhn.fhir.jpa.api.config.DaoConfig;
import ca.uhn.fhir.jpa.config.BaseJavaConfigR4;
import ca.uhn.fhir.jpa.model.config.PartitionSettings;
import ca.uhn.fhir.jpa.model.entity.ModelConfig;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;

import javax.persistence.EntityManagerFactory;

@Configuration
@EntityScan(basePackages = {
        "ca.uhn.fhir.jpa.entity",
        "ca.uhn.fhir.jpa.model.entity"
})
public class JpaServerConfiguration extends BaseJavaConfigR4 {

    @Bean
    public DaoConfig daoConfig() {
        DaoConfig config = new DaoConfig();
        config.setAllowExternalReferences(true);
        config.getTreatReferencesAsLogical().add("urn:uuid:*");
        return config;
    }

    @Bean
    public ModelConfig modelConfig() {
        ModelConfig config = new ModelConfig();
        config.setAllowExternalReferences(true);
        config.getTreatReferencesAsLogical().add("urn:uuid:*");
        return config;
    }

    @Bean
    public PartitionSettings partitionSettings() {
        return new PartitionSettings();
    }

    @Primary
    @Bean
    public JpaTransactionManager hapiTransactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
}
