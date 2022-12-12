/**
 *
 */
package org.mycore.sru;

import java.net.MalformedURLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mycore.sru.SRUConnector.RecordSchema;

/**
 * @author shermann
 */
public class SRUConnectorFactory {
    /** Constant for GBV 
     * @deprecated
     * */
    @Deprecated
    public static final int GBV_SRU_CONNECTION_DB_21 = 1;

    /** Constant for GBV */
    public static final int GBV_SRU_STANDARD_CONNECTION = 4;

    /** Constant for GBV (IKAR)
     *  @deprecated use {@link SRUConnectorFactory#K10PLUS_IKAR_SRU_STANDARD_CONNECTION}
     * */
    @Deprecated
    public static final int GBV_SRU_STANDARD_IKAR_CONNECTION = 41;

    /** Constant for IKAR*/
    public static final int K10PLUS_IKAR_SRU_STANDARD_CONNECTION = 81;

    /** Constant for Kalliope */
    public static final int KALLIOPE_SRU_STANDARD_CONNECTION = 7;

    /** k10plus */
    public static final int K10PLUS_SRU_STANDARD_CONNECTION = 8;

    private static final Logger LOGGER = LogManager.getLogger(SRUConnectorFactory.class);

    /**
     * @param type
     *            defines which SRUConnector should be returned, currently only
     *            a SRUConnector for GVK is supported
     * @param query
     *            the searchString for the connector e.g. &quot;ppn
     *            1234567890&quot; or any other valid query
     *
     * @return the SRUConnector, please note that
     *         {@link SRUConnector#getDocument()} will return a document with at
     *         most one result hit inside
     */
    public static SRUConnector getSRUConnector(int type, String query) {
        SRUConnector toReturn = null;
        String url = null;

        switch (type) {
        /* gbv Verbundkatalog */
        case GBV_SRU_STANDARD_CONNECTION:
            try {
                url = "http://sru.gbv.de/gbvcat";
                toReturn = getSRUConnector(url, query);
            } catch (MalformedURLException urlEx) {
                LOGGER.error(urlEx);
            }
            break;
        case K10PLUS_SRU_STANDARD_CONNECTION:
            try {
                url = "https://sru.k10plus.de/gbvcat!rec=*";
                toReturn = getSRUConnector(url, query);
            } catch (MalformedURLException urlEx) {
                LOGGER.error(urlEx);
            }
            break;
        /* new ikar sru interface */
        case GBV_SRU_STANDARD_IKAR_CONNECTION:
        case K10PLUS_IKAR_SRU_STANDARD_CONNECTION:
            try {
                url = "https://sru.k10plus.de/ikar";
                toReturn = getSRUConnector(url, query);
            } catch (MalformedURLException urlEx) {
                LOGGER.error(urlEx);
            }
            break;
        /* gbv Verbundkatalog */
        case GBV_SRU_CONNECTION_DB_21:
            try {
                url = "http://gso.gbv.de/sru/DB=2.1/";
                toReturn = getSRUConnector(url, query);
                toReturn.setRecordSchema(RecordSchema.PICA);
            } catch (MalformedURLException urlEx) {
                LOGGER.error(urlEx);
            }
            break;

        case KALLIOPE_SRU_STANDARD_CONNECTION:
            try {
                LOGGER.info(
                    "Creating SRUConnector for Kalliope. You may need special access rights in order to retrieve records through this SRU interface");
                url = "https://kalliope-verbund.info/sru";
                toReturn = getSRUConnector(url, query);
                toReturn.setRecordSchema(RecordSchema.MODS);
            } catch (MalformedURLException urlEx) {
                LOGGER.error(urlEx);
            }
            break;
        default:
            LOGGER.warn("Could not create SRUConnector for type {}", type);
            return null;
        }

        return toReturn;
    }

    /**
     * @param url
     *            the gvk database
     * @param query
     *            the query to execute, the query is expected to be parsed
     *            already
     * @return a preconfigured {@link SRUConnector} where maximum records and
     *         startRecord are set to 1, recordSchema is {@link RecordSchema#PICA_XML}
     * @throws MalformedURLException
     */
    public static SRUConnector getSRUConnector(String url, String query) throws MalformedURLException {
        SRUConnector sru = new SRUConnector(url);
        sru.setVersion(SRUConnector.VERSION_1_1);
        sru.setOperation(SRUConnector.Operation.searchRetrieve);
        sru.setRecordSchema(SRUConnector.RecordSchema.PICA_XML);
        sru.setRecordPacking(SRUConnector.RecordPacking.XML);
        sru.setMaximumRecords(1);
        sru.setStartRecord(1);
        sru.setQuery(query);
        return sru;
    }

    /**
     * @param database
     * @return
     */
    public static int typeByDatabase(String database) {
        switch (database) {
        case "2.1":
            return SRUConnectorFactory.GBV_SRU_CONNECTION_DB_21;
        case "ikar":
            return SRUConnectorFactory.K10PLUS_IKAR_SRU_STANDARD_CONNECTION;
        case "kalliope":
            return SRUConnectorFactory.KALLIOPE_SRU_STANDARD_CONNECTION;
        case "k10plus":
            return SRUConnectorFactory.K10PLUS_SRU_STANDARD_CONNECTION;
        default:
            LOGGER.info("Will return type for kalliope sru connection");
            return SRUConnectorFactory.KALLIOPE_SRU_STANDARD_CONNECTION;
        }
    }
}
