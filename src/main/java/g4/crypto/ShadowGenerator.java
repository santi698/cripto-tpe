package g4.crypto;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.util.Arrays;
import java.util.List;

public class ShadowGenerator {
  private int r;
  private int shadowAmount;
  private List<BufferedImage> shadows;
  public ShadowGenerator(int r, int shadowAmount) {
    assert r >= 2;
    assert shadowAmount >= r;
    this.r = r;
    this.shadowAmount = shadowAmount;
  }

  private void initializeShadows(BufferedImage sourceImage) {
    this.shadows = Arrays.asList(new BufferedImage[shadowAmount]);
    for (int shadowIndex = 0; shadowIndex < shadowAmount; shadowIndex++) {
      this.shadows.set(shadowIndex, new BufferedImage(sourceImage.getWidth() / r, sourceImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY));
    }
  }

  public List<BufferedImage> generateShadows(BufferedImage sourceImage) {
    initializeShadows(sourceImage);
    DataBuffer buffer = sourceImage.getRaster().getDataBuffer();
    int size = sourceImage.getWidth() * sourceImage.getWidth();

    for (int imageIndex = 0; imageIndex < size; imageIndex += r) {
      List<Integer> section = Arrays.asList(new Integer[r]);
      for (int sectionIndex = 0; sectionIndex < r; sectionIndex++) {
        if (size < sectionIndex) {
          section.set(sectionIndex, 0);
        } else {
          section.set(sectionIndex, buffer.getElem(imageIndex + sectionIndex));
        }
      }
      setShadowsPixel(imageIndex / r, generateSectionShadows(section));
    }
    return shadows;
  }

  private void setShadowsPixel(int pixelIndex, List<Integer> pixelShadows) {
    assert pixelShadows.size() == shadowAmount;
    if (pixelIndex >= shadows.get(0).getWidth() * shadows.get(0).getHeight()) {
      return;
    }
    for (int shadowIndex = 0; shadowIndex < shadowAmount; shadowIndex++) {
      DataBuffer buffer = this.shadows.get(shadowIndex).getRaster().getDataBuffer();
      buffer.setElem(pixelIndex, pixelShadows.get(shadowIndex));
    }
  }

  private List<Integer> generateSectionShadows(List<Integer> section) {
    assert section.size() == r;
    
    List<Integer> shadowPixels = Arrays.asList(new Integer[shadowAmount]);
    int shadowNumber = 0;
    while (shadowNumber < shadowAmount) {
      Integer currentShadowPixel = 0;
      for (int xPower = 0; xPower < section.size(); xPower++) {
        currentShadowPixel += (int) Math.pow(shadowNumber + 1, xPower) * section.get(xPower);
      }
      currentShadowPixel %= 257;
      if (currentShadowPixel == 256) {
        int firstNonZeroIndex;
        for (firstNonZeroIndex = 0; section.get(firstNonZeroIndex) == 0; firstNonZeroIndex++);
        section.set(firstNonZeroIndex, section.get(firstNonZeroIndex) - 1);
        shadowPixels = Arrays.asList(new Integer[shadowAmount]);
        shadowNumber = 0;
      } else {
        shadowPixels.set(shadowNumber, currentShadowPixel);
        shadowNumber++;
      }
    }

    return shadowPixels;
  }
}
