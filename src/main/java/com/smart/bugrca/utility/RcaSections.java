package com.smart.bugrca.utility;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
@Data
public class RcaSections{

    public static RcaSections parse(String aiRca) {
        RcaSections r = new RcaSections();

        r.rca = extract(aiRca, "1\\. Root Cause Analysis \\(RCA\\)", "2\\. Impact");
        r.impact = extract(aiRca, "2\\. Impact", "3\\. Resolution steps\\s");
        r.resolution = extract(aiRca, "3\\. Resolution steps\\s", null);

        return r;
    }

    private static String extract(String text, String start, String end) {
        String regex = (end != null) ? start + "(.*?)" + end : start + "(.*)";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        return matcher.find() ? matcher.group(1).trim() : "";
    }

    private String rca;
    private String impact;
    private String resolution;
}