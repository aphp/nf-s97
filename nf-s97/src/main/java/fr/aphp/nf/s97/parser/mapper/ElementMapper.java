package fr.aphp.nf.s97.parser.mapper;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import fr.aphp.nf.s97.parser.Element;
import fr.aphp.nf.s97.parser.Recipient;
import fr.aphp.nf.s97.parser.Repetition;
import fr.aphp.nf.s97.parser.Segment;
import fr.aphp.nf.s97.parser.Value;
import fr.aphp.nf.s97.processor.struct.Type;

public class ElementMapper<T>
{

   private Map<String, BiConsumer<Element, T>> consumers = new HashMap<>();

   private Class<T> clazz;

   public ElementMapper(Class<T> c){

      this.clazz = c;

      Stream.of(c.getFields()).filter(f -> !Modifier.isStatic(f.getModifiers())).forEach(f -> {

         if(f.isAnnotationPresent(fr.aphp.nf.s97.processor.struct.Element.class)){
            consumers.put(f.getName(), (v, d) -> {
               try{
                  var o = Value.class.cast(v).value();
                  f.set(d, o);
               }catch(IllegalArgumentException | IllegalAccessException e){}
            });

         }else if(f.isAnnotationPresent(Type.class)){
            var a = f.getAnnotation(Type.class);
            consumers.put(f.getName(), (v, d) -> {
               try{

                  var r = Repetition.class.cast(v);

                  var l = new ArrayList<Object>(r.getChildren().size());

                  f.set(d, l);

                  for(Element child : r.getChildren()){
                     Object o = new ElementMapper<>(a.value()).map(Segment.class.cast(child));
                     l.add(o);
                  }
               }catch(IllegalArgumentException | IllegalAccessException e){
                  e.printStackTrace();
               }
            });

         }else{
            consumers.put(f.getName(), (v, d) -> {
               try{
                  var o = new ElementMapper<>(f.getType()).map(Segment.class.cast(v));
                  f.set(d, o);
               }catch(IllegalArgumentException | IllegalAccessException e){}
            });

         }
      });
   }

   public T map(Recipient s){
      try{
         T segment = clazz.getConstructor().newInstance();

         s.getChildren().forEach(e -> {
            var a = consumers.get(e.name());
            a.accept(e, segment);
         });
         return segment;
      }catch(Exception e){
         e.printStackTrace();
         return null;
      }
   }
}
