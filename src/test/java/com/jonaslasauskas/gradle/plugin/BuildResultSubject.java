package com.jonaslasauskas.gradle.plugin;

import static com.google.common.truth.Truth.assert_;
import static org.gradle.testkit.runner.TaskOutcome.FAILED;
import static org.gradle.testkit.runner.TaskOutcome.SKIPPED;
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.TaskOutcome;

import com.google.common.truth.FailureStrategy;
import com.google.common.truth.Subject;
import com.google.common.truth.SubjectFactory;
import com.google.common.truth.Truth;



/**
 * {@link Subject} to perform assertions on {@link BuildResult} using
 * {@link Truth} assertion framework.
 * 
 * @see Truth
 */
public final class BuildResultSubject extends Subject<BuildResultSubject, BuildResult> {
  
  public static BuildResultSubject assertThat(BuildResult build) {
    return assert_().about(buildResult()).that(build);
  }
  
  
  public BuildResultSubject(FailureStrategy failureStrategy, BuildResult actual) {
    super(failureStrategy, actual);
    this.named("build");
  }
  
  public Outcome task(String taskName) {
    BuildTask task = actual().task(taskName);
    if (task == null) {
      fail("performed task", taskName);
      return new Nonexistent();
    }
    
    return new Existent(taskName, task.getOutcome());
  }
  
  
  public interface Outcome {
    
    void succeeded();
    
    void failed();
    
    void wasUpToDate();
    
    void ignored();
    
  }
  
  private final class Existent implements Outcome {
    
    private final String taskName;
    
    private final TaskOutcome outcome;
    
    
    public Existent(String taskName, TaskOutcome outcome) {
      this.taskName = taskName;
      this.outcome = outcome;
    }
    
    @Override public void succeeded() {
      assertOutcomeWas(SUCCESS);
    }
    
    @Override public void failed() {
      assertOutcomeWas(FAILED);
    }
    
    @Override public void wasUpToDate() {
      assertOutcomeWas(UP_TO_DATE);
    }
    
    @Override public void ignored() {
      assertOutcomeWas(SKIPPED);
    }
    
    private void assertOutcomeWas(TaskOutcome expected) {
      if (!outcome.equals(expected)) {
        failWithCustomSubject("task " + taskName, expected.toString().toLowerCase(), outcome.toString().toLowerCase());
      }
    }
    
  }
  
  private final class Nonexistent implements Outcome {
    
    @Override public void succeeded() {}
    
    @Override public void failed() {}
    
    @Override public void wasUpToDate() {}
    
    @Override public void ignored() {}
    
  }
  
  
  private static final SubjectFactory<BuildResultSubject, BuildResult> factory = new SubjectFactory<BuildResultSubject, BuildResult>() {
    @Override public BuildResultSubject getSubject(FailureStrategy fs, BuildResult that) {
      return new BuildResultSubject(fs, that);
    }
  };
  
  
  public static SubjectFactory<BuildResultSubject, BuildResult> buildResult() {
    return factory;
  }
  
}
