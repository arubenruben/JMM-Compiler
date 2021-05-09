import MathUtils; //lowerLimit,upperLimit
import Quicksort;

class Lazysort extends Quicksort {

    public static void main(String[] a) {
        Lazysort q;
        boolean d;
        int[] L;

        int i;

        L = new int[10];

        i = 0;
        while (i < L.length) {
            L[i] = L.length - i;
            i = i + 1;
        }

        q = new Lazysort();

        q.quicksort(L);
        q = q.printL(L);
        d = q.getBool(L);
    }

    public boolean getBool(int[] L) {
        return true;
    }

    public boolean quicksort(int[] L) {

        boolean lazy;

        int rand;

        rand = MathUtils.random(0, 5);
        if (rand < 4) {
            this.beLazy(L);
            lazy = true;
        } else {
            lazy = false;
        }


        if (lazy && lazy) {
            //lazy = !lazy;
        } else {
            lazy = this.quicksort(L, 0, L.length - 1);
        }


        return lazy;
    }

    public boolean beLazy(int[] L) {
        int _allowedNameL;
        int allowedNameI;
        int rand;
        _allowedNameL = L.length;

        allowedNameI = 0;
        while (allowedNameI < _allowedNameL / 2) {
            L[allowedNameI] = MathUtils.random(0, 10);

            allowedNameI = allowedNameI + 1;
        }

        while (allowedNameI < _allowedNameL) {
            rand = MathUtils.random(0, 10);
            L[allowedNameI] = rand + 1;

            allowedNameI = allowedNameI + 1;
        }


        return true;
    }

}
