package g4.crypto;
import java.awt.image.BufferedImage;

public class ShadowImage {
  private BufferedImage image;
  private int order;

  public ShadowImage(BufferedImage image, int order) {
    this.image = image;
    this.order = order;
  }

  public BufferedImage getImage() {
    return image;
  }

  public int getOrder() {
    return order;
  }
}
