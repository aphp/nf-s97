package fr.aphp.nf.s97.converter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.camel.Converter;

import fr.aphp.nf.s97.model.Efs;
import fr.aphp.nf.s97.parser.Parser;
import fr.aphp.nf.s97.parser.mapper.EfsMapper;
import fr.aphp.nf.s97.serializer.Serializer;

@Converter(generateLoader = true, ignoreOnLoadError = true)
public class AfnorConverter
{

   @Converter
   public static OutputStream fromEFS(Efs efs) throws IOException{
      var os = new ByteArrayOutputStream();
      Serializer.serialize(efs, os);
      return os;
   }

   @Converter
   public static Efs toEFS(InputStream is) throws IOException{
      var root = Parser.parse(is);
      var efs = EfsMapper.map(root);
      return efs;
   }

   @Converter
   public static String fromEFSToString(Efs efs) throws IOException{
      var os = new ByteArrayOutputStream();
      Serializer.serialize(efs, os);
      return new String(os.toByteArray());
   }

   @Converter
   public static Efs toEFS(String s) throws IOException{
      ByteArrayInputStream is = new ByteArrayInputStream(s.getBytes());
      var root = Parser.parse(is);
      var efs = EfsMapper.map(root);
      return efs;
   }
}
