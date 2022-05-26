package JsonDiff;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.flipkart.zjsonpatch.JsonDiff;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;


public class JsonDiffTest {

    public static void doTest()  {
        ObjectMapper mapper = new ObjectMapper();
        try {
            final String prod  = Files.readString(Path.of("/Users/gwolfmann/Downloads/JsonDiffTest/src/main/java/prodmco.json"), StandardCharsets.UTF_8);
            final String local = Files.readString(Path.of("/Users/gwolfmann/Downloads/JsonDiffTest/src/main/java/localmco.json"), StandardCharsets.UTF_8);
            JsonNode prodTree = mapper.readTree(prod);
            JsonNode localTree  = mapper.readTree(local);
            String[] classNames = {"account_money_info","coupons","experiments","payment_options","stored_cards","wallet_info_list"};
            for (String className: classNames ){
                    JsonNode pnode = prodTree.path(className);
                    JsonNode lnode = localTree.path(className);
                    if ("payment_options".equals(className)) {
                        String[] poClasses = {"items","payment_methods"};
                        for (String subClassName :poClasses) {
                            JsonNode pponode = pnode.path(subClassName);
                            JsonNode lponode = lnode.path(subClassName);
                            if ("items".equals(subClassName)){
                                JsonNode diffs = JsonDiff.asJson(pponode,lponode);
                                if (((ArrayNode) diffs).size() > 0)
                                    System.out.println(className + ":" + subClassName + " " + diffs.toPrettyString());
                            } else {   // for payment_methods
                                int pposize = ((ArrayNode) pponode).size();
                                int lposize = ((ArrayNode) lponode).size();
                                if (pposize != lposize){
                                    System.out.println("Production Payment_methods tiene " +String.valueOf(pposize) +
                                        " opciones y local tiene "+String.valueOf(lposize));
                                }
                            }
                        }

                    } else {
                        JsonNode diffs = JsonDiff.asJson(pnode,lnode);
                        if (((ArrayNode) diffs).size()>0)
                            System.out.println(className+" "+diffs.toPrettyString());

                    }
            }
            JsonNode diffs = JsonDiff.asJson(prodTree,localTree);
            System.out.println("diferencias: "+diffs.asText());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
