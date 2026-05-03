package com.smart.bugrca;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RcaMain {

        public static void main(String[] args) {

            String aiRca = "\tAs an experienced Full Stack Engineer, I have analyzed the incident regarding the SSN field validation failure. Below is the technical breakdown, impact analysis, and resolution plan.\n" +
                    "\n" +
                    "---\n" +
                    "\n" +
                    "### 1. Root Cause Analysis (RCA)\n" +
                    "\n" +
                    "The root cause is a **multi-layered validation failure** across the application stack. Specifically:\n" +
                    "\n" +
                    "*   **Frontend (AngularJS):** The `<input>` field for the SSN likely lacks the `maxlength` attribute and a restrictive `ng-pattern`. It is likely using a standard `type=\"text\"` without any client-side validation logic (form controllers or custom directives) to intercept non-numeric keystrokes.\n" +
                    "*   **Backend (Java):** The REST API endpoint/DTO (Data Transfer Object) receiving the payload does not have JSR-303/JSR-380 Bean Validation annotations (like `@Pattern` or `@Size`). Additionally, there is no manual validation logic in the Service layer to sanitize the input before processing.\n" +
                    "*   **Database Layer:** The database column (likely `VARCHAR`) was probably defined with a generic length (e.g., `VARCHAR(255)`) and lacks a `CHECK` constraint to enforce that the data is numeric and exactly 4 digits.\n" +
                    "\n" +
                    "---\n" +
                    "\n" +
                    "### 2. Impact\n" +
                    "\n" +
                    "*   **Data Integrity:** The \"Source of Truth\" is now corrupted with inconsistent data formats (alphanumeric, special characters, and varying lengths).\n" +
                    "*   **Downstream System Failure:** Insurance portals often sync with 3rd-party carriers or payroll systems via EDI (Electronic Data Interchange) or batch files. These systems strictly expect 4 numeric digits; sending \"A#123\" will cause file rejections or processing crashes.\n" +
                    "*   **Compliance & Security:** While this is \"Last 4 of SSN,\" storing unintended data in PII (Personally Identifiable Information) fields can raise flags during security audits or GDPR/CCPA compliance checks.\n" +
                    "*   **Reporting Errors:** SQL queries performing calculations or filtering based on SSN will return inaccurate results or fail if they attempt to cast the column to a numeric type.\n" +
                    "\n" +
                    "---\n" +
                    "\n" +
                    "### 3. Resolution steps \n" +
                    "\n" +
                    "#### Phase 1: Frontend Fix (AngularJS)\n" +
                    "Update the HTML template to restrict input at the UI level.\n" +
                    "```html\n" +
                    "<!-- Implementation using ng-pattern for 4 digits only -->\n" +
                    "<input type=\"text\" \n" +
                    "       name=\"ssn\" \n" +
                    "       ng-model=\"employee.ssn\" \n" +
                    "       ng-pattern=\"/^\\d{4}$/\" \n" +
                    "       maxlength=\"4\" \n" +
                    "       required \n" +
                    "       class=\"form-control\">\n" +
                    "<span ng-show=\"form.ssn.$error.pattern\" class=\"text-danger\">\n" +
                    "    SSN must be exactly 4 numeric digits.\n" +
                    "</span>\n" +
                    "```\n" +
                    "\n" +
                    "#### Phase 2: Backend Fix (Java Spring/Java EE)\n" +
                    "Apply Bean Validation to the DTO to ensure the API rejects bad requests with a `400 Bad Request` status.\n" +
                    "```java\n" +
                    "public class EmployeeDTO {\n" +
                    "    @NotNull\n" +
                    "    @Pattern(regexp = \"^\\\\d{4}$\", message = \"SSN must be exactly 4 numeric digits\")\n" +
                    "    private String ssn;\n" +
                    "    \n" +
                    "    // getters and setters\n" +
                    "}\n" +
                    "```\n" +
                    "*Note: Ensure `@Valid` is used in the Controller method.*\n" +
                    "\n" +
                    "#### Phase 3: Data Sanitization (Database)\n" +
                    "Since this is a Production issue, we must clean the existing \"dirty\" data.\n" +
                    "1.  **Identify:** Run a script to find all records where SSN length != 4 or contains non-numeric characters.\n" +
                    "    ```sql\n" +
                    "    SELECT id, ssn FROM employees WHERE ssn NOT REGEXP '^[0-9]{4}$';\n" +
                    "    ```\n" +
                    "2.  **Correction:** Depending on business rules, either nullify these records, truncate them (if safe), or reach out to users for re-entry.\n" +
                    "3.  **Constraint:** Apply a check constraint to prevent future occurrences at the DB level.\n" +
                    "    ```sql\n" +
                    "    ALTER TABLE employees ADD CONSTRAINT chk_ssn_format CHECK (ssn REGEXP '^[0-9]{4}$');\n" +
                    "    ```\n" +
                    "\n" +
                    "#### Phase 4: Deployment & Verification\n" +
                    "1.  **Hotfix Deployment:** Deploy the backend validation first (to stop the leak), followed by the frontend UI changes.\n" +
                    "2.  **Regression Testing:** Verify that other employee data modules are not affected.\n" +
                    "3.  **Audit:** Verify that the integration logs for downstream insurance carriers are clear of \"Invalid Format\" errors.\n";

            String rca = extract(aiRca, "1\\. Root Cause Analysis \\(RCA\\)", "2\\. Impact");
            String impact = extract(aiRca, "2\\. Impact", "3\\. Resolution steps");
            String resolution = extract(aiRca, "3\\. Resolution steps", null);

            System.out.println("RCA:\n" + rca);
            System.out.println("Impact:\n" + impact);
            System.out.println("Resolution:\n" + resolution);
        }

        private static String extract(String text, String start, String end) {
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

