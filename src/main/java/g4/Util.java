package g4;

import java.awt.FlowLayout;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Util {
  public static void displayImage(Image img) {
    ImageIcon icon = new ImageIcon(img);
    JFrame frame = new JFrame();
    frame.setLayout(new FlowLayout());
    frame.setSize(img.getWidth(null)+50, img.getHeight(null)+50);
    JLabel lbl = new JLabel();
    lbl.setIcon(icon);
    frame.add(lbl);
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
}
