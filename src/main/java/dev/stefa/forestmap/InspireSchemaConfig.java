package dev.stefa.forestmap;

import jakarta.annotation.PostConstruct;
import org.geotools.util.factory.Hints;
import org.springframework.beans.factory.annotation.Value;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import java.io.File;
import java.nio.file.Path;

/**
 * Redirects INSPIRE schema imports to a local mirror so the GML parser never
 * reaches out to inspire.ec.europa.eu at parse time (that network round-trip,
 * and its frequent failure, is exactly what breaks the WFSDataStore route).
 *
 * <p>The resolver is registered as the GeoTools <em>system-default</em> hint,
 * which every GeoTools XML parser (PullParser included) consults. That is the
 * version-robust way to inject it without depending on a per-parser setter.
 */
//@Configuration
public class InspireSchemaConfig {

    private final Path schemaRoot;

    public InspireSchemaConfig(@Value("${cadastre.inspire.schema-dir}") String schemaDir) {
        this.schemaRoot = Path.of(schemaDir);
    }

    @PostConstruct
    void registerEntityResolver() {
        Hints.putSystemDefault(Hints.ENTITY_RESOLVER, new LocalInspireEntityResolver(schemaRoot));
    }

    /**
     * Serves any schema request whose URL contains {@code inspire.ec.europa.eu/schemas/}
     * from the local mirror, mapping the path after {@code /schemas/} onto the cache
     * directory. Returning {@code null} for anything else lets GeoTools fall back to its
     * default handling (the OGC/GML schemas ship inside the gt-xsd-* jars already).
     */
    static final class LocalInspireEntityResolver implements EntityResolver {

        private static final String INSPIRE_MARKER = "inspire.ec.europa.eu/schemas/";

        private final Path root;

        LocalInspireEntityResolver(Path root) {
            this.root = root;
        }

        @Override
        public InputSource resolveEntity(String publicId, String systemId) {
            if (systemId == null) {
                return null;
            }
            int idx = systemId.indexOf(INSPIRE_MARKER);
            if (idx < 0) {
                return null; // not an INSPIRE schema -> default behavior
            }
            String relative = systemId.substring(idx + INSPIRE_MARKER.length());
            File local = root.resolve(relative).toFile();
            if (local.isFile()) {
                InputSource source = new InputSource(local.toURI().toString());
                source.setPublicId(publicId);
                return source;
            }
            return null; // not cached locally -> let GeoTools try its default
        }
    }
}
