package de.loskutov.jpg;

import static java.nio.file.Files.isDirectory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.loskutov.jpg.Project.Template;

class ProjectBuilder {

    static List<String> namesList = IntStream.rangeClosed('a', 'z').mapToObj(x -> String.valueOf((char) x)).collect(Collectors.toList());

    private int projects;
    private int dependencies;
    private int depth;
    private int roots;
    private int classes;
    private Path root;

    private Ring<String> pnames;
    private List<Project> projectsList;

    private Template projectTemplate;

    public ProjectBuilder(int projects, int dependencies, int depth, int roots, int classes, Path root, Project.Template projectTemplate) {
        this.projects = projects;
        this.dependencies = dependencies;
        this.depth = depth;
        this.roots = roots;
        this.classes = classes;
        this.root = root;
        this.projectTemplate = projectTemplate;

        pnames = new Ring<>(namesList);
        projectsList = new ArrayList<>();
    }

    void build() throws IOException {
        if (isDirectory(root)) {
            Files.walkFileTree(root, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        Files.createDirectories(root);

        Project previous = null;
        for (int i = 0; i < projects; i++) {
            String name = pnames.next();
            if (i > pnames.originalDataSize()) {
                name = name + (i - pnames.originalDataSize());
            }
            Project p = createProject(name);
            if(previous != null) {
                p.addDependency(previous);
            }
            projectsList.add(p);
            previous = p;
        }

        for (Project project : projectsList) {
            System.out.print("Project " + project.getName() + ": ");
            project.generate();
            new JavaBuilder(depth, roots, classes, project.getSourceRoot(), project.getName()).build();
        }

    }

    private Project createProject(String name) {
        return new Project(name, root, projectTemplate);
    }
}
