package g4;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import g4.crypto.ImageObfuscator;
import g4.crypto.ShadowGenerator;

public class App {
  public static void main(String[] args) throws Exception {
    BufferedImage image = ImageIO.read(App.class.getResource("/sin_secreto/Alfred.bmp"));
    Util.displayImage(image); // Displays Original image
    BufferedImage obfuscatedImage = new ImageObfuscator(150).obfuscate(image);
    List<BufferedImage> shadows = new ShadowGenerator(4, 6).generateShadows(obfuscatedImage);
    for (BufferedImage shadow : shadows) {
      Util.displayImage(shadow); // Displays shadows
    }
  }
}
