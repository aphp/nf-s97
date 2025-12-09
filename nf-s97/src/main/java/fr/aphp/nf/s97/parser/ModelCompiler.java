package fr.aphp.nf.s97.parser;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fr.aphp.lang.factory.Factory;
import fr.aphp.nf.s97.model.Efs;
import fr.aphp.nf.s97.processor.struct.Element;
import fr.aphp.nf.s97.processor.struct.Type;

public class ModelCompiler
{

   private static Logger LOG = LoggerFactory.getLogger(ModelCompiler.class);

   public static void compile() throws Exception{

      ObjectMapper m = new ObjectMapper();
      var rn = m.createObjectNode();

      Map<String, Function<JsonNode, Factory<fr.aphp.nf.s97.parser.Element>>> type =
         Map.of(Element.REPETITION, Repetition::of, Element.SEGMENT, Segment::of, Element.VALUE, Value::of);

      Map<Class<?>, String> classTag = new HashMap<>();

      Map<Class<?>, Set<Class<?>>> acceptIn = new HashMap<>();

      Map<String, Set<Class<?>>> acceptTag = new HashMap<>();

      classTag.put(Efs.class, ""); //classe racine

      compile(Efs.class, classTag, acceptIn, acceptTag, rn, m);

      for(Entry<Class<?>, Set<Class<?>>> e : acceptIn.entrySet()){
         LOG.debug(e.getKey() + "=> " + e.getValue());

         var t1 = e.getKey().getAnnotation(Element.class).tag();

         for(Class<?> clazz : e.getValue()){
            if(clazz.isAnnotationPresent(Element.class)){
               var t2 = clazz.getAnnotation(Element.class).tag();
               ArrayNode.class.cast(rn.get(t1).get("in")).add(t2);
            }

         }

      }
      if(LOG.isDebugEnabled()){
         LOG.debug("====");
         for(Entry<Class<?>, String> e : classTag.entrySet()){
            LOG.debug(e.getKey() + "=> " + e.getValue());
         }
      }

      Parser.accept = acceptIn.entrySet().stream().filter(e -> classTag.containsKey(e.getKey())).collect(Collectors.toMap(
         //key
         e -> classTag.get(e.getKey()),
         //value
         e -> e.getValue().stream().filter(i -> classTag.containsKey(i)).map(i -> classTag.get(i)).collect(Collectors.toSet())));
      Parser.accept.put("root", Set.of());

      Parser.accept.putAll(

         acceptTag.entrySet().stream().collect(Collectors.toMap(
            //key
            e -> e.getKey(),
            //value
            e -> e.getValue().stream().filter(i -> classTag.containsKey(i)).map(i -> classTag.get(i))
               .collect(Collectors.toSet()))));

      if(LOG.isDebugEnabled()){
         LOG.debug("====");
         for(Entry<String, Set<String>> e : Parser.accept.entrySet()){
            LOG.debug(e.getKey() + "=> " + e.getValue());
         }
      }

      for(JsonNode node : rn){
         var tag = node.get("tag").asText();
         var atype = node.get("type").asText();

         Parser.factory.register(tag, type.get(atype).apply(node));

      }

      String json = m.writerWithDefaultPrettyPrinter().writeValueAsString(rn);

      // print json
      LOG.debug(json);

   }

   private static void compile(Class<?> clazz, Map<Class<?>, String> classTag, Map<Class<?>, Set<Class<?>>> acceptIn,
      Map<String, Set<Class<?>>> acceptTag, ObjectNode rn, ObjectMapper m){

      if(clazz.isAnnotationPresent(Element.class)){
         var e = clazz.getAnnotation(Element.class);

         if(!acceptIn.containsKey(clazz)){
            acceptIn.put(clazz, new HashSet<Class<?>>());
         }
         classTag.put(clazz, e.tag());
         var en = createElementModel(m, e);
         rn.set(e.tag(), en);
      }

      for(Field field : clazz.getFields()){
         if(!java.lang.reflect.Modifier.isStatic(field.getModifiers())){
            if(field.isAnnotationPresent(Element.class)){
               var e = field.getAnnotation(Element.class);

               if(!acceptTag.containsKey(e.tag())){
                  acceptTag.put(e.tag(), new HashSet<Class<?>>());
               }
               acceptTag.get(e.tag()).add(clazz);

               var en = createElementModel(m, e);
               rn.set(e.tag(), en);
               if(clazz.isAnnotationPresent(Element.class)){
                  var t2 = clazz.getAnnotation(Element.class).tag();

                  ArrayNode.class.cast(en.get("in")).add(t2);
               }
            }
            if(field.isAnnotationPresent(Type.class)){

               var e = field.getAnnotation(Type.class);

               if(!acceptIn.containsKey(e.value())){
                  acceptIn.put(e.value(), new HashSet<Class<?>>());
               }
               acceptIn.get(e.value()).add(clazz);

               compile(e.value(), classTag, acceptIn, acceptTag, rn, m);
            }

            if(!String.class.equals(field.getType()) && !List.class.equals(field.getType())){

               if(!acceptIn.containsKey(field.getType())){
                  compile(field.getType(), classTag, acceptIn, acceptTag, rn, m);
               }
               acceptIn.get(field.getType()).add(clazz);
            }
         }
      }
   }

   private static ObjectNode createElementModel(ObjectMapper m, Element e){
      var en = m.createObjectNode();
      en.put("tag", e.tag());
      en.put("type", e.type());
      en.put("name", e.name());
      var in = m.createArrayNode();
      en.set("in", in);
      return en;
   }
}
