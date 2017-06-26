package g4.crypto;
import java.awt.image.BufferedImage;

public class ShadowImage {
  private BufferedImage image;
  private int order;
  private int seed;
  private int originalWidth;
  private int originalHeight;

  public ShadowImage(BufferedImage image, int order, int seed, int originalWidth, int originalHeight) {
    this.image = image;
    this.order = order;
    this.seed = seed;
    this.originalHeight = originalHeight;
    this.originalWidth = originalWidth;
  }

  public BufferedImage getImage() {
    return image;
  }

  public int getOrder() {
    return order;
  }

  public int getSeed() {
    return seed;
  }

  public int getOriginalWidth() {
    return originalWidth;
  }

  public int getOriginalHeight() {
    return originalHeight;
  }
}
