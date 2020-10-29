# Copyright (c) 2020 P. Wohlfarth (Appsfactory), Wladislaw Wagner (Vitasystems GmbH), Dave Petzold (Appsfactory GmbH)
#
# This file is part of Project EHRbase
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.



*** Settings ***
# Library                 REST
# Library                 Collections
# Library                 JSONLibrary
Resource                ${EXECDIR}/robot/_resources/suite_settings.robot

Test Setup              establish preconditions

Force Tags              search



*** Variables ***




*** Test Cases ***
001 Search Body Temperature
    [Documentation]    Search Body Temperature

	observation.get body temperature


002 Search Observation Lab
    [Documentation]    Search Observation Lab

	observation.get observation lab


003 Search Coronavirus Lab Results
    [Documentation]    Search Coronavirus Lab Results

	observation.get coronavirus lab results


*** Keywords ***
establish preconditions
    generic.prepare new request session    Prefer=return=representation
    ehr.create new ehr    000_ehr_status.json
    observation.create body temperature    observation-bodytemp-example.json
    observation.create observation lab    observation-observationlab-example.json
    observation.create coronavirus lab result    observation-coronavirusnachweistest-example.json
    
