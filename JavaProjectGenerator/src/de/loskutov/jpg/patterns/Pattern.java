package de.loskutov.jpg.patterns;

import java.io.IOException;

import de.loskutov.jpg.Project;
import de.loskutov.jpg.Stats;

public abstract class Pattern {

    public void configureProject(Project project) {}

    public abstract Stats generateFiles(Project project)  throws IOException;

}
