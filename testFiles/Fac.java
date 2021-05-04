class myClass {
    public int add() {
        return 1;
    }

    public int[] sum(int[] A, int[] B) {
        int i;
        int[] C;
        myClass helper;
        helper = new myClass();
        C = new int[A.length];
        i = 0;
        C = helper.sum(new int[helper.add()], new int[1]);
        while (i < A.length) {
            C[i] = A[i] + B[i];
            i = i + 1;
        }
        return C;
    }
}