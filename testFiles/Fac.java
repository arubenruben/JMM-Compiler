import io;

class Overall5 {

    int[] c;
    int[] a;
    int[] b;
    int i;

    public static void main(String[] args) {

        a = new int[5];
        b = new int[5];
        c = new int[5];
        i = 0;

        while (i < 5) {
            a[i] = 1;
            b[i] = 2;
            i = i + 1;
        }
        i = 0;
        while (i < 5) {
            c[i] = a[i] + a[i];
            i = i + 1;
        }

        i = 0;
        while (i < 5) {
            io.println(c[i]);
            i = i + 1;
        }
/*
 */
        i = 0;
    }
}