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

import edu.hm.hafner.analysis.IssueBuilder;
import edu.hm.hafner.analysis.Report;
import edu.hm.hafner.analysis.parser.JsonIssueParser;
import org.json.JSONObject;

/**
 * @deprecated Replaced by {@code yoctoScanner()} of <i>warnings-ng</i>; will be removed in v0.6.0
 */
@Deprecated(forRemoval = true, since = "0.5.2")
public class CveScanParser extends JsonIssueParser {
    @Override
    protected void parseJsonObject(Report report, JSONObject jsonReport, IssueBuilder issueBuilder) {
        for (Object entryObj : jsonReport.getJSONArray("package")) {
            final JSONObject entry = (JSONObject) entryObj;

            for (Object issueObj : entry.getJSONArray("issue")) {
                final JSONObject issue = (JSONObject) issueObj;

                if (!ignoreEntry(issue.getString("status"))) {
                    final CveScore score = new CveScore(issue.getString("scorev3"), issue.getString("scorev2"));

                    report.add(issueBuilder
                            .setFileName(entry.getString("name") + " " + entry.getString("version"))
                            .setModuleName(entry.getString("layer"))
                            .setSeverity(score.toSeverity())
                            .setMessage(formatMessage(score, issue.getString("summary")))
                            .setType(issue.getString("vector"))
                            .setCategory(issue.getString("id"))
                            .setDescription(issue.getString("link"))
                            .buildAndClean());
                }
            }
        }
    }

    private String formatMessage(CveScore score, String text) {
        return "Score: " + score.getScoreV3() + " (v3) / " + score.getScoreV2() + " (v2) — " + text;
    }

    private boolean ignoreEntry(String status) {
        return status.equals("Patched") || status.equals("Ignored");
    }
}
