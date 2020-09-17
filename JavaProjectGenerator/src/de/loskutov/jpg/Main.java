package de.loskutov.jpg;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import de.loskutov.jpg.Project.Template;

public class Main {

    public static void main(String[] args) throws IOException {
        String pathname = "./target/generated/";

        int projects = 10;
        int dependencies = 100;
        int roots = 10;
        int depth = 10;
        int classes = 100;

        int fields = 3;
        int imports = 3;
        int comments = 3;
        int see = 3;
        int methods = 1;
        boolean extend = false;
        Project.Template template = Template.Java11;

        if (args.length == 0) {
            System.out.println("No arguments given, using defaults");
        } else {
            try {
                int argc = 0;
                pathname = args[argc++];
                projects = Integer.parseUnsignedInt(args[argc++]);
                dependencies = Integer.parseUnsignedInt(args[argc++]);
                roots = Integer.parseUnsignedInt(args[argc++]);
                depth = Integer.parseUnsignedInt(args[argc++]);
                classes = Integer.parseUnsignedInt(args[argc++]);
                fields = Integer.parseUnsignedInt(args[argc++]);
                imports = Integer.parseUnsignedInt(args[argc++]);
                comments = Integer.parseUnsignedInt(args[argc++]);
                see = Integer.parseUnsignedInt(args[argc++]);
                methods = Integer.parseUnsignedInt(args[argc++]);
                int javaVersion = Integer.parseUnsignedInt(args[argc++]);
                if (javaVersion == 8) {
                    template = Template.Java8;
                }
            } catch (Exception e) {
                //
            }
        }
        File rootDir = new File(pathname);
        Path root = rootDir.toPath();
        System.out.println("Writing to " + rootDir.getAbsolutePath());
        System.out.println("Projects: " + projects + ", dependencies: " + dependencies + ", roots: " + roots + ", depth: " + depth
                + ", classes & interfaces per package: " + (classes * 2) + ", \n imports + fields + comments + methodsx4 per class: "
                + imports + " + " + fields + " + " + comments + " + " + (methods * 4));
        System.out.println("Will generate " + projects + "x" + +roots + "x" + depth + "x" + classes + "x2 + 2 = "
                + (projects * depth * roots * classes * 2 + 2) + " files");

        JavaElement.fieldsCount = fields;
        JavaElement.importsCount = imports;
        JavaElement.commentsCount = comments;
        JavaElement.seeCount = see;
        JavaElement.methodCounts = methods;
        JavaElement.useExtend = extend;

        new ProjectBuilder(projects, dependencies, depth, roots, classes, root, template).build();
    }
}
