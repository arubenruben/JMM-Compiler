import io;

class Convertor {

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
        Convertor convertor_aux;

        convertor_aux = new Convertor();

        io.println(convertor_aux.metersToDecimeters(convertor_aux.metersToCentimeters(convertor_aux.metersToMillimeters(convertor_aux.millimetersToCentimeters(convertor_aux.millimetersToDecimeters(convertor_aux.millimetersToMeters(1000)))))));
    }
}