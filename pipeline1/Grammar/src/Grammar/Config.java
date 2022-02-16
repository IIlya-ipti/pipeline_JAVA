package Grammar;

import java.util.Arrays;
import java.util.Objects;

public class Config {
    private int length = -1;
    Config[] Elements = null;
    private Object KEY = null;
    public Config(){};
    public Config(String c){
        KEY = c;
    }
    public int get_length(){return length + 1;}
    public void Add(String[] k){
        if(k.length == 0)return;

        // ищем по ключу под дерево которое нам нужно
        Config key = Search(k[0]);

        // если такого ключа нет, то мы его создаем
        if(key == null){
            Add_to_array(k);
        }else{
            key.Add(Arrays.copyOfRange(k,1,k.length));
        }
    }
    private void Add_to_array(String[] k){
        if(k == null || k.length == 0)return;
        if(Elements == null){
            Elements = new Config[1];
        }else{
            Elements = Arrays.copyOf(Elements, (Elements.length*3)/2 + 1);
        }
        Elements[++length] = new Config(k[0]);
        // вызываем метод Add
        Elements[length].Add_to_array(Arrays.copyOfRange(k,1,k.length));
    }
    public Config Search(String key){
        // поиск из ключей массива
        if (Elements == null) return null;
        for (Config element : Elements) {
            if(element != null) {
                if (Objects.equals(element.KEY, key)) {
                    return element;
                }
            }
        }
        return null;
    }
    public Config[] get_array(){
        return Elements;
    }
    public Object get_KEY(){
        return KEY;
    }
    public String[] key_set(){
        String[] keys = new String[length + 1];
        for (int i = 0; i <= length ; i++){
            keys[i] = (String) Elements[i].KEY;
        }
        return keys;
    }

}
