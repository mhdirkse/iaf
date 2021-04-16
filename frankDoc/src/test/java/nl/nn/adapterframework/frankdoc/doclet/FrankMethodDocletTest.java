package nl.nn.adapterframework.frankdoc.doclet;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

public class FrankMethodDocletTest {
	static final String PACKAGE = "nl.nn.adapterframework.frankdoc.testtarget.doclet.";

	private FrankClassRepository repository;

	@Before
	public void setUp() {
		repository = Environment.DOCLET.getRepository(PACKAGE);
	}

	@Test
	public void methodOrderIsPreserved() throws FrankDocException {
		FrankClass clazz = repository.findClass(PACKAGE + "Child");
		List<FrankMethod> nonJacocoMethods = Arrays.asList(clazz.getDeclaredMethods()).stream()
				.filter(m -> ! m.getName().contains("jacoco"))
				.collect(Collectors.toList());
		List<String> actual = nonJacocoMethods.stream().map(FrankMethod::getName).collect(Collectors.toList());
		assertArrayEquals(new String[] {"setInherited", "packagePrivateMethod", "setVarargMethod", "getMyInnerEnum", "myAnnotatedMethod"},
				actual.toArray(new String[] {}));
	}

	@Test
	public void testGetJavaDoc() throws FrankDocException {
		String expected = "This is the JavaDoc of \"packagePrivateMethod\".";
		FrankClass clazz = repository.findClass(PACKAGE + "Child");
		FrankMethod packagePrivateMethod = Arrays.asList(clazz.getDeclaredMethods()).stream()
				.filter(m -> m.getName().equals("packagePrivateMethod"))
				.collect(Collectors.toList()).get(0);
		assertEquals(expected, packagePrivateMethod.getJavaDoc());
	}
}
