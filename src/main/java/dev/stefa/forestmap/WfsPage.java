package dev.stefa.forestmap;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.OptionalInt;

@NullMarked
public record WfsPage(OptionalInt numberMatched, int numberReturned, byte[] raw) {

  private static final XMLInputFactory FACTORY = newFactory();

  private static XMLInputFactory newFactory() {
    XMLInputFactory f = XMLInputFactory.newInstance();
    f.setProperty(XMLInputFactory.SUPPORT_DTD, false);
    f.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    return f;
  }

  /// Fresh stream over the buffered body; safe to call repeatedly.
  public InputStream body() {
    return new ByteArrayInputStream(raw);
  }

  public boolean isEmpty() {
    return numberReturned == 0;
  }

  public boolean isTruncated(int requestedCount) {
    return numberReturned >= requestedCount - 1;   // AdE off-by-one
  }

  public static WfsPage of(byte[] raw) throws XMLStreamException {
    XMLStreamReader r = FACTORY.createXMLStreamReader(new ByteArrayInputStream(raw));
    try {
      while (r.hasNext()) {
        if (r.next() != XMLStreamConstants.START_ELEMENT) continue;
        if ("ServiceExceptionReport".equals(r.getLocalName())) {
          throw new XMLStreamException("WFS returned a ServiceExceptionReport");
        }
        return new WfsPage(
          parseCount(r.getAttributeValue(null, "numberMatched")),
          parseCount(r.getAttributeValue(null, "numberReturned")).orElse(0),
          raw
        );
      }
      throw new XMLStreamException("No root element in WFS response");
    } finally {
      r.close();
    }
  }

  /// numberMatched is literally "unknown" whenever COUNT is supplied.
  private static OptionalInt parseCount(@Nullable String v) {
    if (v == null || "unknown".equals(v)) return OptionalInt.empty();
    try {
      return OptionalInt.of(Integer.parseInt(v.trim()));
    } catch (NumberFormatException e) {
      return OptionalInt.empty();
    }
  }
}
