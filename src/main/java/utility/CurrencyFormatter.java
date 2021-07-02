package utility;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class CurrencyFormatter {

    //A function to convert currency formatted string to bigDecimal
    public static BigDecimal parseCurrency(final String amount, final Locale locale) throws ParseException {
        final NumberFormat format = NumberFormat.getNumberInstance(locale);
        if (format instanceof DecimalFormat) {
            ((DecimalFormat) format).setParseBigDecimal(true);
        }
        return (BigDecimal) format.parse(amount.replaceAll("[^\\d.,]",""));
    }

    /*public static void main(String[] args) throws ParseException {
        final String dollarsA = "$199.00";
        final String real = "R$ 399,00";
        final String dollarsB = "£25.00";
        final String tailingEuro = "90,83 €";
        final String dollarsC = "$199.00";
        final String dirham = "AED 449.00";
        final String fromjson = 	"$1,214.44";

        System.out.println(parseCurrency(dollarsA, Locale.US));
        System.out.println(parseCurrency(real, Locale.FRANCE));
        System.out.println(parseCurrency(dollarsB, Locale.US));
        System.out.println(parseCurrency(tailingEuro, Locale.FRANCE));
        System.out.println(parseCurrency(dollarsC, Locale.US));
        System.out.println(parseCurrency(dirham, Locale.US));
        System.out.println(parseCurrency(fromjson, Locale.US));
    }*/

}
