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
        C[i] = A[helper.add()] + B[i + 1 + 5 + 9];
        while (i < A.length) {
            i = i + 1;
        }
        return C;
    }
}