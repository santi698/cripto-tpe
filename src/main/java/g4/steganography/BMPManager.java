package g4.steganography;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;

public class BMPManager {
  byte[] bytes;
  File file;
  public BMPManager(File bmpFile) {
    file = bmpFile;
    try {
      bytes = Files.readAllBytes(bmpFile.toPath());
    } catch(IOException exception) {
      exception.printStackTrace();
      System.exit(3);
    }
    assert bytes[0] == 'B' && bytes[1] == 'M';
  }

  public File getFile() {
    return file;
  }

  public void setReservedZone1(int value) {
    bytes[6] = (byte) (value & 0xFF);
    bytes[7] = (byte) (value >> 8);
  }

  public void setReservedZone2(int value) {
    bytes[8] = (byte) (value & 0xFF);
    bytes[9] = (byte) (value >> 8);
  }

  public int getWidth() {
    return getLittleEndianInteger(18);
  }

  public int getHeight() {
    return getLittleEndianInteger(22);

  }
  public int getReservedZone1() {
    return getLittleEndianShort(6);
  }

  public int getReservedZone2() {
    return getLittleEndianShort(8);
  }

  public byte[] getImageData() {
    int offset = getLittleEndianInteger(10);
    return Arrays.copyOfRange(bytes, offset, bytes.length);
  }

  public void setImageData(byte[] imageData) {
    int offset = getLittleEndianInteger(10);
    System.arraycopy(imageData, 0, bytes, offset, imageData.length);
  }

  public void writeToFile() throws IOException {
    Files.write(file.toPath(), bytes);
  }
  private int getLittleEndianShort(int index) {
    return (bytes[index] & 0xFF) + ((bytes[index + 1] & 0xFF) << 8);
  }

  private int getLittleEndianInteger(int index) {
    return (bytes[index] & 0xFF) +
           ((bytes[index + 1] & 0xFF) << 8) +
           ((bytes[index + 2] & 0xFF) << 16) +
           ((bytes[index + 3] & 0xFF) << 24);
  }

  public static void main(String[] args) throws Exception {
    BMPManager manager = new BMPManager(Paths.get("/tmp/Alfred.bmp").toFile());
    byte[] imageData = manager.getImageData();
    ArrayUtils.reverse(imageData);
    manager.setImageData(imageData);
  }
}
