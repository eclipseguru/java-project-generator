package de.loskutov.jpg;


public class Stats {

    public final int classes;
    public final long lines;

    public Stats(int classes, long lines) {
        this.classes = classes;
        this.lines = lines;
    }

    public Stats sum(Stats other) {
        return new Stats(classes + other.classes, lines + other.lines);
    }


}
