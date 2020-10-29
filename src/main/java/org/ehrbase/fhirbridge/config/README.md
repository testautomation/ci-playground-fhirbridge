# Config package

## EHRbase

To use the client library / SDK to let us interact with the running EHRbase server instance a `DefaultRestClient` object needs to configured.

It points to the EHRbase server with the properties set (address, port and REST path), see `EhrbaseClientConfig`.

Additionally, some template provider is necessary. Here the `DataTemplateProvider` is implemented to use a fixed set of openEHR templates located in the resource folder. 