package fr.aphp.nf.s97.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes({"fr.aphp.nf.s97.processor.struct.Element", "fr.aphp.nf.s97.processor.struct.Type"})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class ModelProcessor extends AbstractProcessor
{

   Map<String, String> classNames = new HashMap<>();

   Map<String, Map<String, String>> elementNames = new HashMap<>();

   @Override
   public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv){
      if(annotations.size() == 0){
         return false;
      }

      Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(fr.aphp.nf.s97.processor.struct.Element.class);
      for(Element element : elements){

         if(element.getKind() == ElementKind.CLASS){
            fr.aphp.nf.s97.processor.struct.Element afnE = element.getAnnotation(fr.aphp.nf.s97.processor.struct.Element.class);
            classNames.put(element.asType().toString(), afnE.name());
            Map<String, String> m = new HashMap<>();
            elementNames.put(element.asType().toString(), m);

            List<? extends Element> l = element.getEnclosedElements();

            for(Element element2 : l){
               if((element2.getKind() == ElementKind.FIELD) && (!element2.getModifiers().contains(Modifier.STATIC))){
                  fr.aphp.nf.s97.processor.struct.Element afnElement =
                     element2.getAnnotation(fr.aphp.nf.s97.processor.struct.Element.class);
                  if(null != afnElement){
                     if(!String.valueOf(element2.getSimpleName()).equals(afnElement.name())){
                        error(String.valueOf(element) + "." + String.valueOf(element2.getSimpleName())
                           + " does not match tag name " + afnElement.name(), element2);
                     }
                     if(!afnElement.type().equals(fr.aphp.nf.s97.processor.struct.Element.VALUE)){
                        error(String.valueOf(element) + "." + String.valueOf(element2.getSimpleName()) + " invalid Element type "
                           + afnElement.type() + " nust be " + fr.aphp.nf.s97.processor.struct.Element.VALUE, element2);
                     }
                     if(!String.class.getName().equals(element2.asType().toString())){
                        error(String.valueOf(element) + "." + String.valueOf(element2.getSimpleName()) + " invalid Type "
                           + element2.asType().toString() + " nust be " + String.class.getName(), element2);
                     }

                  }else{

                     fr.aphp.nf.s97.processor.struct.Type afnType =
                        element2.getAnnotation(fr.aphp.nf.s97.processor.struct.Type.class);
                     if(null != afnType){
                        var t = DeclaredType.class.cast(element2.asType()).getTypeArguments().get(0);
                        m.put(t.toString(), String.valueOf(element2.getSimpleName()));
                     }else{
                        m.put(element2.asType().toString(), String.valueOf(element2.getSimpleName()));
                     }

                  }
               }

            }
         }
      }

      elementNames.entrySet().stream().forEach(e -> {
         var clazz = e.getKey();
         e.getValue().entrySet().stream().forEach(e2 -> {
            var field = e2.getValue();
            var tag = classNames.get(e2.getKey());

            if(!field.equals(tag)){
               error(clazz + "." + field + " does not match tag name " + tag, null);
            }
         });
      });

      return false;
   }

   private void error(String msg, Element e){
      processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg, e);
   }
}
