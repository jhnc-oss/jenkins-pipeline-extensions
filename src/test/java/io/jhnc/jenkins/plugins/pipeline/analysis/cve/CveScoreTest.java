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
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CveScoreTest {
    @Test
    void validScores() {
        final CveScore both = new CveScore("4.2", "3.4");
        assertThat(both.getScoreV3()).isEqualTo("4.2");
        assertThat(both.getScoreV2()).isEqualTo("3.4");

        final CveScore trimmed = new CveScore("   3.7 ", " 5.1   ");
        assertThat(trimmed.getScoreV3()).isEqualTo("3.7");
        assertThat(trimmed.getScoreV2()).isEqualTo("5.1");

        final CveScore onlyV3 = new CveScore("1.2", "0.0");
        assertThat(onlyV3.getScoreV3()).isEqualTo("1.2");
        assertThat(onlyV3.getScoreV2()).isEqualTo("0.0");

        final CveScore onlyV2 = new CveScore("0.0", "2.3");
        assertThat(onlyV2.getScoreV3()).isEqualTo("0.0");
        assertThat(onlyV2.getScoreV2()).isEqualTo("2.3");
    }

    @Test
    void invalidScoreThrows() {
        assertThrows(IllegalArgumentException.class, () -> new CveScore("", ""));
        assertThrows(IllegalArgumentException.class, () -> new CveScore("2.0", "-3.0"));
        assertThrows(IllegalArgumentException.class, () -> new CveScore("a", "1.0"));
        assertThrows(IllegalArgumentException.class, () -> new CveScore("10.1", "1.0"));
        assertThrows(IllegalArgumentException.class, () -> new CveScore("11.0", "1.0"));
    }

    @Test
    void severityUsesScoreV3First() {
        assertThat(new CveScore("0.1", "5.0").toSeverity()).isEqualTo(Severity.WARNING_LOW);
        assertThat(new CveScore("3.9", "5.0").toSeverity()).isEqualTo(Severity.WARNING_LOW);
        assertThat(new CveScore("4.0", "5.0").toSeverity()).isEqualTo(Severity.WARNING_NORMAL);
        assertThat(new CveScore("6.9", "5.0").toSeverity()).isEqualTo(Severity.WARNING_NORMAL);
        assertThat(new CveScore("7.0", "5.0").toSeverity()).isEqualTo(Severity.WARNING_HIGH);
        assertThat(new CveScore("8.9", "5.0").toSeverity()).isEqualTo(Severity.WARNING_HIGH);
        assertThat(new CveScore("9.0", "5.0").toSeverity()).isEqualTo(Severity.ERROR);
        assertThat(new CveScore("10.0", "5.0").toSeverity()).isEqualTo(Severity.ERROR);
    }

    @Test
    void severityUsesScoreV2Second() {
        assertThat(new CveScore("0.0", "0.0").toSeverity()).isEqualTo(Severity.WARNING_LOW);
        assertThat(new CveScore("0.0", "3.9").toSeverity()).isEqualTo(Severity.WARNING_LOW);
        assertThat(new CveScore("0.0", "4.0").toSeverity()).isEqualTo(Severity.WARNING_NORMAL);
        assertThat(new CveScore("0.0", "6.9").toSeverity()).isEqualTo(Severity.WARNING_NORMAL);
        assertThat(new CveScore("0.0", "7.0").toSeverity()).isEqualTo(Severity.WARNING_HIGH);
        assertThat(new CveScore("0.0", "10.0").toSeverity()).isEqualTo(Severity.WARNING_HIGH);
    }

}