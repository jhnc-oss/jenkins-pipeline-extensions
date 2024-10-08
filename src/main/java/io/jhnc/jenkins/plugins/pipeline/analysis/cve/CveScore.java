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

import edu.hm.hafner.analysis.Severity;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * @deprecated Replaced by {@code yoctoScanner()} of <i>warnings-ng</i>; will be removed in v0.6.0
 */
@Deprecated(forRemoval = true, since = "0.5.2")
public class CveScore {
    private final String scoreV3;
    private final String scoreV2;


    public CveScore(@NonNull String scoreV3, @NonNull String scoreV2) {
        this.scoreV3 = sanitize(scoreV3);
        this.scoreV2 = sanitize(scoreV2);
    }

    @NonNull
    public Severity toSeverity() {
        if (!scoreV3.equals("0.0")) {
            final float v3 = toFloat(scoreV3);

            if (inRange(v3, 0.1f, 3.9f)) {
                return Severity.WARNING_LOW;
            } else if (inRange(v3, 4.0f, 6.9f)) {
                return Severity.WARNING_NORMAL;
            } else if (inRange(v3, 7.0f, 8.9f)) {
                return Severity.WARNING_HIGH;
            } else {
                return Severity.ERROR;
            }
        } else {
            final float v2 = toFloat(scoreV2);
            if (inRange(v2, 0.0f, 3.9f)) {
                return Severity.WARNING_LOW;
            } else if (inRange(v2, 4.0f, 6.9f)) {
                return Severity.WARNING_NORMAL;
            } else {
                return Severity.WARNING_HIGH;
            }
        }
    }

    @NonNull
    public String getScoreV3() {
        return scoreV3;
    }

    @NonNull
    public String getScoreV2() {
        return scoreV2;
    }

    private String sanitize(String value) {
        String newValue = value.trim();
        if (newValue.isEmpty() || !inRange(toFloat(newValue), 0.0f, 10.0f)) {
            throw new IllegalArgumentException("Invalid CVSS: '" + value + "'");
        }
        return newValue;
    }

    private boolean inRange(float value, float min, float max) {
        return (value >= min) && (value <= max);
    }

    private float toFloat(String value) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid CVSS value: '" + value + "'");
        }
    }
}
