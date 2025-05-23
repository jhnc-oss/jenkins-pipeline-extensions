/*
 * MIT License
 *
 * Copyright (c) 2021-2025 jhnc-oss
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

import hudson.model.Result;
import io.jhnc.jenkins.plugins.pipeline.WorkflowTestRunner;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.jenkinsci.plugins.workflow.steps.StepConfigTester;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
public class CheckStepTest {

    @Test
    public void checkStepFailsOnFalseCondition(TestInfo info, JenkinsRule r) throws Exception {
        final WorkflowRun build = WorkflowTestRunner.executeScript(r, info.getDisplayName(),
                "check(condition: false, message: 'should fail here')");
        r.assertBuildStatus(Result.FAILURE, build);
        r.assertLogContains("should fail here", build);
    }

    @Test
    public void checkStepDoesNotFailOnTrueCondition(TestInfo info, JenkinsRule r) throws Exception {
        final WorkflowRun build = WorkflowTestRunner.executeScript(r, info.getDisplayName(),
                "check(condition: true, message: 'should not fail here')");
        r.assertBuildStatus(Result.SUCCESS, build);
        r.assertLogNotContains("should not fail here", build);
    }

    @Test
    public void checkStepIsSafeToNullMessage(TestInfo info, JenkinsRule r) throws Exception {
        final WorkflowRun build = WorkflowTestRunner.executeScript(r, info.getDisplayName(), "check(condition: false, message: null)");
        r.assertBuildStatus(Result.FAILURE, build);
        r.assertLogContains("< No Message (null) >", build);
    }

    @Test
    public void configRoundTrip(JenkinsRule r) throws Exception {
        final CheckStep step = new CheckStep(true, "x");
        final CheckStep step2 = new StepConfigTester(r).configRoundTrip(step);
        r.assertEqualDataBoundBeans(step, step2);
    }

}