package fr.aphp.nf.s97.parser;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;

import fr.aphp.lang.factory.Factory;

public class Segment implements Recipient
{

   public Segment(){
      super();
   }

   String name;

   String tag;

   List<Element> childrens = new ArrayList<Element>();

   @JsonIgnore
   private Recipient parent;

   private Segment(String name/*, String... accept*/){
      this.name = name;
      parent = this;
   }

   public static Segment of(String name, String tag){
      return new Segment(name/*, accept*/).tag(tag);
   }

   public static Factory<Element> of(JsonNode node){
      var name = node.get("name").asText();
      var tag = node.get("tag").asText();
      return () -> new Segment(name).tag(tag);
   }

   public Segment tag(String s){
      tag = s;
      return this;
   }

   public Segment value(String v){
      return this;
   }

   @Override
   public String toString(){
      return "\nSegment: " + tag + childrens;
   }

   public Recipient on(Recipient current){

      parent = current;
      parent.getChildren().add(this);
      return this;
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
