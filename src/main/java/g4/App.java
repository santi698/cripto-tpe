package g4;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class App {
  public static void main(String[] args) throws Exception {
    System.out.println("Hola mundo");
    BufferedImage image = ImageIO.read(App.class.getResource("/sin_secreto/Alfred.bmp"));
    Util.displayImage(image);
  }
}
