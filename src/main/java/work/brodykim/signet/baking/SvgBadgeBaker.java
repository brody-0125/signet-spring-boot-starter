package work.brodykim.signet.baking;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * SVG badge baking using JDK {@code javax.xml}.
 *
 * <p>Embeds credential data in an {@code <openbadges:credential>} element
 * in the {@code https://purl.imsglobal.org/ob/v3p0} namespace.
 */
public class SvgBadgeBaker implements BadgeBaker {

    static final String OB3_NAMESPACE = "https://purl.imsglobal.org/ob/v3p0";
    private static final String XMLNS_NS = "http://www.w3.org/2000/xmlns/";

    private final BadgeBakingProperties properties;

    public SvgBadgeBaker(BadgeBakingProperties properties) {
        this.properties = properties;
    }

    @Override
    public ImageFormat supportedFormat() {
        return ImageFormat.SVG;
    }

    @Override
    public byte[] bake(byte[] imageData, String credentialData) throws BadgeBakingException {
        validateSize(imageData);
        Document doc = parseSvg(imageData);
        Element svgRoot = doc.getDocumentElement();

        if (!"svg".equals(svgRoot.getLocalName())) {
            throw new BadgeBakingException("Root element is not <svg>");
        }

        // Check for existing credential element
        NodeList existing = svgRoot.getElementsByTagNameNS(OB3_NAMESPACE, "credential");
        if (existing.getLength() > 0) {
            if (!properties.isOverwriteExisting()) {
                throw new BadgeBakingException(
                        "SVG already contains baked credential data. "
                                + "Set openbadges.baking.overwrite-existing=true to allow overwriting.");
            }
            for (int i = existing.getLength() - 1; i >= 0; i--) {
                existing.item(i).getParentNode().removeChild(existing.item(i));
            }
        }

        // Add namespace declaration
        svgRoot.setAttributeNS(XMLNS_NS, "xmlns:openbadges", OB3_NAMESPACE);

        // Create credential element
        Element credentialElem = doc.createElementNS(OB3_NAMESPACE, "openbadges:credential");

        // Determine proof format and set content accordingly
        CredentialFormat format = CredentialFormat.detect(credentialData);
        if (format == CredentialFormat.JWS) {
            credentialElem.setAttribute("verify", credentialData);
        } else {
            CDATASection cdata = doc.createCDATASection("\n" + credentialData.trim() + "\n");
            credentialElem.appendChild(cdata);
        }

        // Insert as first child of SVG root
        svgRoot.insertBefore(credentialElem, svgRoot.getFirstChild());

        return serializeSvg(doc);
    }

    @Override
    public String extract(byte[] imageData) throws BadgeBakingException {
        Document doc = parseSvg(imageData);
        NodeList nodes = doc.getElementsByTagNameNS(OB3_NAMESPACE, "credential");
        if (nodes.getLength() == 0) {
            return null;
        }
        Element credentialElem = (Element) nodes.item(0);

        // Check for JWS in verify attribute
        String verify = credentialElem.getAttribute("verify");
        if (verify != null && !verify.isEmpty()) {
            return verify;
        }

        // Otherwise, text content is the JSON-LD credential
        String textContent = credentialElem.getTextContent();
        return textContent != null ? textContent.trim() : null;
    }

    private Document parseSvg(byte[] data) throws BadgeBakingException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            // XXE prevention
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            return factory.newDocumentBuilder().parse(new ByteArrayInputStream(data));
        } catch (Exception e) {
            throw new BadgeBakingException("Failed to parse SVG document", e);
        }
    }

    private byte[] serializeSvg(Document doc) throws BadgeBakingException {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "no");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            transformer.transform(new DOMSource(doc), new StreamResult(baos));
            return baos.toByteArray();
        } catch (Exception e) {
            throw new BadgeBakingException("Failed to serialize SVG document", e);
        }
    }

    private void validateSize(byte[] data) throws BadgeBakingException {
        if (data.length > properties.getMaxImageSizeBytes()) {
            throw new BadgeBakingException(
                    "SVG exceeds maximum size of " + properties.getMaxImageSizeBytes() + " bytes");
        }
    }
}
