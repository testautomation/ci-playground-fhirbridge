package org.ehrbase.fhirbridge.config.util;

import org.apache.xmlbeans.XmlException;
import org.ehrbase.client.templateprovider.TemplateProvider;
import org.openehr.schemas.v1.OPERATIONALTEMPLATE;
import org.openehr.schemas.v1.TemplateDocument;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DataTemplateProvider implements TemplateProvider {

    public List<String> listTemplateIds() {
        return Arrays.stream(OperationalTemplateData.values())
                .map(OperationalTemplateData::getTemplateId)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<OPERATIONALTEMPLATE> find(String s) {
        return Optional.ofNullable(OperationalTemplateData.findByTemplateId(s))
                .map(OperationalTemplateData::getStream)
                .map(t -> {
                    try {
                        return TemplateDocument.Factory.parse(t);
                    } catch (XmlException | IOException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                })
                .map(TemplateDocument::getTemplate);
    }
}
