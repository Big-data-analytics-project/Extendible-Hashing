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
    
    
    public static void writeInsertPerformance(String filename) throws IOException, InterruptedException {
    	ExtHash<String, String> eh2 = new ExtHash<String, String>();
        ArrayList<String> keys = new ArrayList<String>();
        ArrayList<String> values = new ArrayList<String>();
        Scanner reader = new Scanner(new File("911.csv"));
        reader.nextLine();
        String sep = ",";

        while(reader.hasNextLine()) {
            String [] line = reader.nextLine().split(sep);
            keys.add(line[2]);
            values.add(line[4]);
        }
        
        ArrayList<Long> times = new ArrayList<>();
    	
    	for(int i=5000;i<keys.size();i+=100000) {
    		List<String> keysSub = keys.subList(0, i);
    		List<String> valuesSub = values.subList(0, i);
    		int size = keysSub.size();
            
    		
            List<String> key1 = keysSub.subList(0,(int)size/3);
            List<String> key2 = keysSub.subList((int)size/3,(int) 2*size/3);
            List<String> key3 = keysSub.subList((int) 2*size/3,size);

            List<String> value1 = valuesSub.subList(0,(int)size/3);
            List<String> value2 = valuesSub.subList((int)size/3 + 1,(int) 2*size/3);
            List<String> value3 = valuesSub.subList((int) 2*size/3,size);

            
            MyThread m1 = new MyThread("1", eh2, key1, value1, "Insert");
            Thread t1 = new Thread(m1);
            Thread t2 = new Thread(new MyThread("2", eh2, key2, value2 ,"Insert"));
            Thread t3 = new Thread(new MyThread("3", eh2, key3, value3 ,"Insert"));
            
            long start = System.nanoTime();
            t1.start();
            t2.start();
            t3.start();

            t1.join();
            t2.join();
            t3.join();
            long finish = System.nanoTime();
            
            times.add(finish - start);
            
            t1.interrupt();
            t2.interrupt();
            t3.interrupt();
    	}
    	
    	
    	PrintWriter pw=new PrintWriter(new FileWriter(filename));
	    pw.println(times.toString());
		pw.close();
    }
    
    public static void writeAccessPerformance(String filename) throws InterruptedException, IOException {
    	ExtHash<String, String> eh2 = new ExtHash<String, String>();
        ArrayList<String> keys = new ArrayList<String>();
        ArrayList<String> values = new ArrayList<String>();
        Scanner reader = new Scanner(new File("911.csv"));
        reader.nextLine();
        String sep = ",";

        while(reader.hasNextLine()) {
            String [] line = reader.nextLine().split(sep);
            keys.add(line[2]);
            values.add(line[4]);
            eh2.put(line[2], line[4]);
        }
        
        ArrayList<Long> times = new ArrayList<>();
        
        for(int i=5000;i<keys.size();i+=100000) {
    		List<String> keysSub = keys.subList(0, i);
    		List<String> valuesSub = values.subList(0, i);
    		int size = keysSub.size();
            
    		
            List<String> key1 = keysSub.subList(0,(int)size/3);
            List<String> key2 = keysSub.subList((int)size/3,(int) 2*size/3);
            List<String> key3 = keysSub.subList((int) 2*size/3,size);
            
            List<String> value1 = valuesSub.subList(0,(int)size/3);
            List<String> value2 = valuesSub.subList((int)size/3 + 1,(int) 2*size/3);
            List<String> value3 = valuesSub.subList((int) 2*size/3,size);
            
            MyThread m1 = new MyThread("1", eh2, key1, value1, "Search");
            Thread t1 = new Thread(m1);
            Thread t2 = new Thread(new MyThread("2", eh2, key2, value2 ,"Search"));
            Thread t3 = new Thread(new MyThread("3", eh2, key3, value3 ,"Search"));
            
            long start = System.nanoTime();
            t1.start();
            t2.start();
            t3.start();

            t1.join();
            t2.join();
            t3.join();
            long finish = System.nanoTime();
            
            times.add(finish - start);
            
            t1.interrupt();
            t2.interrupt();
            t3.interrupt();
    	}
        
        PrintWriter pw=new PrintWriter(new FileWriter(filename));
	    pw.println(times.toString());
		pw.close();
    }
    
    public static void main(String[] args) throws InterruptedException, IOException {

        //writeInsertPerformance("insertMulti_time.txt");
    	writeAccessPerformance("accessMulti_time.txt");
    }
}


