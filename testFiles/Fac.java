class myClass {
    public int[] sum(int[] A, int[] B) {
        int[] C;
        int i;
        C = new int[A.length];
        i = 0;
        while (i < A.length) {
            C[i] = A[i] + B[i];
            i = i + 1;
        }
        return C;
    }
}