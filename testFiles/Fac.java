import io;

class Example {
    public static void main(String[] args) {
        int sum;
        int i;
        int[] A;
        A = new int[4];

        sum = 0;
        i = 0;

        while (i < 50) {
            sum = sum + 1;
            i = i + 1;
        }
        io.println(sum);
    }
}