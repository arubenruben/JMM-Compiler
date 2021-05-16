import io;

class Duck {

    public boolean quack(Duck animal) {
        io.println(true);
        return true;
    }

    public boolean quack(int var) {
        io.println(false);
        return false;
    }

    public boolean quack(boolean var) {
        io.println(false);
        return false;
    }

    public static void main(String[] args) {
        boolean dummy;
        boolean dummy1;
        dummy = true;
        dummy1 = false;
        if (dummy && dummy1) {
            dummy = true;
        } else {
            dummy = true;
        }
        dummy = true;
    }
}