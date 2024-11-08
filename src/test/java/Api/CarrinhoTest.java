package Api;

import Params.Produto;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.restassured.response.Response;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class CarrinhoTest {

    public String ct = "application/json";
    public String uri = "https://serverest.dev";
    public static String auth;
    public static String idCart;

    public static String lerArquivoJson(String arquivoJson) throws IOException {
        return new String(Files.readAllBytes(Paths.get(arquivoJson)));
    }

    @Test
    @Order(1)
    public void LoginUserTest() throws IOException {

        String jsonBody = lerArquivoJson("src/test/resources/json/loginUser.json");

        Response response = (Response) given()
                .contentType(ct)
                .log().all()
                .body(jsonBody)
        .when()
                .post(uri + "/login")
        .then()
                .log().all()
                .statusCode(200)
                .body("message", is("Login realizado com sucesso"))
                .extract();
        auth = response.jsonPath().getString("authorization");
        System.out.println("A auth do usuário é: " + auth);

    }

    /*
    @Test
    @Order(2)
    public void GetAllProdTest(){

        Response response = (Response) given()
                .contentType(ct)
                .log().all()
                .header("Authorization", auth)
        .when()
                .get(uri + "/produtos/")
        .then()
                .log().all()
                .statusCode(200)
                .extract();


        // Converter a resposta para uma lista de objetos Produto
        Gson gson = new Gson();
        List<Produto> produtos = gson.fromJson(response.asString(), new TypeToken<List<Produto>>() {}.getType());

        // Selecionar os produtos desejados (exemplo: os dois primeiros)
        List<String> idsProdutos = produtos.stream()
                .limit(2)
                .map(Produto::getId)
                .collect(Collectors.toList());

        // Construir o JSON do corpo da requisição para criar o carrinho

        // (Adaptar de acordo com a estrutura esperada pela sua API)
        String jsonBody = "{\"produtos\": [" +
                idsProdutos.stream()
                        .map(id -> "{\"id\": \"" + id + "\"}")
                        .collect(Collectors.joining(",")) +
                "]}";

        // Enviar a requisição POST para criar o carrinho
        // ... (seu código existente para PostCartTest)
    }

     */

    @Test
    public void PostCartTest() throws IOException {

        String jsonBody = lerArquivoJson("caminho");

        Response response = (Response) given()
                .contentType(ct)
                .log().all()
                .header("Authorization", auth)
                .body(jsonBody)
        .when()
                .post(uri + "/carrinhos")
        .then()
                .log().all()
                .statusCode(201)
                .body("message", is("Cadastro realizado com sucesso"))
                .extract();

        idCart = response.jsonPath().getString("_id");
        System.out.println("O id do carrinho é: " + idCart);
    }

}
