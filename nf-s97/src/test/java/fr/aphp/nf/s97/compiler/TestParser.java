package fr.aphp.nf.s97.compiler;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import fr.aphp.nf.s97.model.Efs;
import fr.aphp.nf.s97.parser.Parser;
import fr.aphp.nf.s97.parser.mapper.EfsMapper;

public class TestParser
{

   private ObjectWriter jsonWriter;

   @BeforeEach
   public void init(){
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.setSerializationInclusion(NON_EMPTY);
      jsonWriter = objectMapper.writer(new DefaultPrettyPrinter().withObjectIndenter(new DefaultIndenter().withLinefeed("\n")));
   }

   @Test
   public void testDel() throws Exception{

      var u = getClass().getClassLoader().getResource("del.afn");
      var res = readFile("del.json", StandardCharsets.UTF_8);
      var root = Parser.parse(Path.of(u.toURI()));
      Efs efs = EfsMapper.map(root);

      String json = jsonWriter.writeValueAsString(efs);
      assertEquals(res, json);
   }

   @Test
   public void testRDel() throws Exception{
      var u = getClass().getClassLoader().getResource("rdel.afn");
      var res = readFile("rdel.json", StandardCharsets.UTF_8);
      var root = Parser.parse(Path.of(u.toURI()));

      Efs efs = EfsMapper.map(root);

      String json = jsonWriter.writeValueAsString(efs);
      assertEquals(res, json);

   }

   private static String readFile(String fileName, Charset charEncoding) throws Exception{
      URL url = TestParser.class.getClassLoader().getResource(fileName);
      // Récupération du fichier
      final Path path = Paths.get(url.toURI());

      return Files.readString(path, charEncoding);
   }
}
