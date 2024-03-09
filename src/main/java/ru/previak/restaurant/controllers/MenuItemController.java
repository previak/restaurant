package ru.previak.restaurant.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.previak.restaurant.dto.MenuItemDTO;
import ru.previak.restaurant.services.interfaces.MenuItemService;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
@RequestMapping("/api/menu_items")
@Api("Menu item controller")
public class MenuItemController {

    MenuItemService menuItemService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @ApiOperation("Create menu item")
    public ResponseEntity<String> createMenuItem(
            @Valid @RequestBody MenuItemDTO menuItemDTO
            ) {
        menuItemService.createMenuItem(menuItemDTO);
        return ResponseEntity.ok("Menu item was successfully created");
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{dishName}")
    @ApiOperation("Update menu item")
    public ResponseEntity<String> updateMenuItem(
            @PathVariable String dishName,
            @Valid @RequestBody MenuItemDTO menuItemDTO
    ) {
        menuItemService.updateMenuItem(dishName, menuItemDTO);
        return ResponseEntity.ok("Menu item was successfully updated");
    }

    @GetMapping
    @ApiOperation("Get all menu items")
    public ResponseEntity<List<MenuItemDTO>> getAllMenuItems() {
        return ResponseEntity.ok(menuItemService.getAllMenuItems());
    }
}
