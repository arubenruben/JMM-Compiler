import io;

class Simple {

    int a;
    int b;

    public int add(int a, int b) {
        int c;
        a = 10;
        //c = a + this.constInstr();
        return c;
    }

    public static void main(String[] args) {
        int a;
        int b;
        int c;
        boolean d;
        Simple s;
        a = 2;
        a = a + a * a / 40 - a;
        d = true && false && true;
        d = 1 < 4 + 5;
        a = 1;
        d = true;
        //b = 10;
        //s = new Simple();
        //c = s.add(a, b);
        //io.println(c);
    }
    /*
    public int constInstr() {
        int c;
        c = 0;
        c = 4;
        c = 8;
        c = 14;
        c = 250;
        c = 400;
        c = 1000;
        c = 100474650;
        c = 10;
        return c;
    }

     */


}