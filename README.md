# Jenkins Pipeline Extensions

[![ci](https://github.com/jhnc-oss/jenkins-pipeline-extensions/actions/workflows/ci.yml/badge.svg)](https://github.com/jhnc-oss/jenkins-pipeline-extensions/actions/workflows/ci.yml)
[![GitHub release](https://img.shields.io/github/release/jhnc-oss/jenkins-pipeline-extensions.svg)](https://github.com/jhnc-oss/jenkins-pipeline-extensions/releases)
[![License](https://img.shields.io/badge/license-MIT-yellow.svg)](LICENSE)
![Java](https://img.shields.io/badge/java-17-green.svg)

Additional Pipeline features which aren't part of other plugins.

## Steps

### Fail

Prints the error message to the build log and fails the build.

```groovy
fail("error message")
fail(message: "error message")
```
**Note:** This step is matches [`error`](https://jenkins.io/doc/pipeline/steps/workflow-basic-steps/#error-error-signal) but will print the message to the log.


### Check

Fails the build if the condition is `false`.

```groovy
check(condition: a == b, message: "error message")
```

## Parameter

### Hidden parameter

Hidden parameter types which aren't available for user input. Supported types:

- hiddenString
- hiddenText
- hiddenBoolean
- hiddenChoice

Users with permission `app/administer` have access to those parameter types and may change it.
