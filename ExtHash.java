// << +1 || >> -1
import java.util.*;

public class ExtHash<K,V> {
    static class Bucket<K,V> {
        int localdepth = 0;
        static int bucket_size = 3;
        private MyHashMap bucket = new MyHashMap<K,V>();
        List<K> keyset = new ArrayList<K>();

        public void put(Data<K,V> x){
            // put data in bucket and create keyset
            if(!(keyset.contains(x.key))){
                keyset.add(x.key);
                //System.out.println(keyset);
            }
            bucket.addData(x);
        }

        public boolean isFull(){
            return bucket.getSize() > bucket_size;
        }

        @Override
        public String toString() {
            return  "{ bucket=" + bucket + ", localdepth=" +  localdepth + "}\n";
        }
    }

    int globaldepth = 0;
    List<Bucket<K,V>> bucketlist = new ArrayList<Bucket<K,V>>();

    public ExtHash(){
        bucketlist.add(new Bucket<K,V>());
    }

    public static <K> String hashcode(K k) {
        // convert key to binary and return it. for now it only work with integer keys.
        // if we will have string keys as well we have to find a way to convert string to integers.
        String hashcode = Integer.toBinaryString((int) k);
        return hashcode;
    }

    public Bucket<K,V> getBucket(K key){ // get bucket based on hashcode(key)
        int hashcode = Integer.parseInt(hashcode(key));
        Bucket<K,V> b = bucketlist.get(hashcode & (1 << globaldepth) - 1);
        return b;
    }

    public void put(K key, V value){
        Bucket<K,V> b = getBucket(key);
        //System.out.println(b);
        if(b.localdepth == globaldepth && b.isFull()){
            //in this case we double the buckets and we increase globaldepth.
            List<Bucket<K,V>> t2 = new ArrayList<ExtHash.Bucket<K,V>>(bucketlist);
            bucketlist.addAll(t2);
            globaldepth++;
        }

        if(b.localdepth < globaldepth && b.isFull()){
            // in this case we dont have to double the no of buckets.. we just have to split the current bucket because its full..
            Data<K,V> d = new Data<K,V>(key,value);
            b.put(d);
            //split data of bucket b to buckets b1 and b2
            Bucket<K,V> b1 = new Bucket<K,V>();
            Bucket<K,V> b2 = new Bucket<K,V>();

            for(K key2 : b.keyset){
                V value2 = (V) b.bucket.getData(key2);
                Data<K,V> d2 = new Data<K,V>(key2,value2);

                int hashcode = Integer.parseInt(hashcode(key2)) & ((1 << globaldepth) - 1);
                //System.out.println(hashcode);
                if((hashcode | (1 << b.localdepth)) == hashcode){
                    b2.put(d2);
                }else{
                    b1.put(d2);
                }
            }

            List<Integer> l = new ArrayList<Integer>();
            for(int i=0; i<bucketlist.size(); i++){
                if(bucketlist.get(i) == b) {
                    l.add(i);
                }
            }
            for(int i : l){
                if((i | (1 << b.localdepth)) == i){
                    bucketlist.set(i, b2);
                }else{
                    bucketlist.set(i,b1);
                }
            }
            b1.localdepth = b.localdepth + 1;
            b2.localdepth = b.localdepth + 1;
        }else{
            //if we are not in the above cases just add the data to hashmap
            Data<K,V> d = new Data<K,V>(key,value);
            b.put(d);
        }
    }

    @Override
    public String toString() {
        return "ExtHash{" +
                "globaldepth=" + globaldepth +
                ",\n " + bucketlist + '}'; }

    public static void main(String [] args){
        //this example is from anadiotis slides..
        ExtHash<Integer, Integer> eh = new ExtHash<Integer, Integer>();
        eh.put(4, 4);
        eh.put(1, 1);
        eh.put(12, 12);
        eh.put(32, 32);
        eh.put(16, 16);
        eh.put(5, 5);
        eh.put(21, 21);
        eh.put(13, 13);
        eh.put(10, 10);
        eh.put(15, 15);
        eh.put(7, 7);
        eh.put(19, 19);
        System.out.println(eh.toString());
        eh.put(20, 20);
        System.out.println(eh.toString());
    }
}