package fr.aphp.nf.s97.converter;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

import fr.aphp.nf.s97.model.Efs;

public class CamelConverterTest extends CamelTestSupport
{

   @SuppressWarnings("resource")
   @Test
   public void testRouteConverter() throws Exception{
      getMockEndpoint("mock:result").expectedMessageCount(1);
      //on reçoit un objet Efs car la route se stermine sans avoir converti le message après le processeur
      getMockEndpoint("mock:result").expectedBodyReceived().body(Efs.class);
      //mais on peut le comparer à la String res car Camel va utiliser
      //le converter pour transformer l'objet Efs en String
      getMockEndpoint("mock:result").expectedBodiesReceived(res);
      // on ne peut pas utiliser
      //.expectedBodyReceived().constant(res);
      // car alors camel prend le body brut et c'est un objet Efs

      template.sendBody("direct:start", data);

      assertMockEndpointsSatisfied();
   }

   @Override
   protected RoutesBuilder createRouteBuilder(){
      return new RouteBuilder()
      {

         @Override
         public void configure() throws Exception{

            from("direct:start").process(e -> {
               //utilise le converter pour obtenir un objet Efs
               final var b = e.getMessage().getBody(Efs.class);

               b.header.msgRef = "My new Ref";
               e.getMessage().setBody(b);
               //on a un objet Efs dans le body
            }).to("mock:result");

         }

      };
   }

   public static String data = "aa\n" + "ad,000000000017688\n" + "af,EFS_RETOUR_DN\n" + "ah,AFNOR S 97-531\n"
      + "ai,20180703145345\n" + "ar,ORI\n" + "az\n" + "ca\n" + "ua\n" + "ud,068136\n" + "sh,920100054\n" + "ia\n" + "ea\n"
      + "ec,7802\n" + "ej,7802\n" + "la\n" + "lf,RAS\n" + "lh\n" + "ua\n" + "ud,068136\n" + "sh,920100054\n" + "ma\n"
      + "md,20180702220500\n" + "ra\n" + "rd,xxxx\n" + "rf,xxxx\n" + "rh,xxxx\n" + "rj,F\n" + "rl,19301105\n" + "rb\n"
      + "rn,8009675469\n" + "sh,920100054\n" + "rp,4761156902\n" + "ej,7802\n" + "pa\n" + "gc,3514368\n" + "ph\n" + "pj,04171\n"
      + "ej,7599\n" + "pl,11D\n" + "pm,6718115825-\n" + "zz\n";

   public static String res =
      "aa\n" + "ad,000000000017688\n" + "af,EFS_RETOUR_DN\n" + "ah,AFNOR S 97-531\n" + "ai,20180703145345\n" + "ar,ORI\n"
      //Champs ajouté par le processeur.
         + "ax,My new Ref\n"
         //
         + "az\n" + "ca\n" + "ua\n" + "ud,068136\n" + "sh,920100054\n" + "ia\n" + "ea\n" + "ec,7802\n" + "ej,7802\n" + "la\n"
         + "lf,RAS\n" + "lh\n" + "ua\n" + "ud,068136\n" + "sh,920100054\n" + "ma\n" + "md,20180702220500\n" + "ra\n" + "rd,xxxx\n"
         + "rh,xxxx\n" + "rf,xxxx\n" + "rj,F\n" + "rl,19301105\n" + "rb\n" + "rn,8009675469\n" + "sh,920100054\n"
         + "rp,4761156902\n" + "ej,7802\n" + "pa\n" + "gc,3514368\n" + "ph\n" + "pj,04171\n" + "ej,7599\n" + "pl,11D\n"
         + "pm,6718115825-\n" + "zz\n";
}
