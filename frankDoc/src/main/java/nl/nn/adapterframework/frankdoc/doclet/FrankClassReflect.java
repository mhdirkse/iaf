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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.type.filter.AssignableTypeFilter;

class FrankClassReflect implements FrankClass {
	private final Class<?> clazz;
	private final FrankClassRepositoryReflect repository;
	private final Map<String, FrankAnnotation> annotations;

	FrankClassReflect(Class<?> clazz, FrankClassRepositoryReflect repository) {
		this.clazz = clazz;
		this.repository = repository;
		Annotation[] reflectAnnotations = clazz.getAnnotations();
		annotations = new HashMap<>();
		for(Annotation r: reflectAnnotations) {
			FrankAnnotation frankAnnotation = new FrankAnnotationReflect(r);
			annotations.put(frankAnnotation.getName(), frankAnnotation);
		}
	}

	FrankClassRepository getRepository() {
		return repository;
	}

	@Override
	public String getName() {
		return clazz.getName();
	}

	@Override
	public String getSimpleName() {
		return clazz.getSimpleName();
	}

	@Override
	public String getPackageName() {
		return clazz.getPackage().getName();
	}

	@Override
	public FrankClass getSuperclass() {
		final Class<?> superClazz = clazz.getSuperclass();
		if(superClazz == null) {
			return null;
		} else {
			boolean omit = ((FrankClassRepositoryReflect) repository).getExcludeFiltersForSuperclass().stream().anyMatch(
					exclude -> superClazz.getName().startsWith(exclude));
			if(omit) {
				return null;
			} else {
				return new FrankClassReflect(superClazz, repository);
			}
		}
	}

	@Override
	public FrankClass[] getInterfaces() {
		Class<?>[] interfazes = clazz.getInterfaces();
		FrankClass[] result = new FrankClass[interfazes.length];
		for(int i = 0; i < interfazes.length; ++i) {
			result[i] = new FrankClassReflect(interfazes[i], repository);
		}
		return result;
	}

	@Override
	public boolean isAbstract() {
		return Modifier.isAbstract(clazz.getModifiers());
	}

	@Override
	public boolean isPublic() {
		return Modifier.isPublic(clazz.getModifiers());		
	}

	@Override
	public boolean isInterface() {
		return clazz.isInterface();
	}

	@Override
	public List<FrankClass> getInterfaceImplementations() throws FrankDocException {
		List<SpringBean> springBeans;
		try {
			springBeans = getSpringBeans(clazz.getName());
		} catch(ReflectiveOperationException e) {
			throw new FrankDocException(String.format("Could not get interface implementations of Java class [%s]", getName()), e);
		}
		// We sort here to make the order deterministic.
		Collections.sort(springBeans);
		return springBeans.stream()
				.map(SpringBean::getClazz)
				.map(clazz -> new FrankClassReflect(clazz, repository))
				.collect(Collectors.toList());
	}

	/**
	 * @param interfaceName The interface for which we want SpringBean objects.
	 * @return All classes implementing interfaceName, ordered by their full class name.
	 */
	private List<SpringBean> getSpringBeans(final String interfaceName) throws ReflectiveOperationException {
		Class<?> interfaze = FrankClassRepositoryReflect.getClass(interfaceName);
		if(interfaze == null) {
			throw new ReflectiveOperationException("Class or interface is not available on the classpath: " + interfaceName);
		}
		if(!interfaze.isInterface()) {
			throw new ReflectiveOperationException("This exists on the classpath but is not an interface: " + interfaceName);
		}
		Set<SpringBean> unfiltered = repository.getSpringBeans(new AssignableTypeFilter(interfaze));
		List<SpringBean> result = new ArrayList<SpringBean>();
		for(SpringBean b: unfiltered) {
			if(interfaze.isAssignableFrom(b.getClazz())) {
				result.add(b);
			}
		}
		return result;
	}

	@Override
	public FrankAnnotation[] getAnnotations() {
		List<FrankAnnotation> annotationList = new ArrayList<>(annotations.values());
		FrankAnnotation[] result = new FrankAnnotation[annotationList.size()];
		for(int i = 0; i < annotationList.size(); ++i) {
			result[i] = (FrankAnnotation) annotationList.get(i);
		}
		return result;
	}

	@Override
	public FrankAnnotation getAnnotation(String name) {
		return annotations.get(name);
	}

	@Override
	public FrankMethod[] getDeclaredMethods() {
		Method[] rawDeclaredMethods = clazz.getDeclaredMethods();
		return wrapReflectMethodsInArray(rawDeclaredMethods);
	}

	private FrankMethod[] wrapReflectMethodsInArray(Method[] rawDeclaredMethods) {
		FrankMethod[] result = new FrankMethod[rawDeclaredMethods.length];
		for(int i = 0; i < rawDeclaredMethods.length; ++i) {
			result[i] = new FrankMethodReflect(rawDeclaredMethods[i], new FrankClassReflect(rawDeclaredMethods[i].getDeclaringClass(), repository));
		}
		return result;
	}

	@Override
	public FrankMethod[] getDeclaredAndInheritedMethods() {
		Method[] rawDeclaredMethods = clazz.getMethods();
		return wrapReflectMethodsInArray(rawDeclaredMethods);		
	}

	@Override
	public boolean isEnum() {
		return clazz.isEnum();
	}

	@Override
	public String[] getEnumConstants() {
		@SuppressWarnings("unchecked")
		Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) clazz;
		Enum<?>[] enumConstants = enumClass.getEnumConstants();
		String[] result = new String[enumConstants.length];
		for(int i = 0; i < enumConstants.length; ++i) {
			result[i] = enumConstants[i].name();
		}
		return result;
	}

	@Override
	public String getJavaDoc() {
		return null;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public FrankAnnotation getGroupAnnotation() {
		return null;
	}
}
