package Api;

import Params.Carrinho;
import Params.CarrinhoSt;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CrudCarrinhoTest {

    public String ct = "application/json";
    public String uri = "https://serverest.dev";
    public static String auth;
    public static String idCart;
    public static String jsonRequisicao;

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
        for (int i = 2; i < 4; i++) {
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

        // Criar um objeto Carrinho e adicionar os produtos
        Carrinho carrinho = new Carrinho();
        carrinho.setProdutos(carrinhoRequisicao);

        // Converter o objeto Carrinho para JSON
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        jsonRequisicao = gson.toJson(carrinho);

        System.out.println(jsonRequisicao);
    }


    @Test
    @Order(3)
    public void PostCartTest() throws IOException {

        Response response = (Response) given()
                .contentType(ct)
                .log().all()
                .header("Authorization", auth)
                .body(jsonRequisicao)
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

    @Test
    @Order(4)
    public void GetCartTest() {

        given()
                .contentType(ct)
                .log().all()
                .header("Authorization", auth)
            .when()
                .get(uri + "/carrinhos/" + idCart)
            .then()
                .log().all()
                .statusCode(200)
                .body("precoTotal", is(1000))
                .body("quantidadeTotal", is(2))
        ;
    }

    @Test
    @Order(5)
    public void DeleteCartTest(){

        given()
                .contentType(ct)
                .log().all()
                .header("Authorization", auth)
        .when()
                .delete(uri + "/carrinhos/cancelar-compra")
        .then()
                .log().all()
                .statusCode(200)
                .body("message", is("Registro excluído com sucesso. Estoque dos produtos reabastecido"));
    }
}
