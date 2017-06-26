package g4.util;

public class GFArithmetic {
  public static int inverse(int number, int modulo) {
    int t = 0;
    int newt = 1;
    int r = modulo;
    int newr = number;
    int aux;
    while (newr != 0) {
      int quotient = r / newr;
      aux = newt;
      newt = t - quotient * newt;
      t = aux;
      aux = newr;
      newr = r - quotient * newr;
      r = aux;
    }
    if (r > 1) { throw new RuntimeException("a is not invertible"); }
    if (t < 0) { t = t + modulo; }
      return t;
  }
}
