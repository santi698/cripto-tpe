package g4.util;

import g4.util.GFArithmetic;

public class GaussianElimination {
    public static int GF_PRIME = 257;
    // Gaussian elimination with partial pivoting
    public static int[] lsolve(int[][] mat) {
        int k = mat.length;
        int i, j, t, a, temp;

        /* take matrix to echelon form */
        for(j = 0; j < k-1; j++){
            for(i = k-1; i > j; i--){
                a = mat[i][j] * GFArithmetic.inverse(mat[i-1][j], GF_PRIME);
                for(t = j; t < k+1; t++){
                    temp = mat[i][t] - ((mat[i-1][t] * a) % GF_PRIME);
                    mat[i][t] = mod(temp, GF_PRIME);
                }
            }
        }

        /* take matrix to reduced row echelon form */
        for(i = k-1; i > 0; i--){
            mat[i][k] = (mat[i][k] * GFArithmetic.inverse(mat[i][i], GF_PRIME)) % GF_PRIME;
            mat[i][i] = (mat[i][i] * GFArithmetic.inverse(mat[i][i], GF_PRIME)) % GF_PRIME;
            for(t = i-1; t >= 0; t--){
                temp = mat[t][k] - ((mat[i][k] * mat[t][i]) % GF_PRIME);
                mat[t][k] = mod(temp, GF_PRIME);
                mat[t][i] = 0;
            }
        }
        int[] result = new int[k];
        for (int z = 0; z < k; z++) {
            result[z] = mat[z][k];
        }
        return result;
    }
    private static int mod(int n, int mod) {
        int result = n % mod;
        return result < 0 ? result + mod : result;
    }
    public static void main(String[] args) {
        int[][] A = {
            { 1, 1, 1, 1, 1, 1, 1, 1, 71 },
            { 1, 2, 4, 8, 16, 32, 64, 128, 48 },
            { 1, 3, 9, 27, 81, 243, 729, 2187, 162 },
            { 1, 4, 16, 64, 256, 1024, 4096, 16384, 196 },
            { 1, 5, 25, 125, 625, 3125, 15625, 78125, 44 },
            { 1, 6, 36, 216, 1296, 7776, 46656, 279936, 77},
            { 1, 7, 49, 343, 2401, 16807, 117649, 823543, 14 },
            { 1, 8, 64, 512, 4096, 32768, 262144, 2097152, 218 }
        };
        int[] x = lsolve(A);


        // print results
        for (int i = 0; i < x.length; i++) {
            System.out.println(x[i]);
        }

    }
}
