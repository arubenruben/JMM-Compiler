import io;

class List {

    public int[] concat(int[] list1, int[] list2) {
        int[] listResult;
        int i;
        int j;
        listResult = new int[list1.length + list2.length];
        i = 0;
        while (i < list1.length) {
            listResult[i] = list1[i];
            i = i + 1;
        }
        j = 0;
        while (j < list2.length) {
            listResult[i] = list2[j];
            i = i + 1;
            j = j + 1;
        }

        return listResult;
    }

    public int at(int[] list, int index) {
        return list[index];
    }

    public boolean print(int[] list) {
        int i;
        i = 0;
        while (i < list.length) {
            io.print(list[i]);
            i = i + 1;
        }

        return true;
    }

    public int[] slice(int[] list, int upperBound) {
        int[] result;
        int i;

        result = new int[upperBound];
        i = 0;

        while (i < upperBound) {
            result[i] = list[i];
            i = i + 1;
        }

        return result;
    }

    public int[] slice(int[] list, int lowerBound, int upperBound) {

        int[] result;
        int i;
        int j;

        result = new int[upperBound - lowerBound];
        i = lowerBound;
        j = 0;
        while (i < upperBound) {
            result[j] = list[i];
            i = i + 1;
            j = j + 1;
        }

        return result;
    }

    public boolean quicksort(int[] L, int lo, int hi) {
        int p;

        if (lo < hi) {
            p = this.partition(L, lo, hi);

            this.quicksort(L, lo, p - 1);
            this.quicksort(L, p + 1, hi);
        } else {
        }

        return true;
    }

    public int partition(int[] L, int lo, int hi) {
        int p;
        int i;
        int j;
        int tmp;

        p = L[hi];
        i = lo;
        j = lo;

        while (j < hi) {
            if (L[j] < p) {
                tmp = L[i];
                L[i] = L[j];
                L[j] = tmp;

                i = i + 1;
            } else {
            }

            j = j + 1;
        }

        tmp = L[i];
        L[i] = L[hi];
        L[hi] = tmp;

        return i;

    }

    public static void main(String[] args) {
        int[] list1;
        int[] list2;
        int[] result_list;
        int i;
        int result_value;
        List list;

        list = new List();
        list1 = new int[10];
        list2 = new int[10];
        i = 0;

        while (i < 10) {
            list1[i] = i;
            list2[i] = i;
            i = i + 1;
        }
        result_list = list.concat(list1, list2);
        list.print(result_list);

        io.println();

        result_value = list.at(list1, 1);
        io.println(result_value);

        result_list = list.slice(list1, 3);
        list.print(result_list);

        io.println();

        result_list = list.slice(list1, 3, 4);
        list.print(result_list);

        //io.println();

        //result_list = list.quicksort(list1, 0, result_list.length - 1)
        //list.print(result_list);

    }
}