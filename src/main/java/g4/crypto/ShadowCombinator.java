package g4.crypto;

import java.awt.image.BufferedImage;
import java.util.List;

public class ShadowCombinator {
  private int r;
  public ShadowCombinator(int r) {
    this.r = r;
  }

  public BufferedImage restore(List<ShadowImage> shadows) {
    assert shadows.size() >= r;
    shadows = shadows.subList(0, r); // Keep only r if given more
    // TODO!!
    return null;
  }
}
