import io;

class HelloWorld {
    public static void main(String[] args) {
        int a;
        int b;
        int c;
        boolean d;
        boolean f;
        a = 10;
        b = 20;
        d = true;
        f = false;

        if (a < 15) {
            c = a;
        } else {
            c = b;
        }

        // 10
        io.println(c);

        if (true) {
            c = 5;
        } else {
            c = 6;
        }

        // 5
        io.println(c);

        if (d && f) {
            c = 0;
        } else {
            c = 2;
        }

        // 2
        io.println(c);

    }
}