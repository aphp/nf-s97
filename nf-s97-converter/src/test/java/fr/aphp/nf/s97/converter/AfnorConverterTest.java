package fr.aphp.nf.s97.converter;

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

public class AfnorConverterTest
{

   private ObjectWriter jsonWriter;

   @BeforeEach
   public void init(){
      final ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.setSerializationInclusion(NON_EMPTY);
      jsonWriter = objectMapper.writer(new DefaultPrettyPrinter().withObjectIndenter(new DefaultIndenter().withLinefeed("\n")));

   }

   @SuppressWarnings("resource")
   @Test
   public void testConvertDel() throws Exception{
      final var is = getClass().getClassLoader().getResourceAsStream("del.afn");

      final var res = readFile("del.json", StandardCharsets.UTF_8);

      final Efs efs = AfnorConverter.toEFS(is);

      final String json = jsonWriter.writeValueAsString(efs);
      assertEquals(res, json);
   }

   @SuppressWarnings("resource")
   @Test
   public void testConvertRDel() throws Exception{
      final var is = getClass().getClassLoader().getResourceAsStream("rdel.afn");
      final var res = readFile("rdel.json", StandardCharsets.UTF_8);

      final Efs efs = AfnorConverter.toEFS(is);

      final String json = jsonWriter.writeValueAsString(efs);
      assertEquals(res, json);
   }

   private static String readFile(final String fileName, final Charset charEncoding) throws Exception{
      final URL url = AfnorConverterTest.class.getClassLoader().getResource(fileName);
      // Récupération du fichier
      final Path path = Paths.get(url.toURI());

      return Files.readString(path, charEncoding);
   }
}
