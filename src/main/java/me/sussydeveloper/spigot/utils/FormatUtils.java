package me.sussydeveloper.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class FormatUtils {

    public static String FloatFormat(float value){
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance());
        return decimalFormat.format(value);
    }
}
