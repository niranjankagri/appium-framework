package com.automation.appium.base;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

/**
 * Gives every test the {@link RetryOnDeviceProblem} analyzer, so no test has
 * to name it. Registered in testng.xml and known-bugs.xml.
 */
public class RetryTransformer implements IAnnotationTransformer {

	// Raw Class and Constructor because TestNG's interface declares them that way
	@Override
	@SuppressWarnings("rawtypes")
	public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor,
			Method testMethod) {
		annotation.setRetryAnalyzer(RetryOnDeviceProblem.class);
	}
}
