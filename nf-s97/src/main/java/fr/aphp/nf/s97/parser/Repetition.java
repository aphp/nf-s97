package fr.aphp.nf.s97.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;

import fr.aphp.lang.factory.Factory;

public class Repetition implements Recipient
{

   String name;

   String tag;

   List<Element> childrens = new ArrayList<Element>();

   @JsonIgnore
   private Recipient parent;

   private Repetition(String name){
      this.name = name;
      parent = this;
   }

   public static Function<String, Element> of(fr.aphp.nf.s97.processor.struct.Element e){
      return t -> new Repetition(e.name()).tag(t);
   }

   public static Factory<Element> of(JsonNode node){
      var name = node.get("name").asText();
      var tag = node.get("tag").asText();

      return () -> new Repetition(name).tag(tag);
   }

   public Repetition tag(String s){
      tag = s;
      return this;
   }

   public Repetition value(String v){
      return this;
   }

   @Override
   public String toString(){
      return "\nList   : " + tag + childrens;
   }

   public Recipient on(Recipient current){

      parent = current;

      if(parent.getChildren().isEmpty() || !tag.equals(parent.getChildren().get(parent.getChildren().size() - 1).tag())){
         parent.getChildren().add(this);
         return Segment.of(name, tag).on(this);
      }else{
         return Segment.of(name, tag).on(Recipient.class.cast(parent.getChildren().get(parent.getChildren().size() - 1)));
      }
   }

   public List<Element> getChildren(){
      return childrens;
   }

   @Override
   public String tag(){
      return tag;
   }

   @Override
   public Recipient parent(){
      return parent;
   }

   @Override
   public String name(){
      return name;
   }

}
