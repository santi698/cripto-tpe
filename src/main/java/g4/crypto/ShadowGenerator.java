package g4.crypto;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;

public class ShadowGenerator {
  private int r;
  private int shadowAmount;
  private BufferedImage[] shadows = new BufferedImage[r];
  public ShadowGenerator(int r, int shadowAmount) {
    assert r >= 2;
    assert shadowAmount >= r;
    this.r = r;
    this.shadowAmount = shadowAmount;
  }

  private void initializeShadows(BufferedImage sourceImage) {
    this.shadows = new BufferedImage[r];
    for (int shadowIndex = 0; shadowIndex < shadowAmount; shadowIndex++) {
      this.shadows[shadowIndex] = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight() / r, BufferedImage.TYPE_BYTE_GRAY);
    }
  }

  public BufferedImage[] generateShadows(BufferedImage sourceImage) {
    initializeShadows(sourceImage);
    DataBuffer buffer = sourceImage.getRaster().getDataBuffer();
    int size = sourceImage.getWidth() * sourceImage.getWidth();

    for (int imageIndex = 0; imageIndex < size; imageIndex += r) {
      int[] section = new int[r];
      for (int sectionIndex = 0; sectionIndex < r; sectionIndex++) {
        if (size < sectionIndex) {
          section[sectionIndex] = 0;
        } else {
          section[sectionIndex] = buffer.getElem(imageIndex + sectionIndex);
        }
      }
      setShadowsPixel(imageIndex / r, generateSectionShadows(section));
    }
    return shadows;
  }

  private void setShadowsPixel(int pixelIndex, int[] pixelShadows) {
    assert pixelShadows.length == shadowAmount;
    if (pixelIndex >= shadows[0].getWidth() * shadows[0].getHeight()) {
      return;
    }
    for (int shadowIndex = 0; shadowIndex < shadowAmount; shadowIndex++) {
      DataBuffer buffer = this.shadows[shadowIndex].getRaster().getDataBuffer();
      buffer.setElem(pixelIndex, pixelShadows[shadowIndex]);
    }
  }

  private int[] generateSectionShadows(int[] section) {
    assert section.length == this.r;
    int[] shadows = new int[this.r];

    for (int shadowNumber = 0; shadowNumber < shadowAmount; shadowNumber++) {
      for (int xPower = 0; xPower < section.length; xPower++) {
        shadows[shadowNumber] += Math.pow(shadowNumber + 1, xPower) * section[xPower];
      }
      shadows[shadowNumber] %= 257;
    }

    return shadows;
  }
}
