package kr.sproutfx.oauth.backoffice.api.client.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import kr.sproutfx.oauth.backoffice.api.client.controller.ClientController.ClientCreateRequest;
import kr.sproutfx.oauth.backoffice.api.client.controller.ClientController.ClientStatusUpdateRequest;
import kr.sproutfx.oauth.backoffice.api.client.controller.ClientController.ClientUpdateRequest;
import kr.sproutfx.oauth.backoffice.api.client.entity.Client;
import kr.sproutfx.oauth.backoffice.api.client.enumeration.ClientStatus;
import kr.sproutfx.oauth.backoffice.api.client.service.ClientService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles(value = "test")
@AutoConfigureMockMvc
@SpringBootTest
class ClientControllerTest {

    @MockBean
    private ClientService clientService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Mockup recode
    Client mockupClient1 = Client.builder()
        .id(UUID.fromString("00174dec-5665-4760-9255-6feb51a2b980"))
        .code("d36c646af3104e4587e6a1dfd7a398d0")
        .secret("ejRTUHNKTldTcUJiVWh0c2dXRVNlMGhJMDdxWEJLM0M=")
        .name("Test client #1")
        .accessTokenSecret("agvHf3DN8zrjM9eyM0lYiV6o3obyacgJCOSm7x2gCgua90whlpSY2AbfcU1pjNEzDzxWPqnqHZVTJdlw5AFku2T9oWlvJEgI")
        .accessTokenValidityInSeconds(7200L)
        .status(ClientStatus.ACTIVE)
        .description(null)
        .build();

    Client mockupClient2 = Client.builder()
        .id(UUID.fromString("001cae5a-fd6b-43a5-ba74-07ce7bba256b"))
        .code("68c2e7fbf4044c6081ab68b742d65a3d")
        .secret("WHlzaE9oYWtoOElGR0k2SjE5Wnc2Vm5Ybkd2NVF6UVY=")
        .name("Test client #2")
        .accessTokenSecret("5iAJvOfyjrMHkYpnAZRUCnnK4ckKBDirfhKGcZAJOLCOvgLMLXHSBB841Lfot9jOXwl5h291GdBQPrE3cL5uxKQJ5FXCSX4D")
        .accessTokenValidityInSeconds(7200L)
        .status(ClientStatus.ACTIVE)
        .description(null)
        .build();

    Client[] mockupClients = {mockupClient1, mockupClient2};

    @Test
    void testFindAll() throws Exception {
        // given
        given(this.clientService.findAll())
            .willReturn(Lists.newArrayList(mockupClients));

        // when
        ResultActions perform = this.mockMvc.perform(get("/clients")
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding(StandardCharsets.UTF_8));

        // then
        perform.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("succeeded").value(true))
            .andExpect(jsonPath("content", Matchers.hasSize(2)));
    }

    @Test
    void testFindById() throws Exception {
        // given
        given(this.clientService.findById(mockupClient1.getId())).willReturn(mockupClient1);

        // when
        ResultActions perform = this.mockMvc.perform(get(String.format("/clients/%s", mockupClient1.getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding(StandardCharsets.UTF_8));

        // then
        perform.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("succeeded").value(true))
            .andExpect(jsonPath("content.code").exists());
    }

    @Test
    void testCreate() throws Exception {
        // given
        ClientCreateRequest clientCreateRequest = new ClientCreateRequest();
        clientCreateRequest.setName(mockupClient1.getName());
        clientCreateRequest.setDescription(mockupClient1.getDescription());

        given(this.clientService.create(clientCreateRequest.getName(), clientCreateRequest.getDescription()))
            .willReturn(mockupClient1.getId());

        given(this.clientService.findById(mockupClient1.getId()))
            .willReturn(mockupClient1);

        // when
        ResultActions perform = this.mockMvc.perform(post("/clients")
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding(StandardCharsets.UTF_8)
            .content(objectMapper.writeValueAsString(clientCreateRequest)));

        // then
        perform.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("succeeded").value(true))
            .andExpect(jsonPath("content.code").value(mockupClient1.getCode()))
            .andExpect(jsonPath("content.name").value(mockupClient1.getName()))
            .andExpect(jsonPath("content.status").value(mockupClient1.getStatus().toString()))
            .andExpect(jsonPath("content.description").value(mockupClient1.getDescription()));
    }

    @Test
    void testUpdate() throws Exception {
        // given
        ClientUpdateRequest clientUpdateRequest = new ClientUpdateRequest();
        clientUpdateRequest.setName("new name");
        clientUpdateRequest.setDescription("new description");
        clientUpdateRequest.setAccessTokenValidityInSeconds(3600L);

        mockupClient1.setName(clientUpdateRequest.getName());
        mockupClient1.setDescription(clientUpdateRequest.getDescription());
        mockupClient1.setAccessTokenValidityInSeconds(clientUpdateRequest.getAccessTokenValidityInSeconds());

        given(this.clientService.findById(mockupClient1.getId()))
            .willReturn(mockupClient1);

        // when
        ResultActions perform = this.mockMvc.perform(put(String.format("/clients/%s", mockupClient1.getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding(StandardCharsets.UTF_8)
            .content(objectMapper.writeValueAsString(clientUpdateRequest)));

        // then
        perform.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("succeeded").value(true))
            .andExpect(jsonPath("content.code").value(mockupClient1.getCode()))
            .andExpect(jsonPath("content.name").value(mockupClient1.getName()))
            .andExpect(jsonPath("content.status").value(mockupClient1.getStatus().toString()))
            .andExpect(jsonPath("content.description").value(mockupClient1.getDescription()));
    }

    @Test
    void testUpdateStatus() throws Exception {
        // given
        ClientStatusUpdateRequest clientStatusUpdateRequest = new ClientStatusUpdateRequest();
        clientStatusUpdateRequest.setClientStatus(ClientStatus.ACTIVE);

        mockupClient1.setStatus(clientStatusUpdateRequest.getClientStatus());

        given(this.clientService.findById(mockupClient1.getId()))
            .willReturn(mockupClient1);

        // when
        ResultActions perform = this.mockMvc.perform(patch(String.format("/clients/%s/status", mockupClient1.getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding(StandardCharsets.UTF_8)
            .content(objectMapper.writeValueAsString(clientStatusUpdateRequest)));

        // then
        perform.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("succeeded").value(true))
            .andExpect(jsonPath("content.code").value(mockupClient1.getCode()))
            .andExpect(jsonPath("content.name").value(mockupClient1.getName()))
            .andExpect(jsonPath("content.status").value(mockupClient1.getStatus().toString()))
            .andExpect(jsonPath("content.description").value(mockupClient1.getDescription()));
    }

    @Test
    void testDelete() throws Exception {
        // when
        ResultActions resultActions = this.mockMvc.perform(delete(String.format("/clients/%s", mockupClient1.getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding(StandardCharsets.UTF_8));

        // then
        resultActions.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("succeeded").value(true))
            .andExpect(jsonPath("content.deletedClientId").value(mockupClient1.getId().toString()));
    }
}