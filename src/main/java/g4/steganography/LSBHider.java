package g4.steganography;

import g4.util.BitManipulation;

public class LSBHider {
  public LSBHider() {}

  public byte[] hide(byte secret, byte[] bytes) {
    return hide(secret, bytes, 0);
  }

  public byte[] hide(byte secret, byte[] bytes, int offset) {
    for (int bitNumber = 7; bitNumber >= 0; bitNumber--) {
      bytes[bitNumber + offset] = 
        BitManipulation.setLSB(BitManipulation.getBit(secret, 7 - bitNumber), bytes[bitNumber + offset]);
    }
    return bytes;
  }

  public byte recover(byte[] bytes) {
    return recover(bytes, 0);
  }

  public byte recover(byte[] bytes, int offset) {
    byte secret = 0;
    for (int bitNumber = 7; bitNumber >= 0; bitNumber--) {
      byte lsb = BitManipulation.getLSB(bytes[bitNumber + offset]);
      secret += lsb << 7 - bitNumber;
    }
    return secret;
  }
}
