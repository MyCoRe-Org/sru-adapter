package org.mycore.sru;

import junit.framework.TestCase;
import org.junit.Test;

public class SRUConnectorFactoryTest extends TestCase {

    @Test
    public void testKalliope() throws Exception {
        SRUConnector c = SRUConnectorFactory.getSRUConnector(
            SRUConnectorFactory.KALLIOPE_SRU_STANDARD_CONNECTION, "ead.id%3DDE-611-HS-3495752");
        assertEquals(1, new SRUResultDocumentAnalyzer(c.getDocument()).getHits());
    }

    @Test
    public void testK10Plus() throws Exception {
        SRUConnector c = SRUConnectorFactory.getSRUConnector(
            SRUConnectorFactory.K10PLUS_SRU_STANDARD_CONNECTION, "pica.ppn%3D630462143");
        assertEquals(1, new SRUResultDocumentAnalyzer(c.getDocument()).getHits());
    }

    @Test
    public void testIkar() throws Exception {
        SRUConnector c = SRUConnectorFactory.getSRUConnector(
            SRUConnectorFactory.K10PLUS_IKAR_SRU_STANDARD_CONNECTION, "pica.ppn%3D001326368");
        assertEquals(1, new SRUResultDocumentAnalyzer(c.getDocument()).getHits());
    }
}
