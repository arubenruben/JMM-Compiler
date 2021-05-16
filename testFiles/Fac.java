import io;

class Conversor {

    public int millimetersToCentimeters(int value) {
        return value / 10;
    }

    public int millimetersToDecimeters(int value) {
        return value / 100;
    }

    public int millimetersToMeters(int value) {
        return value / 1000;
    }

    public int metersToDecimeters(int value) {
        return value * 10;
    }

    public int metersToCentimeters(int value) {
        return value * 100;
    }

    public int metersToMillimeters(int value) {
        return value * 1000;
    }


    public static void main(String[] args) {
        io.println(
                metersToDecimeters(
                        metersToCentimeters(
                                metersToMillimeters(
                                        millimetersToCentimeters(
                                                millimetersToDecimeters(
                                                        millimetersToMeters(1000)
                                                )
                                        )
                                )
                        )
                )
        );
    }
}