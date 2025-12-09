package fr.aphp.nf.s97.parser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.aphp.lang.factory.DynamicFactory;

public class Parser
{

   public static Segment root = Segment.of("root", "root");

   public static DynamicFactory<String, Element> factory = new DynamicFactory<>();

   public static Map<String, Set<String>> accept = new HashMap<>();

   private static Logger LOG = LoggerFactory.getLogger(Parser.class);

   static Recipient current = root;

   static{
      try{
         ModelCompiler.compile();
      }catch(Exception e){
         LOG.error(e.getMessage(), e);
      }
   }

   public static Segment parse(Path path) throws IOException{
      try( var l = Lexer.of(path);){
         parse(l);
      }catch(IOException e){
         LOG.error(e.getMessage(), e);
         throw e;
      }
      return root;
   }

   public static Segment parse(InputStream is) throws IOException{
      try( var l = Lexer.of(is);){
         parse(l);
      }catch(IOException e){
         LOG.error(e.getMessage(), e);
         throw e;
      }
      return root;
   }

   public static void parse(Lexer l){
      while(l.hasNext()){
         var t = l.next();

         Recipient recipient = findRecipient(current, t.tag);
         if(!Objects.isNull(recipient)){
            current = factory.create(t.tag).value(t.value).on(recipient);
         }else{
            LOG.warn(t.tag + " => ignored");
         }
      }
   }

   private static Recipient findRecipient(Recipient current, String tag){

      if(accept.get(tag).contains(current.tag())){
         return current;
      }else if(current != current.parent()){
         return findRecipient(current.parent(), tag);
      }
      return null;
   }

}
