package com.accrete.sixorbit.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by agt on 19/12/17.
 */

public class PANValidator {

    // PAN Regex java
    private static final String PAN_REGEX = "[A-Z]{5}[0-9]{4}[A-Z]{1}";

    // static Pattern object, since pattern is fixed
    private static Pattern pattern;

    // non-static Matcher object because it's created from the input String
    private Matcher matcher;

    public PANValidator() {
        // initialize the Pattern object
        pattern = Pattern.compile(PAN_REGEX, Pattern.CASE_INSENSITIVE);
    }

    /**
     * This method validates the input email address with PAN_REGEX pattern
     *
     * @param pan
     * @return boolean
     */
    public boolean validatePAN(String pan) {
        matcher = pattern.matcher(pan);
        return matcher.matches();
    }
}
