package com.jonaslasauskas.gradle.plugin;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.truth.FailureStrategy;
import com.google.common.truth.StringSubject;
import com.google.common.truth.Subject;
import com.google.common.truth.SubjectFactory;
import com.jonaslasauskas.gradle.plugin.ExecutableJar.Execution;



public final class ExecutionSubject extends Subject<ExecutionSubject, Execution> {
  
  public ExecutionSubject(FailureStrategy failureStrategy, Execution actual) {
    super(failureStrategy, actual);
  }
  
  public StringSubject standardError() {
    return assertThat(actual().error);
  }
  
  public StringSubject standardOutput() {
    return assertThat(actual().output);
  }
  
  
  private static final SubjectFactory<ExecutionSubject, Execution> factory = new SubjectFactory<ExecutionSubject, Execution>() {
    @Override public ExecutionSubject getSubject(FailureStrategy fs, Execution that) {
      return new ExecutionSubject(fs, that);
    }
  };
  
  
  public static SubjectFactory<ExecutionSubject, Execution> execution() {
    return factory;
  }
  
}
