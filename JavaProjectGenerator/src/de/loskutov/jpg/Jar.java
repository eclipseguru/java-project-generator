package de.loskutov.jpg;

import java.nio.file.Path;

public class Jar {

    private Path jar;
    private Path sourcesJar;

    public Jar(Path jar, Path sourcesJar) {
        this.jar = jar;
        this.sourcesJar = sourcesJar;
    }

    public Path getJar() {
        return jar;
    }

    public Path getSourcesJar() {
        return sourcesJar;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((jar == null) ? 0 : jar.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Jar other = (Jar) obj;
        if (jar == null) {
            if (other.jar != null)
                return false;
        } else if (!jar.equals(other.jar))
            return false;
        return true;
    }

}
