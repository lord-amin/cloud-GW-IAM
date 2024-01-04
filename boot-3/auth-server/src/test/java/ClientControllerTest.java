import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiddev.authorization.AuthenticationServiceApplication;
import com.tiddev.authorization.client.controller.client.ClientCreateRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.nio.file.Files;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author : Yaser(Amin) sadeghi
 */
@Slf4j
@AutoConfigureMockMvc
@SpringBootTest(classes = AuthenticationServiceApplication.class)
public class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static ObjectMapper objectMapper;

    @BeforeAll
    public static void onBeforeAll() {
        objectMapper = new ObjectMapper();
        objectMapper.writerWithDefaultPrettyPrinter();
    }

    @BeforeEach
    public void onBeforeEach() {
        // You can set up any necessary initialization here.
    }

    @Test
    public void testRegister() throws Exception {
        ClientCreateRequest value = ClientCreateRequest.builder()
                .clientId("test_client_id")
                .clientSecret("test_client_secret")
                .tokenExpiresSeconds(500L)
                .clientName("test_client_name")
                .build();
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(value)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        log.info("The create client response is {}", responseContent);

    }

    @Test
    public void testHandleFileUploadUsingCurl() throws Exception {
        // Create a temporary test file to upload
        File tempFile = File.createTempFile("test-file", ".txt");
        String fileContent = "Sample file content";
        Files.write(tempFile.toPath(), fileContent.getBytes());

        // Create a MockMultipartFile for testing
        MockMultipartFile file = new MockMultipartFile("file", "test-file.txt", MediaType.TEXT_PLAIN_VALUE, Files.readAllBytes(tempFile.toPath()));

        // Send a POST request to /batch with the file
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/batch")
                        .file(file))
                .andExpect(status().isOk())
                .andReturn();

        // You can add assertions to verify the response content if needed.
        String responseContent = result.getResponse().getContentAsString();
        // Add assertions to check the response content.

        // Clean up the temporary file
        Files.delete(tempFile.toPath());
    }
}