# SRUAdapter

This maven module provides methods to set parameters
needed to communicate with an SRU interface. See the following example to get
an idea of how to use this connector or visit http://www.dnb.de/service/zd/sru.htm.

### Example

```
SRUConnector sru = new SRUConnector("http://services.d-nb.de/sru/");
sru.setCatalog(SRUConnector.CATALOG_PND);
sru.setVersion(SRUConnector.VERSION_1_1);
sru.setOperation(SRUConnector.OPERATION_SEARCH_RETRIEVE);
sru.setQuery(SRUConnector.IDN_EQUALS + "118555650");
sru.setRecordSchema(SRUConnector.RECORD_SCHEMA_MARC21_XML);
```

Invoking
```SRUConnector#getQueryURL()```
leads to the following url
```http://services.d-nb.de/sru/pnd?version=1.1&operation=searchRetrieve&query=IDN%3D118555650&recordSchema=MARC21-xml```
