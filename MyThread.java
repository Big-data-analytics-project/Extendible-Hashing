import java.util.*;

public class MyThread<K,V> implements Runnable {
    String name;
    Thread t;
    ExtHash eh;
    List<String> title;
    List<String> author;
    String operation;
    K key;
    List<Long> times = new ArrayList<>();

    public MyThread(){};

    public MyThread(String name, ExtHash eh,  List<String> title,List<String> author ,String operation) {
        this.name = name;
        this.eh = eh;
        this.title = title;
        this.author = author;
        this.operation = operation;
    }

    public void run() {
        if (operation == "Insert") {
            for (int i = 0; i < title.size(); i++) {
                long start = System.currentTimeMillis();
                eh.put(title.get(i), author.get(i));
                long finish = System.currentTimeMillis();
                times.add(finish - start);
            }
        } else if (operation == "Search") {
            eh.getValue(key);
        }
    }

    public List<Long> getList(){
        return times;
    }

}

