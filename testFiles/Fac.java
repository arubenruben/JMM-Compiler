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

    public boolean sort(int[] list, int lo, int hi) {
        int p;
        int dummy;
        int[] par1;
        int par2;
        int par3;

        par1 = list;
        par2 = lo;
        par3 = hi;

        if (par2 < par3) {
            p = this.partition(par1, par2, par3);

            this.sort(par1, par2, p - 1);
            this.sort(par1, p + 1, par3);
        } else {
            dummy = 0;
        }

        return true;
    }

    public int partition(int[] list, int lo, int hi) {
        int p;
        int i;
        int j;
        int tmp;

        p = list[hi];
        i = lo;
        j = lo;

        while (j < hi) {
            if (list[j] < p) {
                tmp = list[i];
                list[i] = list[j];
                list[j] = tmp;

                i = i + 1;
            } else {
            }

            j = j + 1;
        }

        tmp = list[i];
        list[i] = list[hi];
        list[hi] = tmp;

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

        io.println();

        list.sort(list1, 0, list1.length - 1);
        list.print(list1);

    }
}
