package de.loskutov.jpg.utils;

import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.write;
import static java.nio.file.Path.of;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MavenJarListGenerator {

    public static void main(String[] args) {
        String pathname = "./target/generated/jar-list";
        AtomicInteger count = new AtomicInteger(2000);

        if (args.length == 0) {
            System.out.println("No arguments given, using defaults");
        } else {
            try {
                int argc = 0;
                pathname = args[argc++];
                count.set(Integer.parseUnsignedInt(args[argc++]));
            } catch (Exception e) {
                //
            }
        }

        List<String> jarList = new ArrayList<>();
        String userHome = System.getProperty("user.home");
        Path m2Repo = of(userHome, ".m2/repository");
        try {
            Files.walkFileTree(m2Repo, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String filePath = file.toString();
                    if(filePath.endsWith(".jar") && !filePath.endsWith("-sources.jar") && !filePath.endsWith("-javadoc.jar")) {
                        jarList.add(filePath.replace(userHome, "~"));
                    }
                    return jarList.size() <= count.get() ? FileVisitResult.CONTINUE : FileVisitResult.TERMINATE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return jarList.size() <= count.get() ? FileVisitResult.CONTINUE : FileVisitResult.TERMINATE;
                }
            });

            Path jarListFile = Path.of(pathname);

            createDirectories(jarListFile.getParent());
            write(jarListFile, jarList);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }

}
