package com.icastar.platform.controller;

import com.icastar.platform.dto.ArtistTypeDto;
import com.icastar.platform.dto.ArtistTypeFieldDto;
import com.icastar.platform.service.ArtistTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/artist-types")
@RequiredArgsConstructor
@Slf4j
public class ArtistTypeController {

    private final ArtistTypeService artistTypeService;

    @GetMapping
    public ResponseEntity<List<ArtistTypeDto>> getAllArtistTypes() {
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

    @GetMapping("/{id}/fields/searchable")
    public ResponseEntity<List<ArtistTypeFieldDto>> getSearchableFields(@PathVariable Long id) {
        List<ArtistTypeFieldDto> fields = artistTypeService.getSearchableArtistTypeFields(id);
        return ResponseEntity.ok(fields);
    }

    @PostMapping
    public ResponseEntity<ArtistTypeDto> createArtistType(@RequestBody ArtistTypeDto artistTypeDto) {
        ArtistTypeDto createdArtistType = artistTypeService.createArtistType(artistTypeDto);
        return ResponseEntity.ok(createdArtistType);
    }

    @PostMapping("/{id}/fields")
    public ResponseEntity<ArtistTypeFieldDto> createArtistTypeField(
            @PathVariable Long id, 
            @RequestBody ArtistTypeFieldDto fieldDto) {
        ArtistTypeFieldDto createdField = artistTypeService.createArtistTypeField(id, fieldDto);
        return ResponseEntity.ok(createdField);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArtistTypeDto> updateArtistType(
            @PathVariable Long id, 
            @RequestBody ArtistTypeDto artistTypeDto) {
        ArtistTypeDto updatedArtistType = artistTypeService.updateArtistType(id, artistTypeDto);
        return ResponseEntity.ok(updatedArtistType);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtistType(@PathVariable Long id) {
        artistTypeService.deleteArtistType(id);
        return ResponseEntity.noContent().build();
    }
}
