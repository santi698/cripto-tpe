package g4.util;
public class BitManipulation {
  public static byte getBit(byte shadowByte, int position) {
    return (byte) ((shadowByte >> position) & 1);
  }

  public static byte setLSB(byte value, byte target) {
    return setBitInByteAtIndex(value, target, 0);
  }

  public static byte getLSB(byte data) {
    return getBit(data, 0);
  }
  public static byte setBitInByteAtIndex(byte value, byte byteToChange, int bitIndexToChange) {
    byte b = byteToChange;
    if (value == 1) {
      b = (byte) (b | (1 << bitIndexToChange));
    } else {
      b = (byte) (b & ~(1 << bitIndexToChange));
    }
    return b;
  }
}
