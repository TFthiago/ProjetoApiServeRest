package Api;

import Params.CarrinhoSt;
import Params.Produto;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.restassured.response.Response;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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

        String jsonBody = lerArquivoJson("src/test/resources/json/loginModUser.json");

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

        // Extrair a lista de IDs usando JSONPath
        List<String> idsProdutos = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            String id = response.jsonPath().getString("produtos[" + i + "]._id");
            idsProdutos.add(id);
        }

        // Imprimir os IDs para verificação
        System.out.println("IDs dos produtos: " + idsProdutos);

        List<CarrinhoSt> carrinhoRequisicao = new ArrayList<>();
        for (String id : idsProdutos) {
            CarrinhoSt produto = new CarrinhoSt();
            produto.setIdProduto(id);
            produto.setQuantidade(1);
            carrinhoRequisicao.add(produto);
        }

        // Criar um objeto Gson
        Gson gson = new Gson();

        // Converter a lista de objetos para JSON
        String jsonRequisicao = gson.toJson(carrinhoRequisicao);

        System.out.println(jsonRequisicao);
    }


    @Disabled
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
