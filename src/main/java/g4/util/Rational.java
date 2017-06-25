package g4.util;

public class Rational {
  private int numerator;
  private int denominator;
  public Rational(double d) {
    String s = String.valueOf(d);
    int digitsDec = s.length() - 1 - s.indexOf('.');

    int denominator = 1;
    for(int i = 0; i < digitsDec; i++){
      d *= 10;
      denominator *= 10;
    }
    int numerator = (int) Math.round(d);
    this.numerator = numerator; this.denominator = denominator;
  }

  public Rational(int numerator, int denominator) {
    this.numerator = numerator; this.denominator = denominator;
  }

  public String toString() {
    return String.valueOf(numerator) + "/" + String.valueOf(denominator);
  }

  public int getNumerator() {
    return numerator;
  }

  public int getDenominator() {
    return denominator;
  }
}
