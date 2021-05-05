class myClass {
    public boolean check(int[] A, int N, int T) {
        int i;
        boolean all;
        all = false;
        i = 0;
        while ((i < N) && (A[i] < T)) {
            i = i + 1;
        }
        if (i < N)
            all = true;
        else
            all = false;

        while ((i < N) && (A[i] < T)) {
            i = i + 1;
        }

        while ((i < N) && (A[i] < T)) {
            i = i + 1;
        }

        return all;
    }
}