package fr.aphp.nf.s97.parser;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Lexer implements Closeable, Iterator<Lexer.Token>
{

   private Stream<String> input;

   private Iterator<String> iterator;

   private String value;

   private Lexer(Path path) throws IOException{
      input = Files.lines(path);
      iterator = input.iterator();
   }

   public Lexer(InputStream is) throws IOException{
      List<String> lines;
      try( InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
         BufferedReader br = new BufferedReader(isr);){
         lines = br.lines().collect(Collectors.toList());
      }
      input = lines.stream();
      iterator = input.iterator();
   }

   public static Lexer of(Path path) throws IOException{
      return new Lexer(path);

   }

   public static Lexer of(InputStream is) throws IOException{
      return new Lexer(is);

   }

   @Override
   public void close() throws IOException{
      input.close();
   }

   @Override
   public boolean hasNext(){
      return (null != value) || iterator.hasNext();
   }

   @Override
   public Token next(){
      var s = iterator.next();
      var t = new Token();
      t.tag = s.substring(0, 2);
      if(s.length() > 2){
         t.value = s.substring(3, s.length());
      }
      return t;
   }

   public class Token
   {
      public String tag;

      public String value;
   }
}
