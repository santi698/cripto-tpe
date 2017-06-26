package g4.util;

import g4.util.GFArithmetic;

public class GaussianElimination {
    public static int GF_PRIME = 257;
    // Gaussian elimination with partial pivoting
    public static int[] lsolve(int[][] A, int[] b) {
        int n = b.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                A[i][j] = A[i][j] % GF_PRIME;
            }
        }
        for (int p = 0; p < n; p++) {

            // find pivot row and swap
            int max = p;
            for (int i = p + 1; i < n; i++) {
                if (Math.abs(A[i][p]) > Math.abs(A[max][p])) {
                    max = i;
                }
            }
            
            int[] temp = A[p];
            A[p] = A[max];
            A[max] = temp;
            
            int t = b[p];
            b[p] = b[max];
            b[max] = t;

            // singular or nearly singular
            if (Math.abs(A[p][p]) == 0) {
                throw new ArithmeticException("Matrix is singular");
            }

            // pivot within A and b
            for (int i = p + 1; i < n; i++) {
                int alpha = (A[i][p] * GFArithmetic.inverse(A[p][p], GF_PRIME)) % GF_PRIME;
                b[i] -= alpha * b[p];
                b[i] = b[i] % GF_PRIME;
                for (int j = p; j < n; j++) {
                    A[i][j] -= alpha * A[p][j];
                    A[i][j] = A[i][j] % GF_PRIME;
                }
            }
        }

        // back substitution
        int[] x = new int[n];
        for (int i = n - 1; i >= 0; i--) {
            int sum = 0;
            for (int j = i + 1; j < n; j++) {
                sum += (A[i][j] * x[j]);
                sum = sum % GF_PRIME;
            }
            x[i] = ((b[i] - sum) * GFArithmetic.inverse(A[i][i], GF_PRIME)) % GF_PRIME;
        }
        return x;
    }
    public static void main(String[] args) {
        int[][] A = {
            { 1, 1, 1, 1, 1, 1, 1, 1 },
            { 1, 2, 4, 8, 16, 32, 64, 128 },
            { 1, 3, 9, 27, 81, 243, 729, 2187 },
            { 1, 4, 16, 64, 256, 1024, 4096, 16384 },
            { 1, 5, 25, 125, 625, 3125, 15625, 78125 },
            { 1, 6, 36, 216, 1296, 7776, 46656, 279936},
            { 1, 7, 49, 343, 2401, 16807, 117649, 823543 },
            { 1, 8, 64, 512, 4096, 32768, 262144, 2097152}
        };
        int[] b = { 71, 48, 162, 196, 44, 77, 14, 218 };
        int[] x = lsolve(A, b);


        // print results
        for (int i = 0; i < x.length; i++) {
            System.out.println(x[i]);
        }

    }
}
