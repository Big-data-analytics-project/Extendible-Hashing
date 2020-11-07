// << +1 || >> -1
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.*;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;

public class  ExtHash<K,V> {
    static class Bucket<K, V> {
        int localdepth = 0;
        int size = 0;
        static int bucket_size = 500;
        private MyHashMap bucket = new MyHashMap<K, V>(size);
        List<K> keyset = new ArrayList<K>();

        public void put(Data<K, V> x) {
            // put data in bucket and create keyset
            if (!(keyset.contains(x.key))) {
                keyset.add(x.key);
                //System.out.println(keyset);
            }
            bucket.addData(x);
        }

        public V get(K key) {
            return (V) bucket.getData(key);
        }

        public boolean isFull() {
            return bucket.getSize() >= bucket_size;
        }

        public int getSize() {
            return bucket_size;
        }

        @Override
        public String toString() {
            return "{ bucket=" + bucket + ",size= " + bucket.getSize() + ", localdepth=" + localdepth + "}\n";
        }
    }

    AtomicInteger globaldepth = new AtomicInteger(0);
    List<Bucket<K, V>> bucketlist = new ArrayList<Bucket<K, V>>();
    int counter = 0;

    public ExtHash() {
        bucketlist.add(new Bucket<K, V>());
    }

    synchronized public static <K> String hashcode(K k) {
        // convert key to binary and return it. for now it only work with integer keys.
        // if we will have string keys as well we have to find a way to convert string to integers
        String hashcode = Integer.toBinaryString(k.hashCode());
        //System.out.println(k + "," + hashcode);
        return hashcode;
    }

    synchronized public Bucket<K, V> getBucket(K key) { // get bucket based on hashcode(key)
        String hashcode = hashcode(key);
        BigInteger hd = new BigInteger(hashcode);
        //System.out.println(hd & (1 << globaldepth.get()) - 1);
        //System.out.println(hd);
        //Bucket<K,V> b = bucketlist.get((int) (hd & (1 << globaldepth.get()) - 1));
        //System.out.println(hd);
        hd = (hd.and(BigInteger.valueOf(1 << globaldepth.get()).subtract(BigInteger.valueOf(1))));
        //System.out.println(hd);
        Bucket<K, V> b = bucketlist.get(hd.intValue());
        return b;
    }

    synchronized public V getValue(K key) {
        String hashcode = hashcode(key);
        BigInteger hd = new BigInteger(hashcode);
        hd = (hd.and(BigInteger.valueOf(1 << globaldepth.get()).subtract(BigInteger.valueOf(1))));
        Bucket<K, V> b = bucketlist.get(hd.intValue());
        for (int i = 0; i < b.getSize(); i++) {
            if (b.bucket.getData(key) != null) {
                return (V) b.get(key); //(V) is called casting.
            }
        }
        return null;
    }

    synchronized public void remove(K key) {
        String hashcode = hashcode(key);
        BigInteger hd = new BigInteger(hashcode);
        hd = (hd.and(BigInteger.valueOf(1 << globaldepth.get()).subtract(BigInteger.valueOf(1))));
        Bucket<K, V> b = bucketlist.get(hd.intValue());
        for (int i = 0; i < b.getSize(); i++) {
            if (b.bucket.getData(key) != null) {
                b.bucket.remove(key); //(V) is called casting.
            }
        }
    }

    synchronized public void put(K key, V value) {
        Bucket<K, V> b = getBucket(key);
        //System.out.println(b);
        if (b.localdepth == globaldepth.get() && b.isFull()) {
            //in this case we double the buckets and we increase globaldepth.
            List<Bucket<K, V>> t2 = new ArrayList<ExtHash.Bucket<K, V>>(bucketlist);
            bucketlist.addAll(t2);
            globaldepth.incrementAndGet();
        }

        if (b.localdepth < globaldepth.get() && b.isFull()) {
            // in this case we dont have to double the no of buckets.. we just have to split the current bucket because its full..
            Data<K, V> d = new Data<K, V>(key, value);
            b.put(d);
            //split data of bucket b to buckets b1 and b2
            Bucket<K, V> b1 = new Bucket<K, V>();
            Bucket<K, V> b2 = new Bucket<K, V>();

            //System.out.println(b.keyset);

            for (K key2 : b.keyset) {
                V value2 = (V) b.bucket.getData(key2);
                Data<K, V> d2 = new Data<K, V>(key2, value2);

                //long hd = Long.parseLong(hashcode(key2));
                //int hashcode =(int) (hd & ((1 << globaldepth.get())) - 1);

                String hashcode = hashcode(key2);
                BigInteger hd = new BigInteger(hashcode);
                hd = (hd.and(BigInteger.valueOf(1 << globaldepth.get()).subtract(BigInteger.valueOf(1))));

                //System.out.println(hd);
                if (hd.or(BigInteger.valueOf(1 << b.localdepth)).equals(hd)) {
                    b2.put(d2);
                } else {
                    b1.put(d2);
                }
            }

            List<Integer> l = new ArrayList<Integer>();
            for (int i = 0; i < bucketlist.size(); i++) {
                if (bucketlist.get(i) == b) {
                    l.add(i);
                }
            }

            for (int i : l) {
                if ((i | (1 << b.localdepth)) == i) {
                    bucketlist.set(i, b2);
                } else {
                    bucketlist.set(i, b1);
                }
            }
            b1.localdepth = b.localdepth + 1;
            b2.localdepth = b.localdepth + 1;

        } else {
            //if the bucket in not full just add the data.
            Data<K, V> d = new Data<K, V>(key, value);
            b.put(d);
        }
    }

    @Override
    public String toString() {
        return "ExtHash{" +
                "globaldepth=" + globaldepth +
                ",\n " + bucketlist + '}';
    }

    public int countElements(){
        for(int i=0; i<bucketlist.size(); i++){
                counter += bucketlist.get(i).keyset.size();
            }
        return counter;
        }

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        //this example is from anadiotis slides..
        //when we have the dataset we can split it to as many threads we want
        //we have to test the code a bit more
        //overleaf (?)
        /*
        ExtHash<Integer, Integer> eh1 = new ExtHash<Integer, Integer>();

        int[] x = new int[]{4, 1, 12, 32, 16, 5};
        Thread t1 = new Thread(new MyThread("1", eh1, x, "Insert"));
        int[] y = new int[]{21, 13, 10, 15, 7, 19};
        Thread t2 = new Thread(new MyThread("2", eh1, y, "Insert"));
        int[] z = new int[]{20};
        Thread t3 = new Thread(new MyThread("3", eh1, z, "Insert"));

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

       // eh1.remove(20);
        System.out.println(eh1);
        System.out.println(eh1.globaldepth);
        System.out.println("yo " + eh1.getValue(20));

        System.out.print(eh1.countElements());
         */

        ExtHash<String, String> eh2 = new ExtHash<String, String>();
        ArrayList<String> keys = new ArrayList<String>();
        ArrayList<String> values = new ArrayList<String>();
        Scanner reader = new Scanner(new File("911.csv"));
        // Skip the 2 first lines
        //reader.nextLine();
        //reader.nextLine();
        String sep = ",";

        while(reader.hasNextLine()) {
            String [] line = reader.nextLine().split(sep);
            keys.add(line[2]);
            //ArrayList temp = new ArrayList(Arrays.asList(line));
            //temp.remove(0);
            values.add(line[4]);
        }

        int size = keys.size();
        System.out.println("size=" + size);
        List<String> key1 = keys.subList(0,(int)size/3);
        List<String> key2 = keys.subList((int)size/3 + 1,(int) 2*size/3);
        List<String> key3 = keys.subList((int) 2*size/3,(int) size);

        List<String> value1 = values.subList(0,(int)size/3);
        List<String> value2 = values.subList((int)size/3 + 1,(int) 2*size/3);
        List<String> value3 = values.subList((int) 2*size/3,(int) size);

        long start = System.currentTimeMillis();
        MyThread m1 = new MyThread("1", eh2, key1, value1, "Insert");
        Thread t1 = new Thread(m1);
        Thread t2 = new Thread(new MyThread("2", eh2, key2, value2 ,"Insert"));
        Thread t3 = new Thread(new MyThread("3", eh2, key3, value3 ,"Insert"));
       // Thread t4 = new Thread(new MyThread("3", eh2, key4, value3 ,"Insert"));
        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        System.out.println("Time=" + timeElapsed);

        List<Long> temp1 =  m1.getList();
        temp1.forEach(x -> counter += x);

        List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
        LongSummaryStatistics sum = temp1.stream()
                .collect(Collectors.summarizingLong(Long::longValue));

        //eh2.remove("Toyota Corolla");
        //System.out.println("yo" + eh2.getValue("Toyota Corolla"));

        //System.out.println(eh2);
        //System.out.println(eh2.getValue("\"Metadata for Digital Media: Introduction to the Special Issue\""));
        //System.out.println("Insertion "+linetype+" finished");


    }
}


