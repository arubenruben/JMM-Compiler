import io;

class Overall2 {

    public static void main(String[] args) {

        int[] list;
        int i;
        int counter;

        list = new int[10];

        while (i < list.length) {
            list[i] = i;
            i = i + 1;
        }

        if (!i < 0) {
            i = 0;
        } else {
            i = 0;
        }

        while (i < list.length) {
            if (!i < 0) {
                counter = counter * 0;
            } else {
                counter = 0;
            }
            counter = counter + list[i]
            i = i + 1;
        }

        io.println(counter);
    }
}