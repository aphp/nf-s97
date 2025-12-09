package fr.aphp.nf.s97.serializer;

import java.io.IOException;
import java.io.OutputStream;

public class Writer
{

   public static byte[] NL = "\n".getBytes();

   public static void segment(String tag, OutputStream os) throws IOException{
      os.write(tag.getBytes());
      os.write(NL);
   }

   public static void value(String tag, Object o, OutputStream os) throws IOException{
      if(null != o){
         os.write((tag + "," + o.toString()).getBytes());
         os.write(NL);
      }
   }
}
