import java.util.Objects;

public class Data<K,V> {
    public K key;
    public V value;
    Data<K,V> next;

    public Data(K key, V value){
        this.key = key;
        this.value = value;
    }

    public void setKey(K key){
        this.key = key;
    }

    public K getKey(){
        return this.key;
    }

    public void setValue(V value){
        this.value = value;
    }

    public V getValue(){
        return this.value;
    }

    @Override
    public String toString() {
        return "Data{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }


    public boolean equals(Object other){
        if (other instanceof Data<?,?>){
            if (((Data<?,?>)other).key.equals(key)){
                return true;
            }
        }
        return false;
    }
}
