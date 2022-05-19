package com.example.taxi.helpers;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ValidateHelper {

    public static final Pattern VALID_PHONE_NUMBER_REGEX =
            Pattern.compile("^\\d{11}$");


    public boolean validatePhoneNumber (String phoneNumber) {
        Matcher matcher = VALID_PHONE_NUMBER_REGEX.matcher(phoneNumber);

        return (matcher.find());
    }
}