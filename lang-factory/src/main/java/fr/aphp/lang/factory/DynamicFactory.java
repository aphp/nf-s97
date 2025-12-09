package fr.aphp.lang.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DynamicFactory<K, T>
{

   Map<K, Factory<T>> registry = new HashMap<>();

   public T create(K k){
      if(containsKey(k)){
         return registry.get(k).create();
      }else{
         throw new UnsupportedOperationException(k + " notFound");
      }
   }

   public void register(K k, Class<? extends T> c){
      registry.put(k, () -> {
         try{
            return c.getConstructor().newInstance();
         }catch(Throwable e){
            throw new UnsupportedOperationException(k + " instanciate error", e);
         }
      });
   }

   public void register(K k, Factory<T> f){
      registry.put(k, f);
   }

   public Object Unregister(K key){
      return registry.remove(key);
   }

   public int size(){
      return registry.size();
   }

   public boolean isEmpty(){
      return registry.isEmpty();
   }

   public boolean containsKey(K key){
      return registry.containsKey(key);
   }

   public void putAll(Map<K, Factory<T>> m){
      m.entrySet().stream().forEach(e -> register(e.getKey(), e.getValue()));
   }

   public void clear(){
      registry.clear();
   }

   public Set<K> keySet(){
      return registry.keySet();
   }

   public Set<Map.Entry<K, Factory<T>>> entrySet(){
      return registry.entrySet();
   }
}
