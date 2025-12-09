package fr.nf.s97.maven;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class NFS97Generator extends AbstractMojo
{

   @Parameter(defaultValue = "${project}", required = true, readonly = true)
   MavenProject project;

   @Parameter(property = "specFile", defaultValue = "${basedir}/src/main/model/s97.tab", required = true)
   Path specFile;

   @Parameter(property = "genDir", defaultValue = "${basedir}/target/generated-sources/main/java/", required = true,
      readonly = true)
   Path genDir;

   @Parameter(property = "pkgDir", defaultValue = "${basedir}/target/generated-sources/main/java/fr/aphp/nf/s97/model",
      required = true, readonly = true)
   Path pkgDir;

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException{

      try{
         getLog().debug("path: " + specFile);
         getLog().debug("path: " + genDir);
         getLog().debug("path: " + pkgDir);
         if(!Files.exists(pkgDir)){
            Files.createDirectories(pkgDir);
         }
         project.addCompileSourceRoot(genDir.toString());
         generate();
      }catch(Exception e){
         getLog().error(e.getClass().getName() + ": " + e.getMessage());
         e.printStackTrace();
         throw new MojoExecutionException(e.getClass().getName() + ": " + e.getMessage(), e);
      }
   }

   public void generate() throws Exception{

      Map<String, Definition> m = new HashMap<>();
      try( var ls = Files.lines(specFile.normalize())){
         ls.filter(l -> !l.startsWith("tag")).map(l -> l.split("\t")).map(a -> new Definition(a))
            .forEachOrdered(d -> m.put(d.tag, d));
      }

      m.entrySet().stream().filter(e -> e.getValue().format.isBlank()).forEach(e -> {

         Definition def = e.getValue();

         var seq = def.occurence.equals("U");
         var tag = def.tag;
         var name = def.name;

         var type = def.type;
         // error on windows => C:\name.java à la place de c:\path\to\project\target\...\name.java
         //var classFile = pkgDir.resolve("/" + name + ".java");
         var classFile = Paths.get(pkgDir.toString() + "/" + type + ".java");

         getLog().debug("path: " + classFile);
         getLog().info("create " + type + " class tag: " + tag);

         var sb = new StringBuilder();

         sb.append("package fr.aphp.nf.s97.model;\n\n");
         sb.append("import java.util.List;\n");
         sb.append("import javax.annotation.processing.Generated;\n\n");

         sb.append("import fr.aphp.nf.s97.processor.struct.Element;\n");
         sb.append("import fr.aphp.nf.s97.processor.struct.Type;\n\n");

         if(seq){
            sb.append("import static fr.aphp.nf.s97.processor.struct.Element.SEGMENT;\n");
            sb.append("import static fr.aphp.nf.s97.processor.struct.Element.VALUE;\n\n");
            sb.append("@Generated(\"NF S97 Generator\")\n");
            sb.append("@Element(tag = \"");
            sb.append(tag);
            sb.append("\", name = \"");
            sb.append(name);
            sb.append("\", type = SEGMENT)\n");
         }else{
            sb.append("import static fr.aphp.nf.s97.processor.struct.Element.REPETITION;\n");
            sb.append("import static fr.aphp.nf.s97.processor.struct.Element.VALUE;\n\n");
            sb.append("@Generated(\"NF S97 Generator\")\n");
            sb.append("@Element(tag = \"");
            sb.append(tag);
            sb.append("\", name = \"");
            sb.append(name);
            sb.append("\", type = REPETITION)\n");
         }
         sb.append("public class ");
         sb.append(def.type);
         sb.append("\n{\n");

         def.fields.stream().forEach(i -> {

            var id = m.get(i);
            if(null == id){
               throw new RuntimeException("Invalid spec file content: " + id + " fiedl " + i + " undefined");
            }
            if(id.format.isBlank()){
               if(id.occurence.equals("U")){
                  sb.append("    //tag = \"");
                  sb.append(id.tag);
                  sb.append("\"\n");
                  sb.append("    public ");
                  sb.append(id.type);
                  sb.append(" ");
                  sb.append(id.name);
                  sb.append(";");
               }else{
                  sb.append("    //tag = \"");
                  sb.append(id.tag);
                  sb.append("\"\n");
                  sb.append("    @Type(");
                  sb.append(id.type);
                  sb.append(".class)\n");
                  sb.append("    public List<");
                  sb.append(id.type);
                  sb.append("> ");
                  sb.append(id.name);
                  sb.append(" = List.of();");
               }
            }else{
               sb.append("    @Element(tag = \"");
               sb.append(id.tag);
               sb.append("\", name = \"");
               sb.append(id.name);
               sb.append("\", type = VALUE)\n");
               sb.append("    public ");
               sb.append("String ");
               sb.append(id.name);
               sb.append("; //");
               sb.append(id.format);
            }
            sb.append("\n\n");
         });

         sb.append("}\n");

         try( var fos = new FileOutputStream(classFile.toFile());
            var writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8)){
            writer.write(sb.toString());
         }catch(Exception e1){
            throw new RuntimeException(e.getClass().getName() + ": " + e1.getMessage(), e1);
         }

      });
      ;
   }

   public static class Definition
   {

      private static final Pattern P = Pattern.compile("^.");

      public String tag;

      public String name;

      public String type;

      public String format;

      public String description;

      public String required;

      public String occurence;

      public List<String> fields;

      public Definition(String[] a){

         tag = of(a, 0);
         name = of(a, 1);
         type = P.matcher(name).replaceFirst(m -> m.group().toUpperCase());
         format = of(a, 3);
         required = of(a, 4);
         occurence = of(a, 5);

         description = of(a, 8);
         fields = listOf(a, 9);
      }

      @Override
      public String toString(){
         return "Definition [tag=" + tag + ", name=" + name + ", type=" + type + ", format=" + format + ", description="
            + description + ", required=" + required + ", occurence=" + occurence + ", fields=" + fields + "]";
      }

      private static String of(String[] a, int i){
         return (a.length > i && null != a[i]) ? a[i] : "";
      }

      private static List<String> listOf(String[] a, int i){
         return (a.length > i) ? new ArrayList<>(List.of(a[i].replaceAll(" ", "").split(","))) : new ArrayList<>();
      }

   }
}
