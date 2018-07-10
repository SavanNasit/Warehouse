package com.accrete.sixorbit.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by agt on 25/12/17.
 */

public class PercentageValidator {
    // Percentage Regex java
    private static final String PERCENTAGE_REGEX = "(^100(\\.0{1,2})?$)|(^([1-9]([0-9])?|0)(\\.[0-9]{1,2})?$)\n";

    // static Pattern object, since pattern is fixed
    private static Pattern pattern;

    // non-static Matcher object because it's created from the input String
    private Matcher matcher;

    public PercentageValidator() {
        // initialize the Pattern object
        pattern = Pattern.compile(PERCENTAGE_REGEX, Pattern.CASE_INSENSITIVE);
    }

    /**
     * This method validates the input percentage with PERCENTAGE_REGEX pattern
     *
     * @param discountPercentage
     * @return boolean
     */
    public boolean validateDiscountPercentage(String discountPercentage) {
        matcher = pattern.matcher(discountPercentage);
        return matcher.matches();
    }
}
