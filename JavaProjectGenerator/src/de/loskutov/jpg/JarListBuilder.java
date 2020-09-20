package de.loskutov.jpg;

import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JarListBuilder {

    private String userHome = System.getProperty("user.home");

    private String jarlist;

    public JarListBuilder(String jarlist) {
        this.jarlist = jarlist;
    }

    private Jar readJar(String line) {
        if(!line.endsWith(".jar")) {
            return null;
        }

        if(line.startsWith("~")) {
            line = userHome + line.substring(1);
        }

        Path jar = Path.of(line);
        Path sourcesJar = Path.of(line.substring(0, line.length()-4) + "-sources.jar");
        return new Jar(jar, sourcesJar);
    }

    public List<Jar> build() throws IOException {
        List<String> lines;
        if(jarlist == null) {
            lines = new ArrayList<>();
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(JarListBuilder.class.getResourceAsStream("/resources/jarlist/oss-maven-jarlist"), StandardCharsets.UTF_8))) {
                String line = reader.readLine();
                while(line != null) {
                    lines.add(line);
                    line = reader.readLine();
                }
            }
        } else {
            lines = Files.readAllLines(Path.of(jarlist));
        }

        return lines.stream().map(this::readJar).filter(Objects::nonNull).collect(toList());
    }

}
