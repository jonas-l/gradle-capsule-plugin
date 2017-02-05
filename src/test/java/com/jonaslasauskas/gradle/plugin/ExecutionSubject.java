package com.jonaslasauskas.gradle.plugin;

import static com.google.common.truth.Truth.assert_;

import com.google.common.base.Joiner;
import com.google.common.truth.FailureStrategy;
import com.google.common.truth.StringSubject;
import com.google.common.truth.Subject;
import com.google.common.truth.SubjectFactory;
import com.google.common.truth.Truth;
import com.jonaslasauskas.gradle.plugin.ExecutableJar.Execution;



public final class ExecutionSubject extends Subject<ExecutionSubject, Execution> {
  
  public ExecutionSubject(FailureStrategy failureStrategy, Execution actual) {
    super(failureStrategy, actual);
  }
  
  public void succeeded() {
    int exitCode = actual().exitCode;
    if (exitCode != 0) {
      String command = Joiner.on(' ').join(actual().command);
      failWithRawMessage("Execution of '%s' was expected to succeed, but exited with code '%s' and reported the following errors:\n%s", command, exitCode, actual().error);
    }
  }
  
  public StringSubject standardError() {
    return Truth.assertThat(actual().error);
  }
  
  public StringSubject standardOutput() {
    return Truth.assertThat(actual().output);
  }
  
  
  private static final SubjectFactory<ExecutionSubject, Execution> factory = new SubjectFactory<ExecutionSubject, Execution>() {
    @Override public ExecutionSubject getSubject(FailureStrategy fs, Execution that) {
      return new ExecutionSubject(fs, that);
    }
  };
  
  
  public static SubjectFactory<ExecutionSubject, Execution> execution() {
    return factory;
  }
  
  public static ExecutionSubject assertThat(Execution execution) {
    return assert_().about(execution()).that(execution);
  }
  
}
