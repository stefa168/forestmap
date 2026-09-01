package dev.stefa.forestmap.assets.image.health;

import org.jspecify.annotations.NullMarked;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

@NullMarked
public class LibvipsFailureAnalyzer extends AbstractFailureAnalyzer<LibvipsHealthCheck.LibvipsUnavailableException> {
  @Override
  protected FailureAnalysis analyze(Throwable rootFailure, LibvipsHealthCheck.LibvipsUnavailableException cause) {
    String os = System.getProperty("os.name", "").toLowerCase();
    String installHint;
    if (os.contains("mac")) {
      installHint = "brew install vips";
    } else if (os.contains("win")) {
      installHint = "Download a libvips build from https://github.com/libvips/build-win64-mxe/releases "
          + "and add its 'bin' directory to PATH.";
    } else {
      installHint = """
          sudo apt-get install libvips42   # Debian/Ubuntu
          sudo dnf install vips            # Fedora/RHEL""";
    }

    String description = """
        The application failed to start because libvips %d.%d+ is required but could not be \
        loaded.
        
        Reported problem: %s""".formatted(LibvipsHealthCheck.MIN_MAJOR, LibvipsHealthCheck.MIN_MINOR, cause.getMessage());

    String action = """
        Install libvips and make sure it is on the dynamic library path, then restart the app:
        
        %s
        
        On Linux you may also need to set LD_LIBRARY_PATH (or DYLD_LIBRARY_PATH on macOS) \
        if libvips is installed in a non-standard location.""".formatted(installHint);

    return new FailureAnalysis(description, action, cause);
  }
}