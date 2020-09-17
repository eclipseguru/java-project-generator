package de.loskutov.jpg;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class JavaBuilder {

	static List<String> namesList = IntStream.rangeClosed('a', 'z').mapToObj(x -> String.valueOf((char)x))
			.collect(Collectors.toList());

	int depth;
	int roots;
	private Ring<String> pnames;
	private Path root;
	private String packagePrefix;

	private List<Clazz> classes;

	private List<Interface> interfaces;

	private int countClasses;


	public JavaBuilder(int depth, int roots, int countClasses, Path root) {
	    this(depth, roots, countClasses, root, null);
	}

	public JavaBuilder(int depth, int roots, int countClasses, Path root, String packagePrefix) {
        this.depth = depth;
        this.roots = roots;
        this.countClasses = countClasses;
        this.root = root;
        this.packagePrefix = packagePrefix;
        pnames = new Ring<>(namesList);
        classes = new ArrayList<>();
        interfaces = new ArrayList<>();
    }

    void build() throws IOException {
		List<Package> rootPackages = new ArrayList<>();

		for (int i = 0; i < roots; i++) {
			String name = pnames.next();
			if(i > pnames.originalDataSize()) {
				name = name + (i - pnames.originalDataSize());
			}
			if(packagePrefix != null) {
			    name = packagePrefix + "." + name;
			}
			Package p = createPackage(name, null);
			rootPackages.add(p);
		}

		for (Package r : rootPackages) {
			Stream<Package> stream = Stream.iterate(r, x -> new Package(pnames.next(), x)).limit(depth);
			stream.forEach(p -> {
				createInterfaces(p);
				createClasses(p);
			});
		}
		AtomicLong lines = new AtomicLong();
		List<JavaElement> elements = new ArrayList<>(interfaces);
		elements.addAll(classes);
		elements.forEach(t -> lines.addAndGet(generateFile(t)));
		System.out.println("Generated " + elements.size() + " classes with " + lines + " lines of code");
	}

	private int generateFile(JavaElement e) {
		try {
			return e.persist(root);
		} catch (IOException e1) {
			e1.printStackTrace();
			return 0;
		}
	}

	void createInterfaces(Package p) {
		Ring<Interface> toImplement = new Ring<>(interfaces, countClasses);
		if(interfaces.isEmpty()) {
			interfaces.add(new Interface("IFoo0", p.getFqn(), "java.util.concurrent.Callable"));
		}
		toImplement.stream().forEach(i -> {
			interfaces.add(new Interface("IFoo" + interfaces.size(), p.getFqn(), toImplement.next().fqn()));
		});
	}

	void createClasses(Package p) {
		Ring<Interface> implement = new Ring<>(interfaces, countClasses);
		Ring<Clazz> toExtend = new Ring<>(classes, countClasses);
		if (classes.isEmpty()) {
			classes.add(new Clazz("Foo0", p.getFqn(), implement.next().fqn(), "java.lang.Object"));
		}
		toExtend.stream().forEach(x -> {
			classes.add(new Clazz("Foo" + classes.size(), p.getFqn(), implement.next().fqn(), toExtend.next().fqn()));
		});
	}

	Package createPackage(String name, Package parent) {
		return new Package(name, parent);
	}

}