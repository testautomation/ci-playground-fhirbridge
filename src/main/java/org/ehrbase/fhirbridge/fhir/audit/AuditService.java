package org.ehrbase.fhirbridge.fhir.audit;

import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.AuditEvent;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

@Service
public class AuditService {

    private final Logger logger = LoggerFactory.getLogger(AuditService.class);

    private final AuditEventEntityTypeResolver entityTypeResolver = new AuditEventEntityTypeResolver();

    private final IFhirResourceDao<AuditEvent> auditEventDao;

    private final BuildProperties buildProperties;

    private final String hostAddress;

    public AuditService(IFhirResourceDao<AuditEvent> auditEventDao, BuildProperties buildProperties) throws UnknownHostException {
        this.auditEventDao = auditEventDao;
        this.buildProperties = buildProperties;

        hostAddress = InetAddress.getLocalHost().getHostAddress();
    }

    public void registerCreateResourceSuccessEvent(Resource resource) {
        AuditEvent auditEvent = defaultAuditEvent(false)
                .setType(AuditEventType.REST)
                .setAction(AuditEvent.AuditEventAction.C)
                .setOutcome(AuditEvent.AuditEventOutcome._0)
                .setOutcomeDesc("Resource created successfully")
                .addEntity(buildEntity(resource));
        registerAuditEvent(auditEvent);
    }

    public void registerMapResourceEvent(AuditEvent.AuditEventOutcome outcome, String outcomeDescription, Resource resource) {
        AuditEvent auditEvent = defaultAuditEvent(true)
                .setType(AuditEventType.TRANSFORM)
                .setAction(AuditEvent.AuditEventAction.E)
                .setOutcome(outcome)
                .setOutcomeDesc(outcomeDescription)
                .addEntity(buildEntity(resource));
        registerAuditEvent(auditEvent);
    }

    public void registerAuditEvent(AuditEvent auditEvent) {
        auditEventDao.create(auditEvent);
        logger.debug("Created audit event: {}", auditEvent);
    }

    private AuditEvent defaultAuditEvent(boolean initiator) {
        // @formatter:off
        return new AuditEvent()
            .setRecorded(new Date())
            .addAgent(new AuditEvent.AuditEventAgentComponent()
                .addRole(new CodeableConcept()
                    .addCoding(new Coding()
                        .setSystem("http://terminology.hl7.org/CodeSystem/extra-security-role-type")
                        .setCode("dataprocessor")
                        .setDisplay("data processor")))
                .setWho(new Reference()
                    .setIdentifier(new Identifier()
                        .setValue(buildProperties.getName() + " - " + buildProperties.getVersion())))
                .setRequestor(initiator)
                .setNetwork(new AuditEvent.AuditEventAgentNetworkComponent()
                    .setAddress(hostAddress)
                    .setType(AuditEvent.AuditEventAgentNetworkType._2)));
        // @formatter:on
    }

    private AuditEvent.AuditEventEntityComponent buildEntity(Resource resource) {
        // @formatter:off
        return new AuditEvent.AuditEventEntityComponent()
            .setWhat(new Reference()
                .setReference(resource.getId()))
            .setType(entityTypeResolver.resolve(resource.getClass()));
        // @formatter:on
    }

    public AuditEvent getAuditEvent(IIdType id) {
        return auditEventDao.read(id);
    }

    public IBundleProvider searchAuditEvent(SearchParameterMap params) {

        return auditEventDao.search(params);
    }
}
