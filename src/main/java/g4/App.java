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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import g4.crypto.ImageObfuscator;
import g4.crypto.ShadowCombinator;
import g4.crypto.ShadowGenerator;
import g4.crypto.ShadowImage;
import g4.steganography.BMPManager;
import g4.util.BitManipulation;
import g4.util.Images;

public class App {
  public static void main(String[] args) throws Exception {
    int seed;
    CommandLine cmd = getCommandLine(args);
    File dir = Paths.get(cmd.getOptionValue("dir")).toFile();
    File secretFile = Paths.get(cmd.getOptionValue("secret")).toFile().getCanonicalFile();
    AppMode mode = cmd.hasOption("d") ? AppMode.DISTRIBUTE : AppMode.RETRIEVE;
    switch(mode) {
      case DISTRIBUTE:
        int k = Integer.valueOf(cmd.getOptionValue("k"));
        int n = Integer.valueOf(cmd.getOptionValue("n"));
        seed = new Random().nextInt();
        BufferedImage image = ImageIO.read(secretFile);
        Images.displayImage(image);
        BufferedImage obfuscatedImage = obfuscateImage(image, seed);
        List<BufferedImage> generatedShadows = new ShadowGenerator(k, n).generateShadows(obfuscatedImage);
        List<ShadowImage> shadowsWithMetadata = new ArrayList<>(k);
        for (int shadowNumber = 0; shadowNumber <= generatedShadows.size(); shadowNumber++) {
          BufferedImage shadow = generatedShadows.get(shadowNumber);
          shadowsWithMetadata.add(shadowNumber, new ShadowImage(shadow, shadowNumber, seed, image.getWidth(), image.getHeight()));
        }
        saveShadows(shadowsWithMetadata, dir);
        System.exit(0);
      case RETRIEVE:
        List<ShadowImage> shadows = recoverShadows(dir);
        int width = shadows.get(0).getOriginalWidth();
        int height = shadows.get(0).getOriginalHeight();
        seed = shadows.get(0).getSeed();
        BufferedImage secretObfuscatedImage = new ShadowCombinator(Integer.valueOf(cmd.getOptionValue("k"))).restore(shadows, width, height);
        BufferedImage secretImage = obfuscateImage(secretObfuscatedImage, seed);
        ImageIO.write(secretImage, "bmp", secretFile);
    }
  }

  private static void saveShadows(List<ShadowImage> shadows, File dir) throws IOException {
    assert dir.listFiles().length == shadows.size();
    int  shadowIndex = 0;
    List<BMPManager> bmpManagers = new ArrayList<>(shadows.size());
    for (File file : dir.listFiles()) {
      bmpManagers.add(new BMPManager(file));
    }
    for (BMPManager bmpManager : bmpManagers) {
      byte[] carrierImageData = bmpManager.getImageData();
      ShadowImage currentShadow = shadows.get(shadowIndex);
      bmpManager.setReservedZone1(currentShadow.getSeed());
      bmpManager.setReservedZone2(currentShadow.getOrder());
      BufferedImage currentShadowData = shadows.get(shadowIndex).getImage();
      DataBuffer currentShadowDataBuffer = currentShadowData.getRaster().getDataBuffer();
      for (int shadowByteIndex = 0; shadowByteIndex < currentShadowDataBuffer.getSize(); shadowByteIndex++) {
        byte shadowByte = (byte) (currentShadowDataBuffer.getElem(shadowByteIndex));
        for (int shadowBitIndex = 0; shadowBitIndex < 8; shadowBitIndex++) {
          // En el archivo a guardar cada bit de la sombra, debo acceder al byte(bit). El numero de bit en la iteracion es
          // (nro de bytes * 8) + nro de bit.
          int carrierPixel = shadowBitIndex + (shadowByteIndex * 8);
          byte fileByte = carrierImageData[carrierPixel];
          carrierImageData[carrierPixel] = BitManipulation.setLSB(BitManipulation.getBit(shadowByte, 7 - shadowBitIndex), fileByte);
        }
      }
      shadowIndex++;
      bmpManager.setImageData(carrierImageData);
    }
  }

  private static List<ShadowImage> recoverShadows(File dir) throws IOException {
    List<ShadowImage> shadows = new ArrayList<ShadowImage>();
    List<BMPManager> bmpManagers = new ArrayList<>(shadows.size());
    for (File file : dir.listFiles()) {
      bmpManagers.add(new BMPManager(file));
    }
    for (BMPManager manager: bmpManagers) {
      byte[] carrierImageData = manager.getImageData();
      int seed = manager.getReservedZone1();
      int order = manager.getReservedZone2();
      int width = manager.getWidth();
      int height = manager.getHeight();
      
      byte[] shadowData = new byte[width * height / 8];

      for (int shadowPixel = 0; shadowPixel < shadowData.length; shadowPixel++) {
        for (int bitIndex = 0; bitIndex < 8; bitIndex++) {
          byte currentBit = BitManipulation.getLSB((byte) carrierImageData[shadowPixel * 8 + bitIndex]);
          shadowData[shadowPixel] = BitManipulation.setBitInByteAtIndex(currentBit, shadowData[shadowPixel], 7 - bitIndex);
        }
      }
      BufferedImage shadowImage = new BufferedImage(width / 8, height, BufferedImage.TYPE_BYTE_GRAY);
      shadowImage.setData(Raster.createRaster(shadowImage.getSampleModel(), new DataBufferByte(shadowData, shadowData.length), new Point()));
      shadows.add(new ShadowImage(shadowImage, order, seed, width, height));
    }
    return shadows;
  }

  private static BufferedImage obfuscateImage(BufferedImage image, int seed) {
    return new ImageObfuscator(seed).obfuscate(image);
  }
  private static Options commandLineOptions() {
    Options options = new Options();
    Option k = Option.builder("k").required()
                                 .hasArg()
                                 .desc("The k parameter of the Shamir schema")
                                 .build();
    Option n = Option.builder("n").required()
                                  .hasArg()
                                  .desc("The number of shadows to generate (must be less than or equal " +
                                                  "to the amount of files in the directory specified by -dir")
                                  .build();
    Option d = Option.builder("d").desc("Distribute in carrier images (used with -n)").build();
    Option r = Option.builder("r").desc("Recover from a set of carrier images").build();
    Option secret = Option.builder("secret")
                          .hasArg()
                          .required()
                          .desc("The image to hide (if used with -d), or the file to write " +
                                "the output to (if used with -r)")
                          .build();
    Option dir = Option.builder("dir")
                       .hasArg()
                       .required()
                       .desc("The directory of the carrier images (if used with -d), " +
                             "or the directory containing the carrier images to " +
                             "recover the secret from")
                       .build();
    return options.addOption(k)
                  .addOption(n)
                  .addOption(d)
                  .addOption(r)
                  .addOption(secret)
                  .addOption(dir);
  }

  private static CommandLine getCommandLine(String[] args) throws ParseException {
    CommandLine cmd = new DefaultParser().parse(commandLineOptions(), args);
    String mode = getMode(cmd);
    validateModeOptions(mode, cmd);
    return cmd;
  }

  private static String getMode(CommandLine cmd) {
    Boolean distribute = cmd.hasOption("d");
    Boolean recover = cmd.hasOption("r");
    if (recover == distribute) {
      System.out.println("Either -d or -r must be specified");
      System.exit(1);
    }
    return distribute ? "d" : "r";
  }

  private static void validateModeOptions(String mode, CommandLine cmd) {
    if (mode == "d") {
      if (!cmd.hasOption("k") || !cmd.hasOption("n")) {
        System.out.println("Parameters k and n are required for distributing");
        System.exit(1);
      }
    }
  }
}
