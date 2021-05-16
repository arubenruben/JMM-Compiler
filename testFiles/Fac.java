import io;

class SimpleCalculator {
    public static void main(String[] args) {
        int a;
        SimpleCalculator calc;
        calc = new SimpleCalculator();

        a = calc.sum(7, 4);
        io.println(a);

        a = calc.sub(15, 9);
        io.println(a);

        a = calc.mult(3, 7);
        io.println(a);

        a = calc.div(8, 4);
        io.println(a);

        a = calc.pow(2, 3);
        io.println(a);

        //a = calc.permutations(6, 3);
        //io.println(a);

        //a = calc.combinations(6, 4);
        //io.println(a);

        //a = calc.triangleArea(4, 9);
        //io.println(a);

        a = calc.cubeVolume(3);
        io.println(a);
    }

    public int sum(int num1, int num2) {
        int res;
        res = num1 + num2;

        return res;
    }

    public int sub(int num1, int num2) {
        int res;
        res = num1 - num2;

        return res;
    }

    public int mult(int num1, int num2) {
        int res;
        res = num1 * num2;

        return res;
    }

    public int div(int num1, int num2) {
        int res;
        res = num1 / num2;

        return res;
    }

    public int pow(int base, int exp) {
        int res;
        int i;
        res = 1;
        i = 0;

        while (i < exp) {
            res = res * base;
            i = i + 1;
        }

        return res;
    }

    public int factorial(int n) {
        int res;
        int i;
        res = 1;
        i = 1;

        if (n < 0) {
            res = 1;
        } else {
            while (i - 1 < n) {
                res = res * i;
                i = i + 1;
            }
        }

        return res;
    }

    public int permutations(int n, int k) {
        int res;
        res = 0;

        if (n < 1) {
            res = 0;
        } else {
            if (k < 1) {
                res = 0;
            } else {
                if (n < k) {
                    res = 0;
                } else {
                    res = this.factorial(n) / this.factorial(n - k);
                }

            }
        }

        return res;
    }

    public int combinations(int n, int k) {
        int res;
        res = 0;

        if (n < 1) {
            res = 0;
        } else {
            if (k < 1) {
                res = 0;
            } else {
                if (n < k) {
                    res = 0;
                } else {
                    res = this.factorial(n) / (this.factorial(k) * this.factorial(n - k));
                }

            }
        }

        return res;
    }

    public int triangleArea(int base, int height) {
        int res;
        res = 0;

        if (base < 0) {
            res = 0;
        } else {
            if (height < 0) {
                res = 0;
            } else {
                res = base * height / 2;
            }
        }

        return res;
    }

    public int cubeVolume(int side) {
        int res;
        res = 0;

        if (side < 0) {
            res = 0;
        } else {
            res = this.pow(side, 3);
        }

        return res;
    }
}