/*
 * MIT License
 *
 * Copyright (c) 2021-2024 jhnc-oss
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.jhnc.jenkins.plugins.pipeline.analysis.cve;

import edu.hm.hafner.analysis.Issue;
import edu.hm.hafner.analysis.IssueBuilder;
import edu.hm.hafner.analysis.Report;
import edu.hm.hafner.analysis.Severity;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

class CveScanParserTest {
    private Report report;

    @BeforeEach
    void setup() {
        report = new Report();
    }

    @Test
    void noResultsAddsNothing() {
        new CveScanParser().parseJsonObject(report, buildJson(Collections.emptyList()), new IssueBuilder());

        assertThat(report).isEmpty();
    }

    @Test
    void noIssuesFoundAddsNothing() {
        new CveScanParser().parseJsonObject(report, buildJson(
                "    {\n" +
                        "      \"name\": \"libunittest\",\n" +
                        "      \"layer\": \"meta-test\",\n" +
                        "      \"version\": \"1.2.3\",\n" +
                        "      \"products\": [\n" +
                        "        {\n" +
                        "          \"product\": \"ignore\",\n" +
                        "          \"cvesInRecord\": \"Yes\"\n" +
                        "        }\n" +
                        "      ],\n" +
                        "      \"issue\": []\n" +
                        "}"), new IssueBuilder());

        assertThat(report).isEmpty();
    }

    @Test
    void foundIssueAddsData() {
        new CveScanParser().parseJsonObject(report, buildJson(
                "    {\n" +
                        "      \"name\": \"libunittest\",\n" +
                        "      \"layer\": \"meta-test\",\n" +
                        "      \"version\": \"1.2.3\",\n" +
                        "      \"products\": [\n" +
                        "        {\n" +
                        "          \"product\": \"ignore\",\n" +
                        "          \"cvesInRecord\": \"Yes\"\n" +
                        "        }\n" +
                        "      ],\n" +
                        "      \"issue\": [\n" +
                        "        {\n" +
                        "          \"id\": \"CVE-1234-5678\",\n" +
                        "          \"summary\": \"CVE message here.\",\n" +
                        "          \"scorev2\": \"6.8\",\n" +
                        "          \"scorev3\": \"5.3\",\n" +
                        "          \"vector\": \"NETWORK\",\n" +
                        "          \"status\": \"Unpatched\",\n" +
                        "          \"link\": \"https://link.to/CVE-1234-5678\"\n" +
                        "        },\n" +
                        "      ]\n" +
                        "}"), new IssueBuilder());

        assertThat(report).hasSize(1);
        assertIssueData(report.get(0), "libunittest 1.2.3",
                "meta-test",
                "CVE-1234-5678",
                "Score: 5.3 (v3) / 6.8 (v2) — CVE message here.",
                Severity.WARNING_NORMAL,
                "NETWORK",
                "https://link.to/CVE-1234-5678");
    }

    @Test
    void multipleFoundIssueAddsData() {
        new CveScanParser().parseJsonObject(report, buildJson(
                "    {\n" +
                        "      \"name\": \"libunittest\",\n" +
                        "      \"layer\": \"meta-test\",\n" +
                        "      \"version\": \"1.2.3\",\n" +
                        "      \"products\": [\n" +
                        "        {\n" +
                        "          \"product\": \"ignore\",\n" +
                        "          \"cvesInRecord\": \"Yes\"\n" +
                        "        }\n" +
                        "      ],\n" +
                        "      \"issue\": [\n" +
                        "        {\n" +
                        "          \"id\": \"CVE-1122-3344\",\n" +
                        "          \"summary\": \"CVE message here.\",\n" +
                        "          \"scorev2\": \"6.8\",\n" +
                        "          \"scorev3\": \"5.3\",\n" +
                        "          \"vector\": \"NETWORK\",\n" +
                        "          \"status\": \"Unpatched\",\n" +
                        "          \"link\": \"https://link.to/CVE-1122-3344\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"id\": \"CVE-5555-6666\",\n" +
                        "          \"summary\": \"Another CVE.\",\n" +
                        "          \"scorev2\": \"0.0\",\n" +
                        "          \"scorev3\": \"2.3\",\n" +
                        "          \"vector\": \"LOCAL\",\n" +
                        "          \"status\": \"Unpatched\",\n" +
                        "          \"link\": \"https://link.to/CVE-5555-6666\"\n" +
                        "        },\n" +
                        "      ]\n" +
                        "}"), new IssueBuilder());

        assertThat(report).hasSize(2);
        assertIssueData(report.get(0),
                "libunittest 1.2.3",
                "meta-test",
                "CVE-1122-3344",
                "Score: 5.3 (v3) / 6.8 (v2) — CVE message here.",
                Severity.WARNING_NORMAL,
                "NETWORK",
                "https://link.to/CVE-1122-3344");
        assertIssueData(report.get(1),
                "libunittest 1.2.3",
                "meta-test",
                "CVE-5555-6666",
                "Score: 2.3 (v3) / 0.0 (v2) — Another CVE.",
                Severity.WARNING_LOW,
                "LOCAL",
                "https://link.to/CVE-5555-6666");
    }

    @Test
    void patchedIssueIsIgnored() {
        new CveScanParser().parseJsonObject(report, buildJson(
                "    {\n" +
                        "      \"name\": \"libunittest\",\n" +
                        "      \"layer\": \"meta-test\",\n" +
                        "      \"version\": \"1.2.3\",\n" +
                        "      \"products\": [\n" +
                        "        {\n" +
                        "          \"product\": \"ignore\",\n" +
                        "          \"cvesInRecord\": \"Yes\"\n" +
                        "        }\n" +
                        "      ],\n" +
                        "      \"issue\": [\n" +
                        "        {\n" +
                        "          \"id\": \"CVE-1122-3344\",\n" +
                        "          \"summary\": \"CVE message here.\",\n" +
                        "          \"scorev2\": \"6.8\",\n" +
                        "          \"scorev3\": \"5.3\",\n" +
                        "          \"vector\": \"NETWORK\",\n" +
                        "          \"status\": \"Patched\",\n" +
                        "          \"link\": \"https://link.to/CVE-1122-3344\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"id\": \"CVE-5555-6666\",\n" +
                        "          \"summary\": \"Another CVE.\",\n" +
                        "          \"scorev2\": \"0.0\",\n" +
                        "          \"scorev3\": \"2.3\",\n" +
                        "          \"vector\": \"LOCAL\",\n" +
                        "          \"status\": \"Unpatched\",\n" +
                        "          \"link\": \"https://link.to/CVE-5555-6666\"\n" +
                        "        },\n" +
                        "      ]\n" +
                        "}"), new IssueBuilder());

        assertThat(report).hasSize(1);
        assertIssueData(report.get(0), "libunittest 1.2.3",
                "meta-test",
                "CVE-5555-6666",
                "Score: 2.3 (v3) / 0.0 (v2) — Another CVE.",
                Severity.WARNING_LOW,
                "LOCAL",
                "https://link.to/CVE-5555-6666");
    }

    @Test
    void ignoredIssueIsIgnored() {
        new CveScanParser().parseJsonObject(report, buildJson(
                "    {\n" +
                        "      \"name\": \"libunittest\",\n" +
                        "      \"layer\": \"meta-test\",\n" +
                        "      \"version\": \"1.2.3\",\n" +
                        "      \"products\": [\n" +
                        "        {\n" +
                        "          \"product\": \"ignore\",\n" +
                        "          \"cvesInRecord\": \"Yes\"\n" +
                        "        }\n" +
                        "      ],\n" +
                        "      \"issue\": [\n" +
                        "        {\n" +
                        "          \"id\": \"CVE-1122-3344\",\n" +
                        "          \"summary\": \"CVE message here.\",\n" +
                        "          \"scorev2\": \"6.8\",\n" +
                        "          \"scorev3\": \"5.3\",\n" +
                        "          \"vector\": \"NETWORK\",\n" +
                        "          \"status\": \"Unpatched\",\n" +
                        "          \"link\": \"https://link.to/CVE-1122-3344\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"id\": \"CVE-5555-6666\",\n" +
                        "          \"summary\": \"Another CVE.\",\n" +
                        "          \"scorev2\": \"0.0\",\n" +
                        "          \"scorev3\": \"2.3\",\n" +
                        "          \"vector\": \"LOCAL\",\n" +
                        "          \"status\": \"Ignored\",\n" +
                        "          \"link\": \"https://link.to/CVE-5555-6666\"\n" +
                        "        },\n" +
                        "      ]\n" +
                        "}"), new IssueBuilder());

        assertThat(report).hasSize(1);
        assertIssueData(report.get(0),
                "libunittest 1.2.3",
                "meta-test",
                "CVE-1122-3344",
                "Score: 5.3 (v3) / 6.8 (v2) — CVE message here.",
                Severity.WARNING_NORMAL,
                "NETWORK",
                "https://link.to/CVE-1122-3344");
    }

    @Test
    void multipleEntriesAddsData() {
        new CveScanParser().parseJsonObject(report, buildJson(List.of(
                "    {\n" +
                        "      \"name\": \"libunittest\",\n" +
                        "      \"layer\": \"meta-test\",\n" +
                        "      \"version\": \"1.2.3\",\n" +
                        "      \"products\": [\n" +
                        "        {\n" +
                        "          \"product\": \"ignore\",\n" +
                        "          \"cvesInRecord\": \"Yes\"\n" +
                        "        }\n" +
                        "      ],\n" +
                        "      \"issue\": [\n" +
                        "        {\n" +
                        "          \"id\": \"CVE-1122-3344\",\n" +
                        "          \"summary\": \"CVE message here.\",\n" +
                        "          \"scorev2\": \"6.8\",\n" +
                        "          \"scorev3\": \"5.3\",\n" +
                        "          \"vector\": \"NETWORK\",\n" +
                        "          \"status\": \"Unpatched\",\n" +
                        "          \"link\": \"https://link.to/CVE-1122-3344\"\n" +
                        "        },\n" +
                        "      ]\n" +
                        "    },\n",
                "    {\n" +
                        "      \"name\": \"example\",\n" +
                        "      \"layer\": \"meta-example\",\n" +
                        "      \"version\": \"0.1.2\",\n" +
                        "      \"products\": [\n" +
                        "        {\n" +
                        "          \"product\": \"ignore2\",\n" +
                        "          \"cvesInRecord\": \"Yes\"\n" +
                        "        }\n" +
                        "      ],\n" +
                        "      \"issue\": [\n" +
                        "        {\n" +
                        "          \"id\": \"CVE-1000-2020\",\n" +
                        "          \"summary\": \"Example issue.\",\n" +
                        "          \"scorev2\": \"0.0\",\n" +
                        "          \"scorev3\": \"9.0\",\n" +
                        "          \"vector\": \"NETWORK\",\n" +
                        "          \"status\": \"Unpatched\",\n" +
                        "          \"link\": \"https://link.to/CVE-1000-2020\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"id\": \"CVE-1000-3030\",\n" +
                        "          \"summary\": \"Example issue 2.\",\n" +
                        "          \"scorev2\": \"0.0\",\n" +
                        "          \"scorev3\": \"6.7\",\n" +
                        "          \"vector\": \"LOCAL\",\n" +
                        "          \"status\": \"Unpatched\",\n" +
                        "          \"link\": \"https://link.to/CVE-1000-3030\"\n" +
                        "        },\n" +
                        "      ]\n" +
                        "}")), new IssueBuilder());

        assertThat(report).hasSize(3);
        assertIssueData(report.get(0), "libunittest 1.2.3",
                "meta-test",
                "CVE-1122-3344",
                "Score: 5.3 (v3) / 6.8 (v2) — CVE message here.",
                Severity.WARNING_NORMAL,
                "NETWORK",
                "https://link.to/CVE-1122-3344");
        assertIssueData(report.get(1),
                "example 0.1.2",
                "meta-example",
                "CVE-1000-2020",
                "Score: 9.0 (v3) / 0.0 (v2) — Example issue.",
                Severity.ERROR,
                "NETWORK",
                "https://link.to/CVE-1000-2020");
        assertIssueData(report.get(2),
                "example 0.1.2",
                "meta-example",
                "CVE-1000-3030",
                "Score: 6.7 (v3) / 0.0 (v2) — Example issue 2.",
                Severity.WARNING_NORMAL,
                "LOCAL",
                "https://link.to/CVE-1000-3030");
    }

    private JSONObject buildJson(String entryJson) {
        return buildJson(List.of(entryJson));
    }

    private JSONObject buildJson(List<String> entriesJson) {
        final JSONObject json = new JSONObject("{\"version\": \"1\", \"package\": []}");
        entriesJson.forEach(entry -> json.getJSONArray("package").put(new JSONObject(entry)));
        return json;
    }

    private void assertIssueData(Issue issue, String fileName, String moduleName, String category,
                                 String message, Severity severity, String type, String description) {
        assertThat(issue.getFileName()).isEqualTo(fileName);
        assertThat(issue.getModuleName()).isEqualTo(moduleName);
        assertThat(issue.getCategory()).isEqualTo(category);
        assertThat(issue.getMessage()).isEqualTo(message);
        assertThat(issue.getSeverity()).isEqualTo(severity);
        assertThat(issue.getType()).isEqualTo(type);
        assertThat(issue.getDescription()).isEqualTo(description);
    }
}