/*
 * MIT License
 *
 * Copyright (c) 2021-2026 jhnc-oss
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

package io.jhnc.jenkins.plugins.pipeline.model;

import hudson.model.StringParameterValue;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static com.google.common.truth.Truth.assertThat;

class HiddenChoiceParameterDefinitionTest {
    @Test
    void definitionValueAndCompatibility() {
        final HiddenChoiceParameterDefinition param = new HiddenChoiceParameterDefinition("paramName", "a\nb\nc", "description0");
        assertThat(param.getName()).isEqualTo("paramName");
        assertThat(param.getDescription()).isEqualTo("description0");
        assertThat(param.getChoices()).isEqualTo(Arrays.asList("a", "b", "c"));
        assertThat(param.createValue("b")).isInstanceOf(StringParameterValue.class);
    }

    @Test
    void definitionValueFromArray() {
        final String[] choices = new String[]{"1", "2", "3"};
        final HiddenChoiceParameterDefinition param = new HiddenChoiceParameterDefinition("paramName", choices, "description1");
        assertThat(param.getName()).isEqualTo("paramName");
        assertThat(param.getDescription()).isEqualTo("description1");
        assertThat(param.getChoices()).isEqualTo(Arrays.asList("1", "2", "3"));
        assertThat(param.createValue("3")).isInstanceOf(StringParameterValue.class);
    }
}