package Api;

import io.restassured.response.Response;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServeRestProdutoTest {

    public static String lerArquivoJson(String arquivoJson) throws IOException {
        return new String(Files.readAllBytes(Paths.get(arquivoJson)));
    }

    public String ct = "application/json";
    public String uri = "https://serverest.dev";

    public static String auth;
    public static String idUser;
    public static String idProd;


    @Test
    @Order(1)
    public void PostUserTest() throws IOException {

        String jsonBody = lerArquivoJson("src/test/resources/json/createUser.json");

        Response response = (Response) given()
                .contentType(ct)
                .log().all()
                .body(jsonBody)
        .when()
                .post(uri + "/usuarios")
        .then()
                .log().all()
                .statusCode(201)
                .body("message", is("Cadastro realizado com sucesso"))
                .extract()
                ;
        idUser = response.jsonPath().getString("_id");
        System.out.println("O id do usuário é: " + idUser);

    }

    @Test
    @Order(2)
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
                .extract()
                ;
        auth = response.jsonPath().getString("authorization");
        System.out.println("A auth do usuário é: " + auth);

    }

    @Test
    @Order(3)
    public void PostProdTest() throws IOException {

        String jsonBody = lerArquivoJson("src/test/resources/json/createProd.json");

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
                .extract()
                ;
        idProd = response.jsonPath().getString("_id");
        System.out.println("O id do produto é: " + idProd);
    }

    @Test
    @Order(4)
    public void GetProdTest(){

        given()
                .contentType(ct)
                .log().all()
                .header("Authorization", auth)
        .when()
                .get(uri + "/produtos/" + idProd)
        .then()
                .log().all()
                .statusCode(200)
                .body("nome", is("HyperX Pulsefire Core XMAX2"))
                .body("preco", is(280))
                .body("quantidade", is(500));
    }

    @Test
    @Order(5)
    public void PutProdTest() throws IOException {

        String jsonBody = lerArquivoJson("src/test/resources/json/ModProd.json");

        given()
                .contentType(ct)
                .log().all()
                .header("Authorization", auth)
                .body(jsonBody)
        .when()
                .put(uri + "/produtos/" + idProd)
        .then()
                .log().all()
                .statusCode(200)
                .body("message", is("Registro alterado com sucesso"));
    }

    @Test
    @Order(6)
    public void DeleteProdTest(){

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

    @Test
    @Order(7)
    public void DeleteUserTest(){

        given()
                .contentType(ct)
                .log().all()
                .header("Authorization", auth)
        .when()
                .delete(uri + "/usuarios/" + idUser)
        .then()
                .log().all()
                .statusCode(200)
                .body("message", is("Registro excluído com sucesso"));
    }
}
