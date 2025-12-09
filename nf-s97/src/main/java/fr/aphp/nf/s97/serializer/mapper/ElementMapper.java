package fr.aphp.nf.s97.serializer.mapper;

import static fr.aphp.nf.s97.serializer.Writer.segment;
import static fr.aphp.nf.s97.serializer.Writer.value;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import fr.aphp.nf.s97.processor.struct.Element;
import fr.aphp.nf.s97.processor.struct.Type;

public class ElementMapper
{
   public static <T> void map(T element, OutputStream os) throws IOException{
      if(Objects.nonNull(element)){
         Class<?> c = element.getClass();
         if(c.isAnnotationPresent(Element.class)){
            var e = c.getAnnotation(Element.class);
            segment(e.tag(), os);

            Stream.of(c.getFields()).filter(f -> !Modifier.isStatic(f.getModifiers())).forEach(f -> {
               if(f.isAnnotationPresent(Element.class)){
                  var a = f.getAnnotation(Element.class);
                  try{
                     value(a.tag(), f.get(element), os);
                  }catch(Exception e1){}
               }else if(f.isAnnotationPresent(Type.class)){
                  //var a = f.getAnnotation(Type.class);
                  try{
                     List<?> l = (List<?>) f.get(element);
                     for(Object o : l){
                        map(o, os);
                     }
                     map(f.get(element), os);
                  }catch(Exception e1){}
               }else{
                  try{
                     map(f.get(element), os);
                  }catch(Exception e1){}
               }
            });

         }
      }
      //      segment("aa", os);
      //      HeadMapper.map(efs.header, os);
      //      BodyMapper.map(efs.body, os);
      //      segment("zz", os);
      //      os.write(NL);
      //      return;
   }

}
