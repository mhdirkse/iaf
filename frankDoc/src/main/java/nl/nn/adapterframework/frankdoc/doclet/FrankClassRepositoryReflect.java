/* 
Copyright 2021 WeAreFrank! 

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 

    http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and 
limitations under the License. 
*/

package nl.nn.adapterframework.frankdoc.doclet;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import nl.nn.adapterframework.util.LogUtil;

class FrankClassRepositoryReflect implements FrankClassRepository {
	private static Logger log = LogUtil.getLogger(FrankClassRepositoryReflect.class);

	private @Getter @Setter(AccessLevel.PACKAGE) Set<String> excludeFilters;
	private @Getter @Setter(AccessLevel.PACKAGE) Set<String> includeFilters;
	private @Getter @Setter(AccessLevel.PACKAGE) Set<String> excludeFiltersForSuperclass;

	@Override
	public FrankClass findClass(String fullName) throws FrankDocException {
		try {
			return new FrankClassReflect(Class.forName(fullName), this);
		} catch(ClassNotFoundException e) {
			String outerClassName = getOuterClassName(fullName);
			String simpleName = getSimpleName(fullName);
			String expectedInnerClassName = outerClassName + "$" + simpleName;
			try {
				Class<?> outerClazz = Class.forName(outerClassName);
				Class<?>[] innerClasses = outerClazz.getDeclaredClasses();
				for(Class<?> innerClass: innerClasses) {
					if(innerClass.getName().equals(expectedInnerClassName)) {
						return new FrankClassReflect(innerClass, this);
					}
				}
				throw new FrankDocException(String.format("Found outer class [%s] but it does not have inner class [%s]", outerClazz.getName(), simpleName), e);
			} catch(ClassNotFoundException innerException) {
				throw new FrankDocException(String.format("Could not find class [%s]", fullName), innerException);
			}
		}
	}

	private String getOuterClassName(String innerClassFullName) {
		return innerClassFullName.substring(0, innerClassFullName.lastIndexOf("."));
	}

	private static String getSimpleName(String innerClassFullName) {
		return innerClassFullName.substring(innerClassFullName.lastIndexOf(".") + 1);
	}

	Set<SpringBean> getSpringBeans(TypeFilter typeFilter) {
		Set<SpringBean> result = new HashSet<SpringBean>();
		BeanDefinitionRegistry beanDefinitionRegistry = new SimpleBeanDefinitionRegistry();
		ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanDefinitionRegistry);
		scanner.setIncludeAnnotationConfig(false);
		if(typeFilter != null) {
			scanner.addIncludeFilter(typeFilter);
		}
		BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator() {
			@Override
			protected String buildDefaultBeanName(BeanDefinition definition) {
				String beanClassName = definition.getBeanClassName();
				Assert.state(beanClassName != null, "No bean class name set");
				return beanClassName;
			}
		};
		scanner.setBeanNameGenerator(beanNameGenerator);
		Set<String> excludeFilters = new HashSet<>(getExcludeFilters());
		for (String excludeFilter : excludeFilters) {
			String filterRegex = excludeFilter.replaceAll("\\.", "\\\\.");
			addExcludeFilter(scanner, filterRegex);
		}
		boolean success = false;
		int maxTries = 100;
		int tryCount = 0;
		while (!success && tryCount < maxTries) {
			tryCount++;
			try {
				scanner.scan(new ArrayList<>(getIncludeFilters()).toArray(new String[] {}));
				success = true;
			} catch(BeanDefinitionStoreException e) {
				// Exclude errors like class java.lang.NoClassDefFoundError: com/tibco/tibjms/admin/TibjmsAdminException
				// for SendTibcoMessage. See menu item Errors in GUI.
				String excludeFilter = e.getMessage();
				excludeFilter = excludeFilter.substring(excludeFilter.indexOf(".jar!/") + 6);
				excludeFilter = excludeFilter.substring(0, excludeFilter.indexOf(".class"));
				excludeFilter = excludeFilter.replaceAll("/", "\\\\.");
				excludeFilter = excludeFilter.substring(0, excludeFilter.lastIndexOf('.') + 1) + ".*";
				excludeFilters.add(excludeFilter);
				addExcludeFilter(scanner, excludeFilter);
				if(log.isWarnEnabled()) {
					log.warn(excludeFilter, e);
				}
			}
		}
		String[] beans = beanDefinitionRegistry.getBeanDefinitionNames();
		for (int i = 0; i < beans.length; i++) {
			String name = beans[i];
			String className = beanDefinitionRegistry.getBeanDefinition(name).getBeanClassName();
			Class<?> clazz = getClass(className);
			if (clazz != null && clazz.getModifiers() == Modifier.PUBLIC) {
				result.add(new SpringBean(beans[i], clazz));
			}
		}
		return result;
	}

	static Class<?> getClass(String className) {
		try {
			return Class.forName(className);
		} catch (NoClassDefFoundError e) {
			// This exception happens when you have the proprietary sub-projects of the Frank!Framework.
			// These sub-projects have classes that depend on third-party classes. If such a third-party
			// class is not found, then this exception handler is entered. We ignore the error because
			// we do have the class in the proprietary FF! subproject.
			log.warn("Ignoring NoClassDefFoundError, assuming it is about a third party class", e);
			return null;
		} catch(ClassNotFoundException e) {
			// This handler is entered when we really do not have the class.
			throw new RuntimeException("Class not found", e);
		}
	}

	private static void addExcludeFilter(ClassPathBeanDefinitionScanner scanner, String excludeFilter) {
		scanner.addExcludeFilter(new RegexPatternTypeFilter(Pattern.compile(excludeFilter)));
	}

	@Override
	public List<FrankClass> getClassesAnnotatedWith(String annotationName) throws FrankDocException {
		Class<?> annotation;
		try {
			annotation = Class.forName(annotationName);
		} catch(ClassNotFoundException e) {
			throw new FrankDocException(String.format("FrankClassRepositoryReflect.getClassesAnnotatedWith called with non-existing annotation [%s]", annotationName), e);
		}
		if(! annotation.isAnnotation()) {
			throw new FrankDocException(String.format("FrankClassRepositoryReflect.getClassesAnnotatedWith called with [%s], but that is not an annotation", annotationName), null);
		}
		// Spring has a filter AnnotationTypeFilter, but that one omits interfaces
		// with an annotation. In my test, using this filter class only left
		// classes implementing the annotated interface, omitting the annotated
		// interface itself.
		Set<SpringBean> springBeans = getSpringBeans(null);
		return springBeans.stream()
				.map(SpringBean::getName)
				.map(this::findClassLoggingException)
				.filter(Objects::nonNull)
				.filter(c -> c.getAnnotation(annotationName) != null)
				.collect(Collectors.toList());
	}

	private FrankClass findClassLoggingException(String className) {
		try {
			return findClass(className);
		} catch(FrankDocException e) {
			log.warn("Could not find FrankClass {}", className, e);
			return null;
		}
	}
}
