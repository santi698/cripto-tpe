package g4.crypto;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.util.Random;

public class ImageObfuscator {
  private long seed;
  public ImageObfuscator(long seed) {
    this.seed = seed;
  }

  public BufferedImage obfuscate(BufferedImage image) {
    ObfuscationTable table = new ObfuscationTable(image.getHeight() * image.getWidth(), seed);
    WritableRaster raster = image.copyData(image.getRaster().createCompatibleWritableRaster());
    DataBuffer buffer = raster.getDataBuffer();
    for (int i = 0; i < image.getWidth() * image.getHeight(); i++) {
      buffer.setElem(i, buffer.getElem(i) ^ table.get(i));
    }
    return new BufferedImage(image.getColorModel(), raster, image.isAlphaPremultiplied(), null);
  }

  private static class ObfuscationTable {
    private byte[] table;
    private Random randomGenerator;

    public ObfuscationTable(int size, long seed) {
      randomGenerator = new Random(seed);
      table = new byte[size];
      randomGenerator.nextBytes(table);
    }

    public int get(int x) {
      assert x > 0 && x < table.length;
      return 0xFF&table[x]; // the 0xFF& part forces unsigned interpretation
    }

    public int get(int x, int y, int width) {
      assert x + y * width < table.length;
      return 0xFF&table[x + y * width]; // the 0xFF& part forces unsigned interpretation
    }
  }
}
