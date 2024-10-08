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

import edu.hm.hafner.analysis.IssueParser;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import io.jenkins.plugins.analysis.core.model.ReportScanningTool;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * @deprecated Replaced by {@code yoctoScanner()} of <i>warnings-ng</i>; will be removed in v0.6.0
 */
@Deprecated(forRemoval = true, since = "0.5.2")
public class CveScanner extends ReportScanningTool {
    static final String ID = "cve-scanner";

    @DataBoundConstructor
    public CveScanner() {
    }

    @Override
    public IssueParser createParser() {
        return new CveScanParser();
    }

    @Extension
    public static class Descriptor extends ReportScanningToolDescriptor {
        public Descriptor() {
            super(ID);
        }

        @NonNull
        @Override
        public String getDisplayName() {
            return "CVE Scanner";
        }
    }
}
