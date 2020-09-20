package de.loskutov.jpg.patterns;

import de.loskutov.jpg.JavaElement;

public class SimpleClazz extends JavaElement {

	String implement;
	String extend;
	SimpleClazz(String name, String packageName, String implement, String extend) {
		super(name, packageName);
		this.implement = implement;
		this.extend = extend;
	}

	@Override
    protected String generateCode() {
		String s = "package " + packageName + ";\n\n" +
				generateImports() +
				generateComments() +
				generateTypeDefinition() +
				"{\n\n" +
					generateFields() +
					generateClassFields("String") +
					generateObjectMethods("String") +
				"}\n";
		return s;
	}

	String generateTypeDefinition() {
		if(extend != null && implement != null) {
			return "@SuppressWarnings(\"all\")\n" +
					"public abstract class " + name + " extends " + extend + " implements " + implement + " ";
		}
		else if(extend != null) {
            return "@SuppressWarnings(\"all\")\n" +
                    "public abstract class " + name + " extends " + extend + " ";
        }
        else if(implement != null) {
            return "@SuppressWarnings(\"all\")\n" +
                    "public abstract class " + name + " implements " + implement + " ";
        }
		return "@SuppressWarnings(\"all\")\n" +
				"public abstract class " + name + " ";
	}

	String generateClassFields(String type) {
		if(methodCounts == 0) {
			return "";
		}
		String result =
				"\t public "+type+" element;\n\n" +
				"\t public static " + name + " instance;\n\n";
		return result;
	}


	String generateObjectMethods(String type) {
		if(methodCounts == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < methodCounts; i++) {
			String suffix = i == 0? "" : "" + i;
			String result =
				"\t public static " + name + " getInstance" + suffix + "() {\n" +
				"\t \t return instance;\n" +
				"\t }\n\n" +
				"\t public static <T> T create" + suffix + "(java.util.List<T> input) {\n" +
				"\t \t return null;\n" +
				"\t }\n\n" +
				"\t public String getName" + suffix + "() {\n" +
				"\t \t return element.toString();\n" +
				"\t }\n\n" +
				"\t public void setName" + suffix + "(String string) {\n" +
				"\t \t return;\n" +
				"\t }\n\n" +
				"\t public "+type+" get" + suffix + "() {\n" +
				"\t \t return element;\n" +
				"\t }\n\n" +
				"\t public void set" + suffix + "(Object element) {\n" +
				"\t \t this.element = ("+type+")element;\n" +
				"\t }\n\n" +
				"\t public "+type+" call" + suffix + "() throws Exception {\n" +
				"\t \t return ("+type+")getInstance" + suffix + "().call" + suffix + "();\n" +
				"\t }\n";
			sb.append(result);
		}
		return sb.toString();
	}

}
