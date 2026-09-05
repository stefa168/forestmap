package dev.stefa.forestmap.assets.image.health;

import app.photofox.vipsffm.Vips;
import app.photofox.vipsffm.VipsHelper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;

@Slf4j
@NullMarked
@Component
public class LibvipsHealthCheck {
  // Bump if we start using libvips features that require a newer version
  static final int MIN_MAJOR = 8;
  static final int MIN_MINOR = 15;

  @Getter
  private boolean supportsHeif;

  @PostConstruct
  void verifyPresence() {
    final String version;
    try {
      version = VipsHelper.version_string();
    } catch (UnsatisfiedLinkError | NoClassDefFoundError | ExceptionInInitializerError e) {
      throw new LibvipsUnavailableException(
          """
              libvips native library could not be loaded. \
              Ensure libvips is installed and on the library path \
              (e.g. 'apt install libvips' / 'brew install vips').
              """, e);
    }

    if (version == null || version.isBlank()) {
      throw new LibvipsUnavailableException(
          "libvips loaded but returned an empty version string; installation may be corrupt.");
    }

    assertMinimumVersion(version);
    log.info("libvips available: {}", version);

    Vips.run(arena -> {
      boolean canReadHeif  = VipsHelper.type_find(arena, "VipsOperation", "heifload") != 0;
      boolean canWriteHeif = VipsHelper.type_find(arena, "VipsOperation", "heifsave") != 0;

//      if(!canReadHeif || !canWriteHeif)
//        throw new LibvipsUnavailableException("Bundled vips version doesn't support heif read (%b) or write (%b)".formatted(canReadHeif, canWriteHeif));
      supportsHeif = canReadHeif && canWriteHeif;
    });
  }

  private static void assertMinimumVersion(String version) {
    try {
      var parts = version.split("\\.");
      int major = Integer.parseInt(parts[0]);
      int minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
      if (major < MIN_MAJOR || (major == MIN_MAJOR && minor < MIN_MINOR)) {
        throw new LibvipsUnavailableException(
            "libvips %s is too old; %d.%d or newer is required."
                .formatted(version, MIN_MAJOR, MIN_MINOR));
      }
    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
      log.warn("Could not parse libvips version '{}', skipping minimum-version check", version);
    }
  }

  public static class LibvipsUnavailableException extends IllegalStateException {
    public LibvipsUnavailableException(String message) {
      super(message);
    }

    public LibvipsUnavailableException(String message, Throwable cause) {
      super(message, cause);
    }
  }

}