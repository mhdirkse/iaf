package nl.nn.adapterframework.frankdoc.doclet;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FrankClassRepositoryGetWithAnnotationTest {
	private static final String ANNOTATION_PACKAGE = "nl.nn.adapterframework.doc.";
	private static final String PREFIX = "nl.nn.adapterframework.frankdoc.testtarget.doclet.filtering.";
	private static final String FIRST = "first.";
	private static final String SECOND = "second.";
	private static final String FIRST_PACKAGE = PREFIX + FIRST;
	private static final String SECOND_PACKAGE = PREFIX + SECOND;
	private static final String[] BOTH_PACKAGES = new String[] {FIRST_PACKAGE, SECOND_PACKAGE};
	private static final String[] NO_EXCLUDES = new String[] {};

	@Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		return Arrays.asList(Environment.values()).stream()
				.map(e -> new Object[] {e})
				.collect(Collectors.toList());
	}
	
	@Parameter(0)
	public Environment environment;

	@Test
	public void testGetClassesWithAnnotation() throws Exception {
		FrankClassRepository repository = environment.getRepository(
				Arrays.asList(ANNOTATION_PACKAGE, FIRST_PACKAGE), Arrays.asList(ANNOTATION_PACKAGE, FIRST_PACKAGE), Arrays.asList(NO_EXCLUDES), new ArrayList<>());
		List<FrankClass> actual = repository.getClassesAnnotatedWith(ANNOTATION_PACKAGE + "FrankDocGroup");
		assertEquals(1, actual.size());
		assertEquals(FIRST_PACKAGE + "MyInterface", actual.get(0).getName());
	}
}
