package com.smart.bugrca.utility;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
@Data
@Component
public class RcaSections {
    private String rca;
    private String impact;
    private String resolution;


    public void setRacVariables(String aiRca){
        this.rca = extract(aiRca, "1\\. Root Cause Analysis \\(RCA\\)", "2\\. Impact");
        this.impact = extract(aiRca, "2\\. Impact", "3\\. Resolution steps");
        this.resolution = extract(aiRca, "3\\. Resolution steps", null);
    }

    private String extract(String text, String start, String end) {
        String regex;

        if (end != null) {
            regex = start + "(.*?)" + end;
        } else {
            regex = start + "(.*)";
        }

        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "";
    }
}