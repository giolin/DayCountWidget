package mmpud.project.daycountwidget.util;

public class Texts {

    /**
     * Get proper text size according to number of digits of the input number.
     *
     * @param number
     * @return
     */
    public static int getTextSizeDpByDigits(int number) {
        int num = Math.abs(number);
        if (num <= 999) {
            return 30;
        } else if (num > 999 && num <= 9999) {
            return 26;
        } else if (num > 9999 && num < 99999) {
            return 22;
        }
        return 18;
    }

    private Texts() {
        throw new RuntimeException("Utils is not instantiable.");
    }

}
