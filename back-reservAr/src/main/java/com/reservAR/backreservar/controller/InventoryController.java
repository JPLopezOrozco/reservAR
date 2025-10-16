package com.reservAR.backreservar.controller;

import com.reservAR.backreservar.dto.InventoryRuleRequestDto;
import com.reservAR.backreservar.dto.InventoryRuleResponseDto;
import com.reservAR.backreservar.model.InventoryRule;
import com.reservAR.backreservar.service.IInventoryRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/rule")
@RequiredArgsConstructor
public class InventoryController {

    private final IInventoryRuleService inventoryRuleService;

    @GetMapping("/id/{id}")
    public ResponseEntity<InventoryRuleResponseDto> findById(@PathVariable Long id){
        InventoryRule inventoryRule = inventoryRuleService.findById(id);
        return ResponseEntity.ok(InventoryRuleResponseDto.of(inventoryRule));
    }

    @PostMapping
    public ResponseEntity<InventoryRuleResponseDto> create(@RequestBody InventoryRuleRequestDto ruleRequestDto){
        InventoryRule inventoryRule = inventoryRuleService.save(ruleRequestDto);
        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(inventoryRule.getId())
                .toUri();
        return ResponseEntity.created(location).body(InventoryRuleResponseDto.of(inventoryRule));
    }

    @GetMapping
    public ResponseEntity<List<InventoryRuleResponseDto>> findAll(){
        List<InventoryRuleResponseDto> rules = inventoryRuleService.findAll().stream()
                .map(InventoryRuleResponseDto::of)
                .toList();
        return ResponseEntity.ok(rules);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        inventoryRuleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}
