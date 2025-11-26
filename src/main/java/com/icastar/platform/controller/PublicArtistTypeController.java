package com.icastar.platform.controller;

import com.icastar.platform.dto.ArtistTypeDto;
import com.icastar.platform.dto.ArtistTypeFieldDto;
import com.icastar.platform.service.ArtistTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/artist-types")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Public", description = "Public API endpoints for general information")
public class PublicArtistTypeController {

    private final ArtistTypeService artistTypeService;

    @GetMapping
    public ResponseEntity<List<ArtistTypeDto>> getAllActiveArtistTypes() {
        List<ArtistTypeDto> artistTypes = artistTypeService.getAllActiveArtistTypes();
        return ResponseEntity.ok(artistTypes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistTypeDto> getArtistTypeById(@PathVariable Long id) {
        ArtistTypeDto artistType = artistTypeService.getArtistTypeById(id);
        return ResponseEntity.ok(artistType);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ArtistTypeDto> getArtistTypeByName(@PathVariable String name) {
        ArtistTypeDto artistType = artistTypeService.getArtistTypeByName(name);
        return ResponseEntity.ok(artistType);
    }

    @GetMapping("/{id}/fields")
    public ResponseEntity<List<ArtistTypeFieldDto>> getArtistTypeFields(@PathVariable Long id) {
        List<ArtistTypeFieldDto> fields = artistTypeService.getArtistTypeFields(id);
        return ResponseEntity.ok(fields);
    }

    @GetMapping("/{id}/fields/required")
    public ResponseEntity<List<ArtistTypeFieldDto>> getRequiredFields(@PathVariable Long id) {
        List<ArtistTypeFieldDto> fields = artistTypeService.getRequiredArtistTypeFields(id);
        return ResponseEntity.ok(fields);
    }
}
