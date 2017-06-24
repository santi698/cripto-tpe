package g4;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;
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

public class App {
  public static void main(String[] args) throws Exception {
    CommandLine cmd = getCommandLine(args);
    File dir = Paths.get(cmd.getOptionValue("dir")).toFile();
    File secretFile = Paths.get(cmd.getOptionValue("secret")).toFile().getCanonicalFile();
    AppMode mode = cmd.hasOption("d") ? AppMode.DISTRIBUTE : AppMode.RETRIEVE;
    switch(mode) {
      case DISTRIBUTE:
        int k = Integer.valueOf(cmd.getOptionValue("k"));
        int n = Integer.valueOf(cmd.getOptionValue("n"));
        BufferedImage image = ImageIO.read(secretFile);
        Util.displayImage(image);
        BufferedImage obfuscatedImage = obfuscateImage(image);
        List<BufferedImage> generatedShadows = new ShadowGenerator(k, n).generateShadows(obfuscatedImage);
        for (BufferedImage shadow : generatedShadows) {
          Util.displayImage(shadow);
        }
        saveShadows(generatedShadows, dir);
        System.exit(0);
        break;
      case RETRIEVE:
        List<ShadowImage> shadows = recoverShadows(dir);
        BufferedImage secretObfuscatedImage = new ShadowCombinator(Integer.valueOf(cmd.getOptionValue("r"))).restore(shadows);
        BufferedImage secretImage = obfuscateImage(secretObfuscatedImage);
        ImageIO.write(secretImage, "bmp", secretFile);
    }
  }

  private static void saveShadows(List<BufferedImage> shadows, File dir) {
    assert dir.listFiles().length == shadows.size();
    for (File file : dir.listFiles()) {
      // TODO hide shadow in file
    }
  }

  private static List<ShadowImage> recoverShadows(File dir) {
    // TODO
    return Collections.emptyList();
  }

  private static BufferedImage obfuscateImage(BufferedImage image) {
    long seed = new Random().nextLong();
    System.out.println("Obfuscating with seed " + seed);
    return obfuscateImage(image, seed);
  }
  private static BufferedImage obfuscateImage(BufferedImage image, long seed) {
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
