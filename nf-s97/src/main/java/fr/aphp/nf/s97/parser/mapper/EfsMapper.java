package fr.aphp.nf.s97.parser.mapper;

import java.util.Map;
import java.util.function.BiConsumer;

import fr.aphp.nf.s97.model.Body;
import fr.aphp.nf.s97.model.Efs;
import fr.aphp.nf.s97.model.Header;
import fr.aphp.nf.s97.parser.Segment;

public class EfsMapper
{

   public static Efs map(Segment root){

      var efs = new Efs();

      root.getChildren().forEach(e -> {
         var s = Segment.class.cast(e);
         M.get(s.name()).accept(s, efs);
      });
      return efs;
   }

   private static Map<String, BiConsumer<Segment, Efs>> M = Map.of(

      "header", (s, d) -> {
         d.header = new ElementMapper<>(Header.class).map(s);
      }, "body", (s, d) -> {
         d.body = new ElementMapper<>(Body.class).map(s);
      }, "end", (s, d) -> {});

}
