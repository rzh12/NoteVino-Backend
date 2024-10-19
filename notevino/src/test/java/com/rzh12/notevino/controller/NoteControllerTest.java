package com.rzh12.notevino.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rzh12.notevino.dto.FreeFormNoteRequest;
import com.rzh12.notevino.dto.FreeFormNoteResponse;
import com.rzh12.notevino.dto.WineDetailsResponse;
import com.rzh12.notevino.security.JwtUtil;
import com.rzh12.notevino.service.NoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)  // Disable Spring Security
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NoteService noteService;

    @MockBean
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {

    }

    @Test
    void createTastingNote_success() throws Exception {
        FreeFormNoteResponse mockResponse = new FreeFormNoteResponse();
        Mockito.when(noteService.createFreeFormNote(anyInt(), any(FreeFormNoteRequest.class)))
                .thenReturn(mockResponse);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/wines/1/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"note\": \"Great wine!\"}"))
                .andExpect(status().isCreated())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        assertEquals(true, responseString.contains("\"success\":true"));
    }

    @Test
    void createTastingNote_badRequest() throws Exception {
        Mockito.when(noteService.createFreeFormNote(anyInt(), any(FreeFormNoteRequest.class)))
                .thenReturn(null);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/wines/1/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"note\": \"Invalid note content\"}"))
                .andExpect(status().isBadRequest())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        String responseString = result.getResponse().getContentAsString();
        JsonNode jsonResponse = objectMapper.readTree(responseString);

        assertEquals(false, jsonResponse.get("success").asBoolean());
        assertEquals("Failed to create note!", jsonResponse.get("message").asText());
        assertEquals(null, jsonResponse.get("data").asText(null));
    }

    @Test
    void getWineDetailsWithNotes_found() throws Exception {
        WineDetailsResponse mockResponse = new WineDetailsResponse();
        Mockito.when(noteService.getWineDetailsWithNotes(anyInt()))
                .thenReturn(mockResponse);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/wines/1"))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        assertEquals(true, responseString.contains("\"success\":true"));
    }

    @Test
    void getWineDetailsWithNotes_notFound() throws Exception {
        Mockito.when(noteService.getWineDetailsWithNotes(anyInt()))
                .thenReturn(null);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/wines/1"))
                .andExpect(status().isNotFound())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        String responseString = result.getResponse().getContentAsString();
        JsonNode jsonResponse = objectMapper.readTree(responseString);

        assertEquals(false, jsonResponse.get("success").asBoolean());
        assertEquals("Wine not found!", jsonResponse.get("message").asText());
        assertEquals(null, jsonResponse.get("data").asText(null));
    }

    @Test
    void updateTastingNote_success() throws Exception {
        LocalDateTime updatedAt = LocalDateTime.now();
        Mockito.when(noteService.updateFreeFormNote(anyInt(), anyInt(), any(FreeFormNoteRequest.class)))
                .thenReturn(updatedAt);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/wines/1/notes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"note\": \"Updated note\"}"))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        String responseString = result.getResponse().getContentAsString();
        JsonNode jsonResponse = objectMapper.readTree(responseString);

        assertEquals(true, jsonResponse.get("success").asBoolean());
        assertEquals("Note updated successfully!", jsonResponse.get("message").asText());
        assertEquals(updatedAt.toString(), jsonResponse.get("data").asText());
    }

    @Test
    void updateTastingNote_notFound() throws Exception {
        Mockito.when(noteService.updateFreeFormNote(anyInt(), anyInt(), any(FreeFormNoteRequest.class)))
                .thenThrow(new IllegalArgumentException("Wine or Note does not belong to the current user."));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/wines/1/notes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"note\": \"Updated note\"}"))
                .andExpect(status().isNotFound())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        String responseString = result.getResponse().getContentAsString();
        JsonNode jsonResponse = objectMapper.readTree(responseString);

        assertEquals(false, jsonResponse.get("success").asBoolean());
        assertEquals("Wine or Note does not belong to the current user.", jsonResponse.get("message").asText());
    }

    @Test
    void updateTastingNote_serverError() throws Exception {
        Mockito.when(noteService.updateFreeFormNote(anyInt(), anyInt(), any(FreeFormNoteRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/wines/1/notes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"note\": \"Updated note\"}"))
                .andExpect(status().isInternalServerError())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        String responseString = result.getResponse().getContentAsString();
        JsonNode jsonResponse = objectMapper.readTree(responseString);

        assertEquals(false, jsonResponse.get("success").asBoolean());
        assertEquals("An error occurred while updating the note.", jsonResponse.get("message").asText());
    }

    @Test
    void deleteTastingNote_success() throws Exception {
        Mockito.when(noteService.deleteNote(anyInt(), anyInt()))
                .thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/wines/1/notes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTastingNote_notFound() throws Exception {
        Mockito.when(noteService.deleteNote(anyInt(), anyInt()))
                .thenReturn(false);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/wines/1/notes/1"))
                .andExpect(status().isNotFound())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        assertEquals(true, responseString.contains("\"success\":false"));
    }
}
