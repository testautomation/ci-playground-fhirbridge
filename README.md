# FHIR Bridge

dummy change

FHIR Bridge is an official component of [EHRbase](https://ehrbase.readthedocs.io/en/latest/index.html) project. 
The purpose of the application is to act as a broker between an HL7 FHIR client and an openEHR server.

## Getting Started
### Prerequisites
#### Java
```
$ java -version
java version "11.0.2" 2019-01-15 LTS
```
FHIR Bridge requires JDK 11 or above. If you do not already have Java installed, 
you can follow the instructions at [openjdk.java.net](https://openjdk.java.net/).

#### Apache Maven
```
$ mvn --version
Apache Maven 3.6.0 (97c98ec64a1fdfee7767ce5ffb20918da4f719f3; 2018-10-24T20:41:47+02:00)
```
FHIR Bridge is compatible with Apache Maven 3.6 or above. If you do not already have Maven installed, 
you can follow the instructions at [maven.apache.org](https://maven.apache.org/).

#### EHRbase 
FHIR Bridge requires an EHRbase instance to communicate and exchange data. 
If you do not already have EHRbase up and running, you can follow the installation procedure available 
in [ehrbase/ehrbase](https://github.com/ehrbase/ehrbase) repository.

#### PostgreSQL
FHIR Bridge requires an additional database schema for its internal audit mechanism. By default, the application already
includes PostgreSQL driver. If you do not already have PostgreSQL installed, you can follow the instructions 
at [postgresql.org](https://www.postgresql.org/).

### Installation
#### Cloning the repository
```
$ git clone https://github.com/ehrbase/fhir-bridge.git
```
#### Build the application
```
$ mvn clean install
```
*Remark: Unlike the standalone execution that requires a database like PostgreSQL, the build process uses [H2](https://www.h2database.com/html/main.html) 
database to execute the Integration Tests and no additional configuration is required.*

#### Configure the application
Before running the application, you need, at least, to configure the database and EHRbase instance. The easiest solution is to 
use an `application.yml` file:
* In the same directory as `fhir-bridge-X-X-X.jar` file.
* Under a `/config` subdirectory.

The `application.yml` must include (at least) the following properties:
```
ehrbase:
  address:              Hostname of the EHRbase instance (ex: localhost)   
  port:                 Port of the EHRbase instance (ex: 8080)
  path:                 Context path of the EHRbase instance (ex: /ehrbase/rest/openehr/v1/)
spring:
  datasource:
    url:                JDBC URL of the database (ex: jdbc:postgresql://fhirdb:5432/fhir_bridge)
    username:           Username of the database (ex: fhir_bridge_usr)           
    password:           Password of the database (ex: fhir_bridge_pwd)            
```

If you want more details about configuration and/or customization of the default one, you can check the official documentation of 
[Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-external-config) 
feature provided by Spring Boot.

#### Run the application
In order to start the application, go to the correct location and use the `java` command (update the version number accordingly).
```
$ cd [fhir-bridge_jar_file_dir]
$ java -jar fhir-bridge-X.X.X.jar
```

## FHIR to openEHR Mappings

| FHIR Resource    | FHIR Profile                 | openEHR OPT                                                 |
| ---------------- | ---------------------------- | ----------------------------------------------------------- |
| DiagnosticReport | [DiagnosticReportLab][1]     | [Laborbefund][ckm1]                                         |
| Observation      | [bodytemp][2]                | [Intensivmedizinisches Monitoring Korpertemperatur][ckm2] * |
| Observation      | [CoronavirusNachweisTest][3] | [Kennzeichnung Erregernachweis SARS-CoV-2][ckm3]            |
| Condition        | -                            | [Diagnose][ckm4]                                            |

[1]: https://github.com/ehrbase/fhir-bridge/blob/master/src/main/resources/profiles/DiagnosticReportLab.xml
[2]: https://github.com/ehrbase/fhir-bridge/blob/master/src/main/resources/profiles/bodytemp.xml
[3]: https://github.com/ehrbase/fhir-bridge/blob/master/src/main/resources/profiles/CoronavirusNachweisTest.xml
[ckm1]: https://ckm.highmed.org/ckm/templates/1246.169.220
[ckm2]: https://ckm.highmed.org/ckm/templates/1246.169.671
[ckm3]: https://ckm.highmed.org/ckm/templates/1246.169.697
[ckm4]: https://ckm.highmed.org/ckm/templates/1246.169.714

* With the Korpertemperatur OPT we needed to change the template_id because it has a "ö", that makes EHRBASE fail on
  GET /template/adl1.4/$template_id. Raised an [issue][issue1] to check it.

[issue1]: https://github.com/ehrbase/project_management/issues/273

* For the Condition we don't have a FHIR profile, this case uses the native Condition resource.


### Mapping Resources

To define the data mappings, we used spreadsheets that can be found in the [mappings][map1]
folder. Those were generated from the archetypes referenced by the OPTs using the [ADL2CSV tool][map2]

[map1]: https://github.com/ehrbase/fhir-bridge/tree/master/mappings/archetypes
[map2]: https://www.youtube.com/watch?v=hMsRkIhuUsU


## Docker deployment

1. git clone git@github.com:ehrbase/fhir-bridge.git
2. cd fhir-bridge
3. mvn compile jib:dockerBuild
4. docker run -p 8888:8888 -e EHRBASE_ADDRESS=172.17.0.1 fhir-bridge

Note 1: 8888 is the default port of the API offered by the fhir-bridge app and the IP is where EHRBASE is running.

Note 2: it uses 8080 as default port to communicate to EHRBASE, if another port is needed, please add -e EHRBASE_PORT=NNNN

Note 3: if you need to run fhir-bridge on another port please add -e SERVER_PORT=MMMM and change -p MMMM:MMMM


## Testing FHIR operations

There is an [Insomnia REST Client][insomnia] configuration in the "src/test/resources/Insomnia_YYYY-MM-DD.json" file project,
that contains requests to test the FHIR interface.

From Insomnia, import the JSON file, and you will see some requests are created for you.

When you click on a request in Insomnia, you will see some parameters or URLs have a blue box, that denotes a reference to a
value contained in another request's response payload, it helps to set values dynamically for testing without manual copy and paste.

[insomnia]: https://insomnia.rest/


### Create EHR Request (EHRBASE)

Before executing the FHIR requests, one EHR should be created in EHRBASE with a specific subject_id (patient id).
This is the "POST ehr first!", that points to "http://localhost:8080/ehrbase/rest/openehr/v1/ehr".
Change this URL accordingly, depending on where you have EHRBASE running, but maintain the payload.

Then run the "GET ehr" request to get the EHR ID, which will be used by the rest of the requests to get the patient ID back
(used in the FHIR resources to POST) and verify there is an EHR in EHRBASE (without an EHR, the requests will fail with an error status).


### Create Resource Requests (FHIR)

There are four requests used to create resources using the FHIR interface. When the FHIR-bridge receives those FHIR resources,
executes the mappings mentioned above and commits the correspondent openEHR COMPOSITIONs to EHRBASE. These requests are:

 * FHIR-BRIDGE POST Body Temp
 * FHIR-BRIDGE POST Condition
 * FHIR-BRIDGE POST SARS COV 2
 * FHIR-BRIDGE POST Lab Result (Observation)

All those are POST requests, and the successful response should have status code 201 Created.


### Search Resources Requests (FHIR)

There are four requests used to execute the FHIR search operation over the resources mapped in the "create resources" requests.
For the Observation search, the profile is a required parameter. For the Condition the profile is not used because we don't have 
a profile for it (mentioned above in the "mappings" section). There is another parameter required, that is mapped to the patient
identifier, that is the "identifier" parameter. These requests are:

 * FHIR-BRIDGE test search Body Temp
 * FHIR-BRIDGE test search Body Temp Alternative Impl
 * FHIR-BRIDGE test search Condition
 * FHIR-BRIDGE test search SARS COV 2
 * FHIR-BRIDGE test search Lab Result (Observation)
 
Note: the "FHIR-BRIDGE test search Body Temp Alternative Impl" request should return the same result as "FHIR-BRIDGE test search Body Temp",
but has a slightly different implementation to evaluate code complexity and performance. The "alternative" does a query for data points and uses RM
classes from Archie, while the "normal" one does a query for full compositions and uses the classes generated by the SDK generator.

All those are GET requests, and the successful response should have a status code 200 OK. The response payload is a FHIR Bundle resource.

For the Condition search we added a date range filter which allows to send a lower and upper boundary, just the lower, just
the upper or none and the search will filter accordingly. That uses the 'recorded-date' parameter (check the request in Insomnia). 

For the Observation search we added a date range filter using the 'date' parameter, also updated the Insomnia requests to show how that works.

### Get Resource by ID Requests (FHIR)

As a test we implemented a GET resource operation for the conditions, that uses the URL associated with each resource returned
by the search operation. The response for the GET operation should be an individual resource.

The GET request is:

 * FHIR-BRIDGE test get Condition

Right now we are mapping the COMPOSITION.uid to the FHIR resource ID, so the ID will look like this: d4090552-d85c-4828-9282-3dbabb9c4d43::local.ehrbase.org::1 

### Resource References
With the introduction of the HAPI FHIR JPA SERVER module, the FHIR-Bridge application is now verifying the references used in the resources sent for creation.

#### Local reference
```
{
  "result" : {
    "reference" : "Observation/123456"
  }
}
```
The use of a local reference in a submitted resource requires that the referenced resource exists in the database.

#### External reference
```
{
  "result" : {
    "reference" : "http://fhir.server.com/Observation/987654321"
  }
}
```
External references must be use for resources that do not exist on the FHIR-Bridge but have to be referenced in the submitted resource.

#### Logical identifier
```
{
  "subject" : {
    "reference" : "urn:uuid:f1a977bd-5090-413c-9719-3e709fd6b0ff"
  }
}
```
Regarding patient identification in submitted resources, the reference shall be a logical identifier as it is used to identify 
the patient in Ehrbase.

For additional information about resources references, please refer to FHIR specifications (https://www.hl7.org/fhir/references.html) 
and HAPI FHIR documentation (https://hapifhir.io/hapi-fhir/docs/model/references.html).

## Audit
FHIR-Bridge registers audit for the following operations:
* Create a new FHIR resource.
* Map a FHIR resource to Ehrbase.

The audit mechanism uses the AuditEvent resource provided by FHIR:
```
{
    "resourceType": "AuditEvent",
    "id": "1204",
    "meta": {
        "versionId": "1",
        "lastUpdated": "2020-10-06T10:44:38.630+02:00"
    },
    "type": {
        "system": "http://terminology.hl7.org/CodeSystem/iso-21089-lifecycle",
        "code": "transform",
        "display": "Transform/Translate Record Lifecycle Event"
    },
    "action": "E",
    "recorded": "2020-10-06T10:44:38.629+02:00",
    "outcome": "8",
    "outcomeDesc": "One contained Observation was expected 0 were received in DiagnosticReport DiagnosticReport/1202/_history/1",
    "agent": [
        {
            "role": [
                {
                    "coding": [
                        {
                            "system": "http://terminology.hl7.org/CodeSystem/extra-security-role-type",
                            "code": "dataprocessor",
                            "display": "data processor"
                        }
                    ]
                }
            ],
            "who": {
                "identifier": {
                    "value": "fhir-bridge - 1.0.0-SNAPSHOT"
                }
            },
            "requestor": true,
            "network": {
                "address": "192.168.10.161",
                "type": "2"
            }
        }
    ],
    "entity": [
        {
            "what": {
                "reference": "DiagnosticReport/1202"
            },
            "type": {
                "system": "http://hl7.org/fhir/resource-types",
                "code": "DiagnosticReport",
                "display": "DiagnosticReport"
            }
        }
    ]
}
```
In addition, the AuditEvent endpoint is available to search for resources using the following supported criteria:

| Name    | Type      | Description                               |
| ------- | --------- | ----------------------------------------- |
| action  | token     | Type of action performed during the event |
| date    | date      | Time when the event was recorded          |
| entity  | reference | Specific instance of resource             |
| outcome | token     | Whether the event succeeded or failed     |
| type    | token     | Type/identifier of event                  |

Examples:
```
# All failed operations
POST {base_url}/fhir/AuditEvent/_search?outcome=8

# All transform operations
POST {base_url}/fhir/AuditEvent/_search?type=transform
```