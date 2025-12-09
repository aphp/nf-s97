package fr.aphp.nf.s97.parser;

import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;

import fr.aphp.lang.factory.Factory;

public class Value implements Element
{

   String name;

   String tag;

   String value;

   private Value(String name/*, String... accept*/){
      this.name = name;
   }

   public static Function<String, Element> of(fr.aphp.nf.s97.processor.struct.Element e){
      return t -> new Value(e.name()).tag(t);
   }

   public static Factory<Element> of(JsonNode node){
      var name = node.get("name").asText();
      var tag = node.get("tag").asText();
      return () -> new Value(name).tag(tag);
   }

   public Value tag(String t){
      tag = t;
      return this;
   }

   public String value(){
      return value;
   }

   public Value value(String v){
      value = v;
      return this;
   }

   @Override
   public String toString(){
      return "\nValue  : " + tag + "," + value;
   }

   public Recipient on(Recipient current){
      current.getChildren().add(this);
      return current;
   }

   @Override
   public String tag(){
      return tag;
   }

   @Override
   public String name(){
      return name;
   }

}
