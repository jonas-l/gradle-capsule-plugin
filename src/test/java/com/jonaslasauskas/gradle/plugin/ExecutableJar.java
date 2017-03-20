package com.jonaslasauskas.gradle.plugin;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.CharStreams.copy;
import static com.google.common.truth.Truth.assertThat;
import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Joiner;



public final class ExecutableJar {
  
  public static ExecutableJar at(File jarFile) {
    File directory = jarFile.getParentFile();
    assertThat(directory.list()).asList().named("Files at %s", directory).contains(jarFile.getName());
    
    return new ExecutableJar(jarFile);
  }
  
  
  private final String jarFilePath;
  
  private final List<String> systemProperties;
  
  private final Map<String, String> environmentVariables;
  
  
  public ExecutableJar(File jarFile) {
    jarFilePath = jarFile.getAbsolutePath();
    systemProperties = new ArrayList<>();
    environmentVariables = new HashMap<>();
  }
  
  public ExecutableJar withSystemProperty(String property) {
    systemProperties.add("-D" + property);
    
    return this;
  }
  
  public ExecutableJar withSystemProperty(String property, String value) {
    systemProperties.add("-D" + property + "=" + value);
    
    return this;
  }
  
  public ExecutableJar withEnvironmentVariable(String name, String value) {
    environmentVariables.put(name, value);
    
    return this;
  }
  
  public Execution run() throws IOException {
    return runWithArguments();
  }
  
  public Execution runWithArguments(String... arguments) {
    List<String> command = jarCommand();
    command.addAll(asList(arguments));
    
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    processBuilder.environment().putAll(environmentVariables);
    return new Execution(processBuilder);
  }
  
  private List<String> jarCommand() {
    List<String> command = new ArrayList<>();
    command.add("java");
    command.addAll(systemProperties);
    command.add("-jar");
    command.add(jarFilePath);
    return command;
  }
  
  
  public static final class Execution {
    
    public final Iterable<String> command;
    
    public final int exitCode;
    
    public final String output;
    
    public final String error;
    
    
    public Execution(ProcessBuilder processBuilder) {
      command = processBuilder.command();
      
      try {
        Process process = processBuilder.start();
        
        try (
            Reader outputReader = new InputStreamReader(process.getInputStream(), UTF_8);
            Reader errorReader = new InputStreamReader(process.getErrorStream(), UTF_8)) {
          
          ensureCompletesIn(process, 42, SECONDS);
          
          StringBuilder outputBuilder = new StringBuilder();
          copy(outputReader, outputBuilder);
          
          StringBuilder errorBuilder = new StringBuilder();
          copy(errorReader, errorBuilder);
          
          exitCode = process.exitValue();
          output = outputBuilder.toString();
          error = errorBuilder.toString();
        } catch (InterruptedException e) {
          throw new RuntimeException("Execution failed.", e);
        }
      } catch (IOException e) {
        throw new RuntimeException("Execution did not start.", e);
      }
    }
    
    private void ensureCompletesIn(Process process, int duration, TimeUnit timeUnit) throws InterruptedException {
      if (!process.waitFor(duration, timeUnit)) {
        process.destroyForcibly();
        throw new RuntimeException(format("Execution of ''{0}'' didn''t finished in {1} {2}.", Joiner.on(" ").join(command), duration, timeUnit));
      }
    }
    
  }
  
}
