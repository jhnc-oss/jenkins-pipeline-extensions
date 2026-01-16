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

package io.jhnc.jenkins.plugins.pipeline;

import edu.umd.cs.findbugs.annotations.NonNull;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.Objects;

public final class WorkflowTestRunner {
    private WorkflowTestRunner() {
    }

    public static WorkflowRun executeScript(@NonNull JenkinsRule rule, @NonNull String testName, @NonNull String script) throws Exception {
        final WorkflowJob project = rule.jenkins.createProject(WorkflowJob.class, testName);
        project.setDefinition(new CpsFlowDefinition(script, true));
        final WorkflowRun build = Objects.requireNonNull(project.scheduleBuild2(0)).waitForStart();
        rule.waitForCompletion(build);
        return build;
    }
}
