package com.example.frontend.util;

import org.springframework.web.util.HtmlUtils;

public class Sanitizer {
    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        return HtmlUtils.htmlEscape(input);
    }
} 