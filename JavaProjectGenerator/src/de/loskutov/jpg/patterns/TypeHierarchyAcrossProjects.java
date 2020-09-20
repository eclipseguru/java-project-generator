package de.loskutov.jpg.patterns;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.loskutov.jpg.Package;
import de.loskutov.jpg.Project;
import de.loskutov.jpg.Ring;
import de.loskutov.jpg.Stats;

public class TypeHierarchyAcrossProjects extends Pattern {

    static List<String> namesList = IntStream.rangeClosed('A', 'Z').mapToObj(x -> String.valueOf((char)x))
            .collect(Collectors.toList());

    private final Ring<String> cnames = new Ring<>(namesList);

    private Project definingProject;
    private String baseType;
    private String baseTypeImport;
    private int classesGenerated = 0;

    @Override
    public void configureProject(Project project) {
        super.configureProject(project);
        if(definingProject != null) {
            project.addDependency(definingProject);
        }
    }

    @Override
    public Stats generateFiles(Project project) throws IOException {
        if(definingProject == null) {
            definingProject = project;
            int lines = generateBaseType();
            return new Stats(1, lines);
        }

        String name = "Extender" + cnames.next();
        if(++classesGenerated >= cnames.originalDataSize()) {
            name = name + (classesGenerated - cnames.originalDataSize());
        }

        Package p = new Package("basetypeextension", null);
        SimpleClazz c = new SimpleClazz(name, p.getFqn(), null, baseType);
        c.setAdditionalImports(List.of(baseTypeImport));

        int lines = c.persist(project.getSourceRoot());
        return new Stats(1, lines);
    }

    private int generateBaseType() throws IOException {

        Package p = new Package("basetype", null);
        SimpleClazz c = new SimpleClazz("BaseType", p.getFqn(), null, null);
        baseType = c.name();
        baseTypeImport = c.fqn();

        return c.persist(definingProject.getSourceRoot());
    }

}
