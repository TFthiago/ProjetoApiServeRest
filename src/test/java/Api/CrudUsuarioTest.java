package Api;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CrudUsuarioTest {

    public String ct = "application/json";
    public String uri = "https://serverest.dev";
    public static String idUser;
    public static String auth;

    public static String lerArquivoJson(String arquivoJson) throws IOException {
        return new String(Files.readAllBytes(Paths.get(arquivoJson)));
    }

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
                .extract();
        idUser = response.jsonPath().getString("_id");
        System.out.println("O id do usuário é: " + idUser);


        FileWriter writer = new FileWriter("src/test/resources/userId.txt");
        writer.write(idUser);
        writer.close();

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
                .extract();
        auth = response.jsonPath().getString("authorization");
        System.out.println("A auth do usuário é: " + auth);

    }

    @Test
    @Order(3)
    public void GetAllUsersTest() {

        given()
                .contentType(ct)
                .log().all()
                .header("Authorization", auth)
        .when()
                .get(uri + "/usuarios/")
        .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    @Order(3)
    public void GetUserTest() {

        given()
                .contentType(ct)
                .log().all()
                .header("Authorization", auth)
        .when()
                .get(uri + "/usuarios/" + idUser)
        .then()
                .log().all()
                .statusCode(200)
                .body("nome", is("Usuario teste 5299"))
                .body("email", is("usu5299@qa.com.br"));
    }

    @Test
    @Order(4)
    public void PutUsuarioTest() throws IOException {

        String jsonBody = lerArquivoJson("src/test/resources/json/modUsu.json");

        given()
                .contentType(ct)
                .log().all()
                .header("Authorization", auth)
                .body(jsonBody)
        .when()
                .put(uri + "/usuarios/" + idUser)
        .then()
                .log().all()
                .statusCode(200)
                .body("message", is("Registro alterado com sucesso"));
    }


    private String lerIdUsuarioDeArquivo() throws IOException {

        try (BufferedReader br = new BufferedReader(new FileReader("src/test/resources/userId.txt"))) {
            // Processar a linha lida
            return br.readLine();
        }
    }

    @Disabled
    @Test
    @Order(5)
    public void DeleteUserTest() throws IOException {

        String id = lerIdUsuarioDeArquivo();

        given()
                .contentType(ct)
                .log().all()
        .when()
                .delete(uri + "/usuarios/" + id)
        .then()
                .log().all()
                .statusCode(200)
                .body("message", is("Registro excluído com sucesso"));
    }
}
