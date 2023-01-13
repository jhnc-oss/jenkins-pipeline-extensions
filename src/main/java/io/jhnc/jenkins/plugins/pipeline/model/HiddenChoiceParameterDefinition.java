/*
 * MIT License
 *
 * Copyright (c) 2021-2023 jhnc-oss
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

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.ChoiceParameterDefinition;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

public class HiddenChoiceParameterDefinition extends ChoiceParameterDefinition {
    private static final long serialVersionUID = 1L;


    @DataBoundConstructor
    public HiddenChoiceParameterDefinition(@NonNull String name, @NonNull String choices, @CheckForNull String description) {
        super(name, choices, description);
    }

    public HiddenChoiceParameterDefinition(@NonNull String name, @NonNull String[] choices, @CheckForNull String description) {
        super(name, choices, description);
    }


    @Extension
    @Symbol({"hiddenChoice"})
    public static class DescriptorImpl extends ChoiceParameterDefinition.DescriptorImpl {
        @NonNull
        @Override
        public String getDisplayName() {
            return Messages.HiddenChoiceParameterDefinition_displayName();
        }
    }
}
