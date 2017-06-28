package g4;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import g4.util.AppUtil;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.ArrayUtils;

import g4.crypto.ImageObfuscator;
import g4.crypto.ShadowCombinator;
import g4.crypto.ShadowGenerator;
import g4.crypto.ShadowImage;
import g4.steganography.BMPManager;
import g4.steganography.LSBHider;
import g4.util.BitManipulation;
import g4.util.Images;
import org.apache.commons.io.FilenameUtils;

public class App {

  public static void main(String[] args) throws Exception {
    int seed;
    CommandLine cmd = AppUtil.getCommandLine(args);
    File dir = AppUtil.getDir(cmd);

    File secretFile = AppUtil.getSecretFile(cmd);

    int n = AppUtil.getN(cmd, dir);
    int k = AppUtil.getK(cmd, n);

    AppMode mode = cmd.hasOption("d") ? AppMode.DISTRIBUTE : AppMode.RETRIEVE;

    switch(mode) {
      case DISTRIBUTE:
        seed = new Random().nextInt(256);
        BufferedImage image = ImageIO.read(secretFile);
        BufferedImage obfuscatedImage = obfuscateImage(image, seed);
        List<BufferedImage> generatedShadows = new ShadowGenerator(k, n).generateShadows(obfuscatedImage);
        List<ShadowImage> shadowsWithMetadata = new ArrayList<>(n);
        for (int shadowNumber = 0; shadowNumber < generatedShadows.size(); shadowNumber++) {
          BufferedImage shadow = generatedShadows.get(shadowNumber);
          shadowsWithMetadata.add(shadowNumber, new ShadowImage(shadow, shadowNumber + 1, seed, image.getWidth(), image.getHeight()));
        }
        saveShadows(shadowsWithMetadata, dir, n);
        break;
      case RETRIEVE:
        List<ShadowImage> shadows = recoverShadows(dir, k);

        int width = shadows.get(0).getOriginalWidth();
        int height = shadows.get(0).getOriginalHeight();
        seed = shadows.get(0).getSeed();
        BufferedImage secretObfuscatedImage = new ShadowCombinator(k).restore(shadows, width, height);
        BufferedImage secretImage = obfuscateImage(secretObfuscatedImage, seed);
        ImageIO.write(secretImage, "bmp", secretFile);
    }
  }

  private static void saveShadows(List<ShadowImage> shadows, File dir, int n) throws IOException {
    assert dir.listFiles().length == shadows.size();
    BufferedImage sampleShadow = shadows.get(0).getImage();
    int minimumSize = sampleShadow.getWidth() * sampleShadow.getHeight() * shadows.size() / 8;
    LSBHider hider = new LSBHider();
    int  shadowIndex = 0;
    List<BMPManager> bmpManagers = new ArrayList<>(shadows.size());
    for (File file : dir.listFiles()) {
      if(FilenameUtils.getExtension(file.getName()).equals("bmp")) {
        BMPManager manager = new BMPManager(file);
        if (manager.getWidth() * manager.getHeight() < minimumSize) {
          continue;
        }
        bmpManagers.add(manager);
      }
    }
    if (bmpManagers.size() < shadows.size()) {
      System.err.println("Se necesitan al menos " + shadows.size() + " en el directorio de destino");
      System.exit(1);
    }
    bmpManagers = bmpManagers.subList(0, n);
    for (BMPManager bmpManager : bmpManagers) {
      System.out.println("Saving shadow " + (shadowIndex + 1) + " in file " + bmpManager.getFile());
      byte[] carrierImageData = bmpManager.getImageData();
      ShadowImage currentShadow = shadows.get(shadowIndex);

      bmpManager.setReservedZone1(currentShadow.getSeed());
      bmpManager.setReservedZone2(currentShadow.getOrder());
      BufferedImage currentShadowData = shadows.get(shadowIndex).getImage();
      DataBuffer currentShadowDataBuffer = currentShadowData.getRaster().getDataBuffer();
      for (int shadowByteIndex = 0; shadowByteIndex < currentShadowDataBuffer.getSize(); shadowByteIndex++) {
        byte shadowByte = (byte) (currentShadowDataBuffer.getElem(shadowByteIndex));
        hider.hide(shadowByte, carrierImageData, shadowByteIndex * 8);
      }
      shadowIndex++;
      bmpManager.setImageData(carrierImageData);
      try {
        bmpManager.writeToFile();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private static List<ShadowImage> recoverShadows(File dir, int k) throws IOException {
    LSBHider hider = new LSBHider();
    List<ShadowImage> shadows = new ArrayList<ShadowImage>();
    List<BMPManager> bmpManagers = new ArrayList<>(shadows.size());
    for (File file : dir.listFiles()) {
      if(FilenameUtils.getExtension(file.getName()).equals("bmp")){
        bmpManagers.add(new BMPManager(file));
      }
    }
    bmpManagers = bmpManagers.subList(0, k);
    for (BMPManager manager: bmpManagers) {
      byte[] carrierImageData = manager.getImageData();
      int seed = manager.getReservedZone1();
      int order = manager.getReservedZone2();
      int width = manager.getWidth();
      int height = manager.getHeight();

      byte[] shadowData = new byte[width * height / k];

      for (int shadowPixel = 0; shadowPixel < shadowData.length; shadowPixel++) {
        shadowData[shadowPixel] = hider.recover(carrierImageData, shadowPixel * 8);
      }
      BufferedImage shadowImage = new BufferedImage(width * height / k, 1, BufferedImage.TYPE_BYTE_GRAY);
      shadowImage.setData(Raster.createRaster(shadowImage.getSampleModel(), new DataBufferByte(shadowData, shadowData.length), new Point()));
      shadows.add(new ShadowImage(shadowImage, order, seed, width, height));
    }
    return shadows;
  }

  private static BufferedImage obfuscateImage(BufferedImage image, int seed) {
    return new ImageObfuscator(seed).obfuscate(image);
  }
}
