/**
 *
 */
package org.mycore.sru;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class represents a SRU interface. It provides methods to set parameters
 * needed to communicate with a SRU interface. See the following example to get
 * an idea of how to use this connector or visit <a
 * href="http://www.dnb.de/service/zd/sru.htm"
 * >http://www.dnb.de/service/zd/sru.htm</a><BR/>
 * <BR/>
 *
 * <code>
 SRUConnector sru = new SRUConnector("https://services.dnb.de/sru");<br/>
 sru.setCatalog(SRUConnector.CATALOG_DNB);<br/>
 sru.setVersion(SRUConnector.VERSION_1_1);<br/>
 sru.setOperation(SRUConnector.OPERATION_SEARCH_RETRIEVE);<br/>
 sru.setQuery("WOE%3DGoethe");<br/>
 sru.setRecordSchema(SRUConnector.RECORD_SCHEMA_MARC21_XML);<br/><br/>
 </code> Invoking <code>{@link SRUConnector#getQueryURL()}</code> leads to
 * the following url:
 * <code>https://services.dnb.de/sru/dnb?version=1.1&operation=searchRetrieve&query=WOE%3DGoethe&recordSchema=MARC21-xml</code>
 *
 * @author shermann
 *
 */
public class SRUConnector {
    public enum Catalog {
        dnb, pnd, gkd
    }

    public enum RecordPacking {
        XML;

        public String toString() {
            switch (this) {
            case XML:
                return "xml";
            default:
                return super.toString();
            }
        }
    }

    public enum RecordSchema {
        MABXML, MARC_XML, MARC21, DUBLIN_CORE, PICA, PICA_XML, MODS, MODS37;

        public String toString() {
            switch (this) {
            case MABXML:
                return "MABxml-1";
            case MARC_XML:
                return "marcxml";
            case MARC21:
                return "MARC21-xml";
            case DUBLIN_CORE:
                return "oai_dc";
            case PICA:
                return "pica";
            case PICA_XML:
                return "picaxml";
            case MODS:
                return "mods";
            case MODS37:
                return "mods37";
            default:
                return super.toString();
            }
        }
    }

    public enum Operation {
        searchRetrieve
    }

    public enum SearchField {
        idn, knr
    }

    private static final Logger LOGGER = LogManager.getLogger(SRUConnector.class);

    public final static String VERSION_1_1 = "1.1";

    public final static String VERSION_1_2 = "1.2";

    private URL url;

    private Catalog catalog;

    private String version;

    private String query;

    private Operation operation;

    private RecordSchema recordSchema;

    private RecordPacking recordPacking;

    private int maximumRecords, startRecord;

    /**
     * @param url
     *            the url of the sru interface, should terminate with a backslash
     */
    public SRUConnector(URL url) throws MalformedURLException {
        if (url != null) {
            this.url = url;
        } else {
            throw new MalformedURLException("Passing null as argument is not allowed. ");
        }
        maximumRecords = -1;
        startRecord = -1;
    }

    /**
     * @param url
     *            the url of the sru interface, should terminate with a backslash
     */
    public SRUConnector(String url) throws MalformedURLException {
        this(new URL(url));
    }

    /**
     * @return the url of the sru interface
     */
    public URL getSRUInterfaceUrl() {
        return url;
    }

    /**
     * @param url
     *            the sru url to set
     */
    public void setSRUInterfaceUrl(URL url) {
        this.url = url;
    }

    /**
     * @return the catalog
     */
    public Catalog getCatalog() {
        return catalog;
    }

    /**
     * @param catalog
     *            the catalog to set
     */
    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version
     *            the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the query
     */
    public String getQuery() {
        return query;
    }

    /**
     * @param query
     *            the query to set
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * @return the operation
     */
    public Operation getOperation() {
        return operation;
    }

    /**
     * @param searchretrieve
     *            the operation to set
     */
    public void setOperation(Operation searchretrieve) {
        this.operation = searchretrieve;
    }

    /**
     * @return the recordSchema
     */
    public RecordSchema getRecordSchema() {
        return recordSchema;
    }

    /**
     * @param recordSchema
     *            the recordSchema to set
     */
    public void setRecordSchema(RecordSchema recordSchema) {
        this.recordSchema = recordSchema;
    }

    /**
     * @return true if the current state of the object is valid. This is the
     *         case when the following fields are not null: catalog, operation,
     *         query, recordSchema and version
     */
    public boolean isValidConfig() {
        if (operation == null || query == null || recordSchema == null || version == null) {
            return false;
        }
        return true;
    }

    /**
     * @return the url invoked when connecting to the specified sru interface or
     *         null if the configuration is not valid
     * @throws MalformedURLException if {@link #setSRUInterfaceUrl(URL)} was called with illegal argument
     * @see SRUConnector#isValidConfig()
     */
    public URL getQueryURL() throws MalformedURLException {
        if (!isValidConfig()) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(url.toString());
        /* catalog is not needed in every query*/
        if (catalog != null) {
            builder.append(catalog + "?");
        } else {
            builder.append("?");
        }

        builder.append("query=" + query);
        builder.append("&version=" + version);
        builder.append("&operation=" + operation);
        builder.append("&recordSchema=" + recordSchema);

        if (recordPacking != null) {
            builder.append("&recordPacking=" + recordPacking);
        }

        if (maximumRecords > 0) {
            builder.append("&maximumRecords=" + maximumRecords);
        }

        if (startRecord > 0) {
            builder.append("&startRecord=" + startRecord);
        }
        return new URL(builder.toString());
    }

    /**
     * Invokes the query url and delivers the document retrieved through the sru
     * interface.
     *
     * @return the document as the result of the query, may be null
     * @throws ConnectException
     * */
    public Document getDocument() throws ConnectException {
        if (!isValidConfig()) {
            return null;
        }
        Document document = null;

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            CloseableHttpResponse response = httpClient.execute(new HttpGet(getQueryURL().toExternalForm()))) {
            LOGGER.info("SRU: connecting to {}", getQueryURL());
            SAXBuilder builder = new SAXBuilder();
            document = builder.build(response.getEntity().getContent());
        } catch (ConnectException e) {
            LOGGER.error(e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occured in {}", getClass().getName(), e);
        }
        return document;
    }

    /** Sets the internal record packing for the query*/
    public void setRecordPacking(RecordPacking p) {
        this.recordPacking = p;
    }

    /** Returns the record packing to be used when executing the query */
    public RecordPacking getRecordPacking() {
        return this.recordPacking;
    }

    public static String getIDQuery(Catalog catalog, String id) {
        switch (catalog) {
        case gkd:
            return SearchField.knr + "%3D" + id;
        default:
            return SearchField.idn + "%3D" + id;
        }
    }

    /**
     * @return the amount of records to be returned
     */
    public int getMaximumRecords() {
        return maximumRecords;
    }

    /**
     * @param maximumRecords the amount of record to be returned
     */
    public void setMaximumRecords(int maximumRecords) {
        this.maximumRecords = maximumRecords;
    }

    /**
     * @return the startRecord
     */
    public int getStartRecord() {
        return startRecord;
    }

    /**
     * @param startRecord the startRecord to set
     */
    public void setStartRecord(int startRecord) {
        this.startRecord = startRecord;
    }

    @Override
    public String toString() {
        return this.url.toString();
    }
}
