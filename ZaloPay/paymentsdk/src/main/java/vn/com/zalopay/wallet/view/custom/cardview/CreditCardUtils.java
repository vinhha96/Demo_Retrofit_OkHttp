package vn.com.zalopay.wallet.view.custom.cardview;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.Calendar;

/**
 * Created by Harish on 03/01/16.
 */
public class CreditCardUtils {

    public static final int MAX_LENGTH_CARD_NUMBER_WITH_SPACES = 19;
    public static final int MAX_LENGTH_CARD_NUMBER = 16;

    public static final int MAX_LENGTH_CARD_HOLDER_NAME = 16;

    public static final String EXTRA_CARD_NUMBER = "card_number";
    public static final String EXTRA_CARD_CVV = "card_cvv";
    public static final String EXTRA_CARD_EXPIRY = "card_expiry";
    public static final String EXTRA_CARD_ISSUE = "card_issue";
    public static final String EXTRA_CARD_HOLDER_NAME = "card_holder_name";
    public static final String EXTRA_CARD_SHOW_CARD_SIDE = "card_side";
    public static final String EXTRA_VALIDATE_EXPIRY_DATE = "expiry_date";
    public static final String EXTRA_VALIDATE_ISSUE_DATE = "issue_date";


    public static final int CARD_SIDE_FRONT = 1, CARD_SIDE_BACK = 0;

    public static final String SPACE_SEPERATOR = " ";
    public static final String DOUBLE_SPACE_SEPERATOR = "  ";

    public static final String SLASH_SEPERATOR = "/";
    public static final char CHAR_X = 'X';

    public static final String PASSWORD = "●";

    public static String handleCardNumber(String inputCardNumber) {

        return handleCardNumber(inputCardNumber, SPACE_SEPERATOR);
    }

    public static String handleCardCVV(String pCardCVV) {
        if (TextUtils.isEmpty(pCardCVV))
            return "";

        StringBuilder cardCVV = new StringBuilder();

        for (int i = 0; i < pCardCVV.length(); i++) {
            cardCVV.append(PASSWORD);
        }

        return cardCVV.toString();
    }


    public static String handleCardNumber(String inputCardNumber, String seperator) {

        String formattingText = inputCardNumber.replace(seperator, "");
        String text;

        if (formattingText.length() >= 4) {

            text = formattingText.substring(0, 4);

            if (formattingText.length() >= 8) {
                text += seperator + formattingText.substring(4, 8);
            } else if (formattingText.length() > 4) {
                text += seperator + formattingText.substring(4);
            }

            if (formattingText.length() >= 12) {
                text += seperator + formattingText.substring(8, 12);
            } else if (formattingText.length() > 8) {
                text += seperator + formattingText.substring(8);
            }

            if (formattingText.length() >= 16) {
                text += seperator + formattingText.substring(12, 16);
            } else if (formattingText.length() > 12) {
                text += seperator + formattingText.substring(12);
            }
            if (formattingText.length() >= 20) {
                text += seperator + formattingText.substring(16);
            } else if (formattingText.length() > 16) {
                text += seperator + formattingText.substring(16);
            }

            return text;

        } else {
            text = formattingText.trim();
        }

        return text;
    }


    public static String handleExpiration(String month, String year) {

        return handleExpiration(month + year);
    }


    public static String handleExpiration(@NonNull String dateYear) {
        // String expiryString = dateYear.replace(SLASH_SEPERATOR, "");

        String text;
        if (dateYear.length() > 3 && dateYear.contains(String.valueOf(SPACE_SEPERATOR))) {
            String mm = dateYear.substring(0, 2);
            String yy;
            text = mm;
            if (dateYear.contains(String.valueOf(SPACE_SEPERATOR)) && dateYear.length() >= 5) {
                String expiryString = dateYear.replace(SLASH_SEPERATOR, "");
                yy = expiryString.substring(2, 4);
                try {
                    Integer.parseInt(yy);
                } catch (Exception e) {

                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    yy = String.valueOf(year).substring(2);
                }

                text = mm + SLASH_SEPERATOR + yy;

            } else if (dateYear.length() > 3) {
                yy = dateYear.substring(3);
                text = mm + SLASH_SEPERATOR + yy;
            }
        } else {
            text = dateYear;

        }
        return text;
    }
}
