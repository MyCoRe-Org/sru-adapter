package org.mycore.sru;

import junit.framework.TestCase;
import org.junit.Test;

public class SRUConnectorTest extends TestCase {

    @Test
    public void testSetInferfaceURL() throws Exception {
        String url = "https://services.dnb.de/sru";
        SRUConnector connector = new SRUConnector(url);
        assertEquals(url, connector.getSRUInterfaceUrl().toString());
    }

    @Test
    public void testDNBConnection() throws Exception {
        SRUConnector connector = new SRUConnector("https://services.dnb.de/sru");

        connector.setCatalog(SRUConnector.Catalog.dnb);
        connector.setVersion(SRUConnector.VERSION_1_1);
        connector.setOperation(SRUConnector.Operation.searchRetrieve);
        connector.setQuery("WOE%3DGoethe");
        connector.setRecordSchema(SRUConnector.RecordSchema.MARC21);
        connector.setStartRecord(1);
        connector.setMaximumRecords(1);
        assertNotNull(connector.getDocument());
    }
}
