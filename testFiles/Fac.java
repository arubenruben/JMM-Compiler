import io;

class Adder {

    public boolean add(int i1, int i2) {
        return i1 + i2;
    }

    public boolean add(boolean b1, boolean b2) {
        return b1 && b2;
    }

    public boolean add(int[] list) {
        int i;
        int counter;
        i = 0;
        counter = 0;

        while (i < list.length) {
            counter = counter + list[i];
            i = i + 1;
        }

        return counter;
    }

    public static void main(String[] args) {
        int index;

        int[] list;

        index = 0;
        list = new int[10];

        while (index < list.length) {
            list[index] = index;
            index = index + 1;
        }

        io.println(add(list));
        io.println(4, 4);
        io.println(true, true);
        io.println(false, false);
    }
}