import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Spliterator;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Chapter7 {

    @Test
    void parallelStreamTest() {
        System.out.println(parallelSum());
    }

    private long parallelSum() {
        return Stream.iterate(1L, i -> i + 1)
                .limit(10_000_000)
                .parallel()
                .reduce(0L, Long::sum);
    }

    @Test
    void sideEffectParallelSum() {
        System.out.println("SideEffect parallel sum done in :" + sideEffectSum(sideEffectSum(10_000_000L)));
    }

    private long sideEffectSum(long n) {
        Accumulator accumulator = new Accumulator();
        LongStream.rangeClosed(1, n)
                .parallel()
                .forEach(accumulator::add);
        return accumulator.total;
    }


    class Accumulator {
        public long total = 0;

        public void add(long value) {
            total += value;
        }
    }

    @Test
    @DisplayName("문자열의 단어 수 계산 -  단순 반복문")
    void countWordsIteratively() {

        System.out.println(countWordsIteratively(" Nel   mezzo del cammin  di nostra vita "));

    }

    private int countWordsIteratively(String s) {
        int counter = 0;
        boolean lastSpace = true;
        for (char c : s.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                lastSpace = true;
            } else {
                if (lastSpace) {
                    counter++;
                }
                lastSpace = false;
            }
        }
        return counter;
    }


    @Test
    @DisplayName("문자열의 단어 수 계산 - reduce")
    void countWordsStream() {
        Stream<Character> stream = IntStream.range(0, " Nel   mezzo del cammin  di nostra vita ".length())
                .mapToObj(" Nel   mezzo del cammin  di nostra vita "::charAt);
        System.out.println(countWordsStream(stream));
    }

    private int countWordsStream(Stream<Character> s) {
        return s.reduce(new WordCounter(0, true),
                WordCounter::accumulate,
                WordCounter::combine).getCounter();
    }

    @Test
    @DisplayName("문자열의 단어 수 계산 - 병렬 스트림")
    void countWordsParallelStream() {
        Stream<Character> stream = IntStream.range(0, " Nel   mezzo del cammin  di nostra vita ".length())
                .mapToObj(" Nel   mezzo del cammin  di nostra vita "::charAt);
        System.out.println(countWordsStream(stream.parallel()));
    }


    class WordCounter {
        private final int counter;
        private final boolean lastSpace;

        public WordCounter(int counter, boolean lastSpace) {
            this.counter = counter;
            this.lastSpace = lastSpace;
        }

        public WordCounter accumulate(Character c) {
            if (Character.isSpaceChar(c)) {
                return lastSpace ? this : new WordCounter(counter, true);
            } else {
                return lastSpace ? new WordCounter(counter + 1, false) : this;
            }
        }

        public WordCounter combine(WordCounter other) {
            return new WordCounter(counter + other.counter, lastSpace && other.lastSpace);
        }

        public int getCounter() {
            return counter;
        }
    }


}

