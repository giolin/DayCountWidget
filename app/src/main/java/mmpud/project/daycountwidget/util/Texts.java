package mmpud.project.daycountwidget.util;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;

public class Texts {

    /**
     * For widget, to resize the digit parts of the string.
     *
     * @param str
     * @return
     */
    public static Spannable getResizedText(String str) {
        int i = 0;
        while (!Character.isDigit(str.charAt(i))) {
            i++;
        }
        int j = i;
        while (Character.isDigit(str.charAt(j))) {
            j++;
        }
        int numOfDigits = j - i;
        Spannable span = new SpannableString(str);
        span.setSpan(new RelativeSizeSpan(getSizeSpanByDigitNum(numOfDigits)), i, j,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return span;
    }

    /**
     * Get the resize ratio according to the number of digits.
     *
     * @param numOfDigits
     * @return
     */
    private static float getSizeSpanByDigitNum(int numOfDigits) {
        switch (numOfDigits) {
        case 0: {
            return 3f;
        }
        case 1: {
            return 3f;
        }
        case 2: {
            return 3f;
        }
        case 3: {
            return 3f;
        }
        case 4: {
            return 2.5f;
        }
        case 5: {
            return 2f;
        }
        default: {
            return 2f;
        }
        }
    }

    private Texts() {}

}
