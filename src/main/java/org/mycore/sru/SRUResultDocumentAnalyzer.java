/**
 *
 */
package org.mycore.sru;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

/**
 * @author shermann
 */
public class SRUResultDocumentAnalyzer {

    private static final XPathFactory XPATH_FACTORY = XPathFactory.instance();

    static final Namespace ZS_NAMESPACE = Namespace.getNamespace("zs", "http://www.loc.gov/zing/srw/");

    static final Namespace DIAG_NAMESPACE = Namespace.getNamespace("diag", "http://www.loc.gov/zing/srw/diagnostic/");

    private static final Logger LOGGER = LogManager.getLogger(SRUResultDocumentAnalyzer.class);

    private Document source;

    private int hits = -1;

    /**
     * @param source
     *            the source to examine
     */
    public SRUResultDocumentAnalyzer(Document source) {
        this.source = source;
    }

    /**
     * @return the message from remote
     */
    public String getDiagnostics() {
        if (source == null) {
            return null;
        }
        XPathExpression<Element> xp = XPathFactory.instance().compile(
            "zs:searchRetrieveResponse/zs:diagnostics/diag:diagnostic/diag:message", Filters.element(), null,
            DIAG_NAMESPACE, ZS_NAMESPACE);
        Element e = xp.evaluateFirst(source);
        return e != null ? e.getText() : null;
    }

    /**
     * @return the source used for this analyzer
     */
    public Document getSource() {
        return this.source;
    }

    public String getResultSetId() {
        String resultSetId = "n/a";
        try {
            String xPathDef = "zs:searchRetrieveResponse/zs:resultSetId";
            XPathExpression<Element> xpath = getXPathExpression(xPathDef);
            Element resultElement = xpath.evaluateFirst(this.source);
            if (resultElement == null) {
                LOGGER.warn("Could not find element via XPATH: " + xpath.getExpression());
            } else {
                resultSetId = resultElement.getText();
            }
        } catch (Exception e) {
            LOGGER.warn("Error getting result set id", e);
        }
        return resultSetId;
    }

    /**
     * Returns the number of hits.
     *
     * @return number of hits
     */
    public int getHits() {
        if (hits > 0) {
            return hits;
        }
        int val = 0;
        try {
            XPathExpression<Element> xpath = getXPathExpression("zs:searchRetrieveResponse/zs:numberOfRecords");
            Element resultElement = xpath.evaluateFirst(this.source);
            if (resultElement != null) {
                val = Integer.parseInt(resultElement.getText());
            }
        } catch (Exception e) {
            LOGGER.warn("An error occurred while getting the number of hits from the document provided", e);
        }
        this.hits = val;
        return hits;
    }

    private static XPathExpression<Element> getXPathExpression(String xPathDef) {
        return XPATH_FACTORY.compile(xPathDef, Filters.element(), null, ZS_NAMESPACE);
    }

}
