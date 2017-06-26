package g4.crypto;

import g4.util.GaussianElimination;
import g4.util.Images;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

public class ShadowCombinator {
  private int r;
  public ShadowCombinator(int r) {
    this.r = r;
  }

  public BufferedImage restore(List<ShadowImage> shadows, int width, int height) {
    assert shadows.size() >= r;
    assert width * height == shadows.get(0).getImage().getWidth() * shadows.get(0).getImage().getHeight() * r;
    byte[] result = new byte[width * height];
    shadows = shadows.subList(0, r); // Keep only r if given more
    int sizeArray = shadows.get(0).getImage().getRaster().getDataBuffer().getSize();
    for(int pixelIndex = 0; pixelIndex < sizeArray; pixelIndex++){
      int[][] mat = new int[r][r];
      int[] y = new int[r];
      for(int shadowIndex = 0; shadowIndex < r; shadowIndex++){
        DataBuffer shadow = shadows.get(shadowIndex).getImage().getRaster().getDataBuffer();
        for(int xPower = 0; xPower < r; xPower++) {
          mat[shadowIndex][xPower] = (int) Math.pow(shadows.get(shadowIndex).getOrder(), xPower);
        }
        y[shadowIndex] = shadow.getElem(pixelIndex);
      }
      int[] coefficients = GaussianElimination.lsolve(mat, y);
      byte[] pixels = normalizeCoefficients(coefficients);
      for(int offset = 0; offset < pixels.length; offset++) {
        result[pixelIndex * r + offset] = pixels[offset];
      }
    }
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    image.setData(Raster.createRaster(image.getSampleModel(), new DataBufferByte(result, result.length), new Point()));
    return image;
  }

  private byte normalizeMod257(int number) {
    int normalizedValue = number % 257;
    assert normalizedValue != 256;
    return (byte) normalizedValue;
  }

  private int inverse(int number, int modulo) {
    int t = 0;
    int newt = 1;
    int r = modulo;
    int newr = number;
    int aux;
    while (newr != 0) {
      int quotient = r / newr;
      aux = newt;
      newt = t - quotient * newt;
      t = aux;
      aux = newr;
      newr = r - quotient * newr;
      r = aux;
    }
    if (r > 1) { throw new RuntimeException("a is not invertible"); }
    if (t < 0) { t = t + modulo; }
      return t;
  }
  private byte[] normalizeCoefficients(int[] coefficients){
    byte[] result = new byte[coefficients.length];
    for(int coefficientIndex = 0; coefficientIndex < coefficients.length; coefficientIndex++){
      result[coefficientIndex] = normalizeMod257(coefficients[coefficientIndex]);
    }
    return result;
  }

  public static void main(String[] args) throws Exception {
    int r = 8;
    int n = 8;
    int seed = 1;
    ImageObfuscator obfuscator = new ImageObfuscator(seed);
    BufferedImage image = ImageIO.read(Paths.get("src/main/resources/sin_secreto/Alfred.bmp").toFile());
    Images.displayImage(obfuscator.obfuscate(image));
    BufferedImage obfuscatedImage = obfuscator.obfuscate(image);
    List<BufferedImage> generatedShadows = new ShadowGenerator(r, n).generateShadows(obfuscatedImage);
    List<ShadowImage> shadows = new ArrayList<>(r);
    for (int i = 0; i < generatedShadows.size(); i++) {
      shadows.add(i, new ShadowImage(generatedShadows.get(i), i + 1, seed, image.getWidth(), image.getHeight()));
    }
    BufferedImage secret = new ShadowCombinator(r).restore(shadows, image.getWidth(), image.getHeight());
    Images.displayImage(obfuscator.obfuscate(secret));
  }
}
