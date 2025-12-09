package fr.aphp.nf.s97.serializer.mapper;

import static fr.aphp.nf.s97.serializer.Writer.segment;

import java.io.IOException;
import java.io.OutputStream;

import fr.aphp.nf.s97.model.Efs;

public class EfsMapper
{

   public static void map(Efs efs, OutputStream os) throws IOException{
      ElementMapper.map(efs.header, os);
      ElementMapper.map(efs.body, os);
      segment("zz", os);
      //os.write(NL);
      return;
   }

}
