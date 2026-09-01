package dev.stefa.forestmap.assets.image;

import app.photofox.vipsffm.VImage;
import app.photofox.vipsffm.VSource;
import app.photofox.vipsffm.Vips;
import app.photofox.vipsffm.VipsOption;
import app.photofox.vipsffm.enums.VipsSize;
import org.jspecify.annotations.NullMarked;

import java.io.ByteArrayOutputStream;

@NullMarked
public class ImageProcessor {
  public static byte[] shrink(byte[] input, int size) {
    var buffer = new ByteArrayOutputStream();
    Vips.run(arena -> {
      VSource source = VSource.newFromBytes(arena, input);
      VImage image = VImage.thumbnailSource(arena, source, size,
          VipsOption.Int("height", size),
          VipsOption.Enum("size", VipsSize.SIZE_DOWN) // Only downsize
      );

      image.writeToStream(buffer, ".jpg",
          VipsOption.Int("Q", 85),
          VipsOption.Boolean("strip", true),
          VipsOption.Boolean("optimize_coding", true)
      );
    });
    return buffer.toByteArray();
  }
}
