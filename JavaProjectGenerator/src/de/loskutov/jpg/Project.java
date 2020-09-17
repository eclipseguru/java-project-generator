package de.loskutov.jpg;

import static java.lang.String.format;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.createDirectory;
import static java.nio.file.Files.readAllLines;
import static java.nio.file.Files.readString;
import static java.nio.file.Files.write;
import static java.nio.file.Files.writeString;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Project {

    public static enum Template {
        Java8, Java11
    }

    private Path projectDir;
    private String name;
    private Template template;
    private List<Project> projectDependencies = new ArrayList<>();

    public Project(String name, Path root, Template template) {
        this.name = name;
        this.template = requireNonNull(template);
        projectDir = root.resolve(name);
    }

    public void generate() throws IOException {
        createDirectories(projectDir.resolve(".settings"));
        createDirectory(getSourceRoot());

        String templatePackage = getTemplatePackage();

        Set<String> resources = Set.of(".project", ".classpath", ".settings/org.eclipse.jdt.core.prefs");
        for (String resource : resources) {
            try (InputStream in = Project.class.getResourceAsStream(templatePackage + resource)) {
                try (OutputStream out = Files.newOutputStream(projectDir.resolve(resource))) {
                    in.transferTo(out);
                }
            }
        }

        updateDotProjectName();
        updateDotClasspath();
    }

    private void updateDotProjectName() throws IOException {
        Path dotProject = projectDir.resolve(".project");
        writeString(dotProject, readString(dotProject).replaceFirst("<name>[^<]+</name>", "<name>" + name + "</name>"));
    }

    private void updateDotClasspath() throws IOException {
        Path dotClasspath = projectDir.resolve(".classpath");
        List<String> projectClasspath = readAllLines(dotClasspath);
        int indexOfOutputEntry = projectClasspath.indexOf("\t<classpathentry kind=\"output\" path=\"bin\"/>");
        if (indexOfOutputEntry == -1)
            throw new IOException("Something is wrong with the .classpath template. Output line not found!");
        for (Project project : projectDependencies) {
            projectClasspath.add(indexOfOutputEntry, format("\t<classpathentry combineaccessrules=\"false\" kind=\"src\" path=\"/%s\"/>", project.getName()));
        }
        write(dotClasspath, projectClasspath);
    }

    public String getName() {
        return name;
    }

    public Path getSourceRoot() {
        return projectDir.resolve("src");
    }

    private String getTemplatePackage() {
        switch (template) {
            case Java8:
                return "/resources/java_8/";
            case Java11:
                return "/resources/java_11/";
            default:
                throw new IllegalStateException("unsupported template: " + template);
        }
    }

    public void addDependency(Project p) {
        projectDependencies.add(p);
    }

}
