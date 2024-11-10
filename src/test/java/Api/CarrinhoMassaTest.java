package Api;

import Params.Carrinho;
import Params.CarrinhoSt;
import Params.Produtos;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.response.Response;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class CarrinhoMassaTest {

    public String ct = "application/json";
    public String uri = "https://serverest.dev";
    public static String auth;
    public static String idProd;
    public static String idCart;
    public static String jsonReqPost;


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

    @ParameterizedTest
    @Order(2)
    @CsvFileSource(resources = "/csv/prodMassa.csv", numLinesToSkip = 1, delimiter = ',')
    public void postProdutoMassaTest(String nome,
                                     int preco,
                                     String descricao,
                                     int quantidade
    ) {

        Produtos produto = new Produtos();

        produto.nome = nome;
        produto.preco = preco;
        produto.descricao = descricao;
        produto.quantidade = quantidade;

        Gson gson = new Gson();
        String jsonBody = gson.toJson(produto);

    //POST Produtos
        Response response = (Response) given()
                .contentType(ct)
                .log().all()
                .header("Authorization", auth)
                .body(jsonBody)
        .when()
                .post(uri + "/produtos")
        .then()
                .log().all()
                .statusCode(201)
                .body("message", is("Cadastro realizado com sucesso"))
                .extract();
        idProd = response.jsonPath().getString("_id");
        System.out.println("O id do produto é: " + idProd);

        List<String> idsProdutos = new ArrayList<>();
        idsProdutos.add(idProd);

        //Montagem do Json
        List<CarrinhoSt> carrinhoRequisicao = new ArrayList<>();
        for (String id : idsProdutos) {
            CarrinhoSt produtos = new CarrinhoSt();
            produtos.setIdProduto(id);
            produtos.setQuantidade(1);
            carrinhoRequisicao.add(produtos);
        }

        // Criar um objeto Carrinho e adicionar os produtos
        Carrinho carrinho = new Carrinho();
        carrinho.setProdutos(carrinhoRequisicao);

        // Converter o objeto Carrinho para JSON
        gson = new GsonBuilder().setPrettyPrinting().create();
        jsonReqPost = gson.toJson(carrinho);

        System.out.println(jsonReqPost);

    //POST cart
        response = (Response) given()
                .contentType(ct)
                .log().all()
                .header("Authorization", auth)
                .body(jsonReqPost)
        .when()
                .post(uri + "/carrinhos")
        .then()
                .log().all()
                .statusCode(201)
                .body("message", is("Cadastro realizado com sucesso"))
                .extract();

        idCart = response.jsonPath().getString("_id");
        System.out.println("O id do carrinho é: " + idCart);

    //DELETE cart
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

    //DELETE Produtos
        given()
                .contentType(ct)
                .log().all()
                .header("Authorization", auth)
        .when()
                .delete(uri + "/produtos/" + idProd)
        .then()
                .log().all()
                .statusCode(200)
                .body("message", is("Registro excluído com sucesso"));
    }
}
