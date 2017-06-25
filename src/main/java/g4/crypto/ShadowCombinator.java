package g4.crypto;

import g4.util.GaussianElimination;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.util.Arrays;
import java.util.List;

public class ShadowCombinator {
  private int r;
  public ShadowCombinator(int r) {
    this.r = r;
  }

  public BufferedImage restore(List<ShadowImage> shadows, int width, int height) {
    assert shadows.size() >= r;
    assert width*height == shadows.get(0).getImage().getWidth()*shadows.get(0).getImage().getHeight()*r;
    byte[] result = new byte[width*height];
    shadows = shadows.subList(0, r); // Keep only r if given more
    int sizeArray = shadows.get(0).getImage().getRaster().getDataBuffer().getSize();
    for(int pixelIndex = 0; pixelIndex < sizeArray; pixelIndex++){
      double[][] mat = new double[r][r];
      double[] y = new double[r];
      for(int shadowIndex = 0; shadowIndex < r; shadowIndex++){
        for(int xPower = 0; xPower < r; xPower++){
          mat[shadowIndex][xPower] = Math.pow(shadows.get(shadowIndex).getOrder(), xPower);
        }
        y[shadowIndex] = shadows.get(shadowIndex).getImage().getRaster().getDataBuffer().getElem(pixelIndex);
      }
      double[] coefficients = GaussianElimination.lsolve(mat, y);
      byte[] pixels = normalizeCoefficients(coefficients);
      for(int offset = 0; offset < pixels.length; offset++){
        result[pixelIndex*r + offset] = pixels[offset];
      }
    }
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    image.setData(Raster.createRaster(image.getSampleModel(), new DataBufferByte(result, result.length), new Point()));
    return image;
  }

  private byte normalizeMod257(double number){
    return 0;
  }

  private byte[] normalizeCoefficients(double[] coefficients){
    byte[] result = new byte[coefficients.length];
    for(int coefficientIndex = 0; coefficientIndex < coefficients.length; coefficientIndex++){
      result[coefficientIndex] = normalizeMod257(coefficients[coefficientIndex]);
    }
    return result;
  }
}
