package de.loskutov.jpg.patterns;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.loskutov.jpg.Package;
import de.loskutov.jpg.Project;
import de.loskutov.jpg.Ring;
import de.loskutov.jpg.Stats;

public class PackageSplitAcrossProjects extends Pattern {


    static List<String> namesList = IntStream.rangeClosed('A', 'Z').mapToObj(x -> String.valueOf((char)x))
            .collect(Collectors.toList());


    private int classesGenerated = 0;
    private int classesPerProject;
    private Ring<String> cnames;


    public PackageSplitAcrossProjects(int classesPerProject) {
        this.classesPerProject = classesPerProject;
        cnames = new Ring<>(namesList);
    }

    @Override
    public Stats generateFiles(Project project) throws IOException {
        Package p = new Package("split", null);

        int lines = 0;
        for (int i = 0; i < classesPerProject; i++) {
            String name = cnames.next();
            if(++classesGenerated >= cnames.originalDataSize()) {
                name = name + (classesGenerated - cnames.originalDataSize());
            }
            SimpleClazz clazz = new SimpleClazz(name, p.getFqn(), null, null);
            lines += clazz.persist(project.getSourceRoot());
        }


        return new Stats(classesPerProject, lines);
    }

}
