import io;

class Fac {
    public int compFac(int num) {
        int num_aux;
        if (num < 1)
            num_aux = 1;
        else
            num_aux = num * (this.compFac(num - 1));

        if (num < 1)
            num_aux = 1;
        else
            num_aux = num * (this.compFac(num - 1));

        return num_aux;
    }

    public static void main(String[] args) {
        io.println(new Fac().compFac(10));
    }
}