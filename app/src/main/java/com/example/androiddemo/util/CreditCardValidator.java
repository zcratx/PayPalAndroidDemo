package com.example.androiddemo.util;

public class CreditCardValidator {

    public static boolean isValidCardNumber(String cardNumber) {
        int sum = 0;
        boolean alternate = false;

        // Reverse the card number and iterate over each digit
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));

            // Double every alternate digit
            if (alternate) {
                digit *= 2;
                // If the doubled value is greater than 9, subtract 9
                if (digit > 9) {
                    digit -= 9;
                }
            }

            // Add the digit to the sum
            sum += digit;

            // Toggle the flag for alternate digit
            alternate = !alternate;
        }

        // Check if the sum is divisible by 10
        return sum % 10 == 0;
    }

    public static boolean checkCVV(String CVV) {
        boolean isValid = false;
        // contains all integers
        try {
            Integer.parseInt(CVV);
        } catch(Exception e) {
            return isValid;
        }

        //this holds true for VISA, MASTERCARD, DISCOVER and AMERICAN EXPRESS
        if (CVV.length() == 3 || CVV.length() == 4) {
            isValid = true;
        }

        return isValid;
    }

    /*
     * @return boolean of whether or not the card is expired
     */
    public static boolean checkExpirationDate(int expirationMonth, int expirationYear) {
        boolean isValid = false;

        if (DateChecker.compareDates(expirationYear, DateChecker.CURRENT_YEAR)) {
            isValid = true;
        } else if (DateChecker.compareDates(expirationMonth, DateChecker.CURRENT_MONTH)){
            isValid = true;
        }

        return isValid;
    }

}
