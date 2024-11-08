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

package io.jhnc.jenkins.plugins.pipeline.steps;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.AbortException;
import hudson.Extension;
import hudson.model.TaskListener;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.Serial;
import java.util.Collections;
import java.util.Set;

public class CheckStep extends Step {
    private final boolean condition;
    private final String message;

    @DataBoundConstructor
    public CheckStep(boolean condition, @CheckForNull String message) {
        this.condition = condition;
        this.message = message == null ? "< No Message (null) >" : message;
    }

    public boolean isCondition() {
        return condition;
    }

    @NonNull
    public String getMessage() {
        return message;
    }

    @Override
    public StepExecution start(StepContext stepContext) {
        return new Execution(stepContext, condition, message);
    }


    public static class Execution extends SynchronousStepExecution<Void> {
        @Serial
        private static final long serialVersionUID = 1L;
        @SuppressFBWarnings(value = "SE_TRANSIENT_FIELD_NOT_RESTORED", justification = "Only used when starting.")
        private transient final boolean condition;
        @SuppressFBWarnings(value = "SE_TRANSIENT_FIELD_NOT_RESTORED", justification = "Only used when starting.")
        private transient final String message;

        public Execution(@NonNull StepContext context, boolean condition, @NonNull String message) {
            super(context);
            this.condition = condition;
            this.message = message;
        }

        @Override
        protected Void run() throws Exception {
            if (!condition) {
                getContext().get(TaskListener.class).error(message);
                throw new AbortException(message);
            }
            return null;
        }
    }


    @Symbol("check")
    @Extension
    public static class DescriptorImpl extends StepDescriptor {
        @NonNull
        @Override
        public String getDisplayName() {
            return Messages.CheckStep_displayName();
        }

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return Collections.singleton(TaskListener.class);
        }

        @Override
        public String getFunctionName() {
            return "check";
        }
    }
}
