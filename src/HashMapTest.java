import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by evangileon on 10/16/14.
 */
public class HashMapTest {

    static class Pair implements Comparable<Pair> {
        Integer id;
        Integer pointer;

        Pair(Integer id, Integer pointer) {
            this.id = id;
            this.pointer = pointer;
        }

        @Override
        public int compareTo(Pair o) {
            return this.id - o.id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Pair pair = (Pair) o;

            if (!id.equals(pair.id)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }

    /**
     * Initialize the hash map, using random number as key and one of member of value
     * @param n scale of data
     * @return HashMap
     */
    static HashMap<Integer, Pair> initializeHashMap(int n) {
        if (n < 0) {
            return null;
        }

        HashMap<Integer, Pair> map = new HashMap<Integer, Pair>();

        Random rand = new Random();
        for (int i = 0; i < n; i++) {
            int temp;
            do {
                temp = rand.nextInt(Integer.MAX_VALUE);
            } while (map.containsKey(temp)); // ensure exact n elements in hash map

            map.put(temp, new Pair(temp, i));
        }
        return map;
    }



    /**
     * To ensure map and array have the same elements, iterate the map
     * and export every element into array
     * @param map reference map
     * @return sorted array by the order of Pair.id
     */
    static Pair[] initializeArray(HashMap<Integer, Pair> map) {

        Pair[] array = new Pair[map.size()];
        int i = 0;
        for (Map.Entry<Integer, Pair> kvPair : map.entrySet()) {
            array[i++] = kvPair.getValue();
        }

        Arrays.sort(array);

        return array;
    }

    /**
     * Initialize array list from array
     * @param array reference array
     * @return array list
     */
    static ArrayList<Pair> initializeArrayList(Pair[] array) {
        return new ArrayList<Pair>(Arrays.asList(array));
    }

    /**
     * Three must have the same contents
     * @param fraction one fraction th of elements to search
     * @param map HashMap
     * @param array Array
     * @param arrayList ArrayList
     */
    static void compareSearchTime(int fraction, HashMap<Integer, Pair> map, Pair[] array, ArrayList<Pair> arrayList) {
        if(fraction <= 0) {
            return;
        }

        int n = map.size();

        long timeMap;
        long timeArray;
        long timeArrayList;

        Random rand = new Random();

        int numToSearch = n / fraction; // one tenth of elements to search

        int[] searchs = new int[numToSearch];
        for (int i = 0; i < numToSearch; i++) {
            searchs[i] = rand.nextInt(Integer.MAX_VALUE);
        }

        // then begin search
        // for hash map
        int hitHashMap = 0;
        timeMap = System.nanoTime();
        for (int i = 0; i < numToSearch; i++) {
            if (map.containsKey(searchs[i])) {
                hitHashMap++;
            }
        }
        timeMap = System.nanoTime() - timeMap;

        int hitArray = 0;
        Pair temp = new Pair(0, 0);
        timeArray = System.nanoTime();
        for (int i = 0; i < numToSearch; i++) {
            temp.id = searchs[i];
            if (Arrays.binarySearch(array, temp) >= 0 ) {
                hitArray++;
            }
        }
        timeArray = System.nanoTime() - timeArray;

        temp = new Pair(0, 0);
        int hitArrayList = 0;
        timeArrayList = System.nanoTime();
        for (int i = 0; i < numToSearch; i++) {
            temp.id = searchs[i];
            if (Collections.binarySearch(arrayList, temp) >= 0) {
                hitArrayList++;
            }
        }
        timeArrayList = System.nanoTime() - timeArrayList;

        System.out.println("n = " + n + " fraction = " + fraction);
        System.out.println("HashMap:  \t" + hitHashMap + "\t" + timeMap);
        System.out.println("Array:    \t" + hitArray + "\t" + timeArray);
        System.out.println("ArrayList:\t" + hitArrayList + "\t" + timeArrayList);
    }

    public static void main(String[] args) {
        int n = 1;
        for (int i = 0; i < 7; i++) {
            n *= 10;
            HashMap<Integer, Pair> map = initializeHashMap(n);
            Pair[] array = initializeArray(map);
            ArrayList<Pair> arrayList = initializeArrayList(array);
            compareSearchTime(10, map, array, arrayList);
        }

        ConcurrentSkipListMap<Integer, String> skipListMap = new ConcurrentSkipListMap<Integer, String>();

    }
}
