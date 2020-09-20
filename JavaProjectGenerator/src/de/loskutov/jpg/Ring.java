package de.loskutov.jpg;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Ring<E> {

    private List<E> choices;
    int cursor = -1;
    private int limit;

    public Ring(List<E> list) {
        this(list, -1);
    }

    public Ring(List<E> list, int limit) {
        this.limit = limit;
        if (list instanceof ArrayList) {
            this.choices = list;
        } else {
            this.choices = new ArrayList<>(list);
        }
//		Collections.shuffle(choices);
    }

    public E next() {
        cursor++;
        if (cursor >= choices.size()) {
            cursor = 0;
        }
        return choices.get(cursor);
    }

    public Stream<E> stream() {
        if (limit > 0) {
            return Stream.generate(() -> next()).limit(limit);
        }
        return Stream.generate(() -> next());
    }

    public Stream<E> limtedStream(int limit) {
        return Stream.generate(() -> next()).limit(limit);
    }

    public int originalDataSize() {
        return choices.size();
    }
}
