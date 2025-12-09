package fr.aphp.nf.s97.serializer;

import java.io.IOException;
import java.io.OutputStream;

import fr.aphp.nf.s97.model.Efs;
import fr.aphp.nf.s97.serializer.mapper.EfsMapper;

public class Serializer
{

   public static void serialize(Efs efs, OutputStream os) throws IOException{
      EfsMapper.map(efs, os);
   }
}
