import io;

class Fac {
    public int ComputeFac(int num) {
        int num_aux;
        if (num < 1)
            num_aux = 1;
        else
            num_aux = num * (this.ComputeFac(num - 1));
        return num_aux;
    }

    public static void main(String[] args) {
        io.println(new Fac().ComputeFac(10)); //assuming the existence
        // of the classfile io.class
        /*
            adsaskdaskjdkljq2wleqkwljdq String []]@¹£¹@£12
        */
    }
}