package com.reservAR.backreservar.controller;

import com.reservAR.backreservar.dto.TableRequestDto;
import com.reservAR.backreservar.dto.TableResponseDto;
import com.reservAR.backreservar.model.TableEntity;
import com.reservAR.backreservar.service.impl.TableEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/table")
@RequiredArgsConstructor
public class TableEntityController {

    private final TableEntityService tableEntityService;

    @GetMapping("/id/{id}")
    public ResponseEntity<TableResponseDto> findById(@PathVariable Long id){
        return ResponseEntity.ok(TableResponseDto.of(tableEntityService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<TableResponseDto> create(@RequestBody TableRequestDto tableRequestDto){
        TableEntity table = tableEntityService.save(tableRequestDto);
        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(table.getId())
                .toUri();
        return ResponseEntity.created(location).body(TableResponseDto.of(table));
    }

    @GetMapping("/restaurant/{id}")
    public ResponseEntity<List<TableResponseDto>> findByRestaurant(@PathVariable Long id){

        List<TableResponseDto> tableResponseDtos = tableEntityService.findByRestaurantId(id).stream()
                .map(TableResponseDto::of)
                .toList();
        return ResponseEntity.ok(tableResponseDtos);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<TableResponseDto> delete(@PathVariable Long id){
        tableEntityService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}
