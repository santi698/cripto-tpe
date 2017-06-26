package g4.steganography;

import g4.util.BitManipulation;

class LSBHider {
  public LSBHider() {}

  public byte[] hide(byte secret, byte[] bytes) {
    for (int bitNumber = 7; bitNumber >= 0; bitNumber--) {
      bytes[bitNumber] = BitManipulation.setLSB(BitManipulation.getBit(secret, 7 - bitNumber), bytes[bitNumber]);
    }
    return bytes;
  }

  public byte recover(byte[] bytes) {
    byte secret = 0;
    for (int bitNumber = 7; bitNumber >= 0; bitNumber--) {
      byte lsb = BitManipulation.getLSB(bytes[bitNumber]);
      secret += lsb << 7 - bitNumber;
    }
    return secret;
  }
}
