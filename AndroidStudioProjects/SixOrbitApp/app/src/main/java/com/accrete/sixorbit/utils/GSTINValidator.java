package com.accrete.sixorbit.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by agt on 19/12/17.
 */

public class GSTINValidator {
    // GSTIN Regex java
    private static final String GSTIN_REGEX = "^([0][1-9]|[1-2][0-9]|[3][0-5])([a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9a-zA-Z]{1}[zZ]{1}[0-9a-zA-Z]{1})+$";

    // static Pattern object, since pattern is fixed
    private static Pattern pattern;

    // non-static Matcher object because it's created from the input String
    private Matcher matcher;

    public GSTINValidator() {
        // initialize the Pattern object
        pattern = Pattern.compile(GSTIN_REGEX, Pattern.CASE_INSENSITIVE);
    }

    /**
     * This method validates the input email address with GSTIN_REGEX pattern
     *
     * @param gstIn
     * @return boolean
     */
    public boolean validateGSTIN(String gstIn) {
        matcher = pattern.matcher(gstIn);
        return matcher.matches();
    }
}
