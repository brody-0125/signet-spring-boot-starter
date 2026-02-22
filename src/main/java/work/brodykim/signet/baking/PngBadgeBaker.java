package work.brodykim.signet.baking;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * PNG badge baking using JDK {@code javax.imageio}.
 *
 * <p>Embeds credential data in an iTXt chunk with keyword {@code openbadgecredential}
 * as specified by Open Badges 3.0.
 */
public class PngBadgeBaker implements BadgeBaker {

    static final String KEYWORD = "openbadgecredential";
    private static final String PNG_NATIVE_FORMAT = "javax_imageio_png_1.0";
    private static final byte[] PNG_MAGIC = {
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
    };

    private final BadgeBakingProperties properties;

    public PngBadgeBaker(BadgeBakingProperties properties) {
        this.properties = properties;
    }

    @Override
    public ImageFormat supportedFormat() {
        return ImageFormat.PNG;
    }

    @Override
    public byte[] bake(byte[] imageData, String credentialData) throws BadgeBakingException {
        validatePng(imageData);
        validateSize(imageData);

        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            ImageReader reader = ImageIO.getImageReadersByFormatName("png").next();
            reader.setInput(ImageIO.createImageInputStream(bais));
            BufferedImage image = reader.read(0);
            IIOMetadata metadata = reader.getImageMetadata(0);

            validateDimensions(image);

            if (containsCredential(metadata) && !properties.isOverwriteExisting()) {
                throw new BadgeBakingException(
                        "Image already contains baked credential data. "
                                + "Set openbadges.baking.overwrite-existing=true to allow overwriting.");
            }

            IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(PNG_NATIVE_FORMAT);
            removeExistingCredentialChunks(root);

            IIOMetadataNode iTXt = new IIOMetadataNode("iTXt");
            IIOMetadataNode entry = new IIOMetadataNode("iTXtEntry");
            entry.setAttribute("keyword", KEYWORD);
            entry.setAttribute("compressionFlag", "FALSE");
            entry.setAttribute("compressionMethod", "0");
            entry.setAttribute("languageTag", "");
            entry.setAttribute("translatedKeyword", "");
            entry.setAttribute("text", credentialData);
            iTXt.appendChild(entry);
            root.appendChild(iTXt);

            metadata.setFromTree(PNG_NATIVE_FORMAT, root);

            ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();
            ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
            writer.setOutput(ios);
            writer.write(new IIOImage(image, null, metadata));
            ios.flush();
            writer.dispose();
            reader.dispose();

            return baos.toByteArray();

        } catch (BadgeBakingException e) {
            throw e;
        } catch (Exception e) {
            throw new BadgeBakingException("Failed to bake PNG image", e);
        }
    }

    @Override
    public String extract(byte[] imageData) throws BadgeBakingException {
        validatePng(imageData);

        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageData)) {
            ImageReader reader = ImageIO.getImageReadersByFormatName("png").next();
            reader.setInput(ImageIO.createImageInputStream(bais));
            IIOMetadata metadata = reader.getImageMetadata(0);
            reader.dispose();

            Node root = metadata.getAsTree(PNG_NATIVE_FORMAT);
            NodeList children = root.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if ("iTXt".equals(child.getNodeName())) {
                    NodeList entries = child.getChildNodes();
                    for (int j = 0; j < entries.getLength(); j++) {
                        Node entryNode = entries.item(j);
                        NamedNodeMap attrs = entryNode.getAttributes();
                        if (attrs != null) {
                            Node keywordAttr = attrs.getNamedItem("keyword");
                            if (keywordAttr != null
                                    && KEYWORD.equals(keywordAttr.getNodeValue())) {
                                Node textAttr = attrs.getNamedItem("text");
                                return textAttr != null ? textAttr.getNodeValue() : null;
                            }
                        }
                    }
                }
            }
            return null;

        } catch (Exception e) {
            throw new BadgeBakingException("Failed to extract credential from PNG", e);
        }
    }

    private void validatePng(byte[] data) throws BadgeBakingException {
        if (data == null || data.length < PNG_MAGIC.length) {
            throw new BadgeBakingException("Data is not a valid PNG (too small)");
        }
        for (int i = 0; i < PNG_MAGIC.length; i++) {
            if (data[i] != PNG_MAGIC[i]) {
                throw new BadgeBakingException("Data is not a valid PNG (bad magic bytes)");
            }
        }
    }

    private void validateSize(byte[] data) throws BadgeBakingException {
        if (data.length > properties.getMaxImageSizeBytes()) {
            throw new BadgeBakingException(
                    "Image exceeds maximum size of " + properties.getMaxImageSizeBytes() + " bytes");
        }
    }

    private void validateDimensions(BufferedImage image) throws BadgeBakingException {
        int w = image.getWidth();
        int h = image.getHeight();
        int min = properties.getMinDimension();
        int max = properties.getMaxDimension();
        if (w < min || h < min) {
            throw new BadgeBakingException(
                    "Image dimensions " + w + "x" + h + " below minimum " + min + "x" + min);
        }
        if (w > max || h > max) {
            throw new BadgeBakingException(
                    "Image dimensions " + w + "x" + h + " exceed maximum " + max + "x" + max);
        }
    }

    private boolean containsCredential(IIOMetadata metadata) {
        Node root = metadata.getAsTree(PNG_NATIVE_FORMAT);
        NodeList children = root.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if ("iTXt".equals(child.getNodeName())) {
                NodeList entries = child.getChildNodes();
                for (int j = 0; j < entries.getLength(); j++) {
                    NamedNodeMap attrs = entries.item(j).getAttributes();
                    if (attrs != null) {
                        Node kw = attrs.getNamedItem("keyword");
                        if (kw != null && KEYWORD.equals(kw.getNodeValue())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void removeExistingCredentialChunks(IIOMetadataNode root) {
        NodeList children = root.getChildNodes();
        for (int i = children.getLength() - 1; i >= 0; i--) {
            Node child = children.item(i);
            if ("iTXt".equals(child.getNodeName())) {
                NodeList entries = child.getChildNodes();
                for (int j = entries.getLength() - 1; j >= 0; j--) {
                    NamedNodeMap attrs = entries.item(j).getAttributes();
                    if (attrs != null) {
                        Node kw = attrs.getNamedItem("keyword");
                        if (kw != null && KEYWORD.equals(kw.getNodeValue())) {
                            child.removeChild(entries.item(j));
                        }
                    }
                }
                if (child.getChildNodes().getLength() == 0) {
                    root.removeChild(child);
                }
            }
        }
    }
}
