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
import ru.previak.restaurant.dto.DishDTO;
import ru.previak.restaurant.services.interfaces.DishService;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@Validated
@RequestMapping("/api/dishes")
@Api("Dish controller")
public class DishController {

    DishService dishService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @ApiOperation("Create dish")
    public ResponseEntity<String> createDish(
            @Valid @RequestBody DishDTO dishDTO
    ) {
        dishService.createDish(dishDTO);
        return ResponseEntity.ok("Dish " + dishDTO.getName() + " was successfully created");
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{dishName}")
    @ApiOperation("Remove dish")
    public ResponseEntity<String> removeDish(
            @PathVariable String dishName
    ) {
        dishService.removeDish(dishName);
        return ResponseEntity.ok("Dish was successfully removed");
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{dishName}")
    @ApiOperation("Update dish")
    public ResponseEntity<String> updateDish(
            @PathVariable String dishName,
            @Valid @RequestBody DishDTO dishDTO
    ) {
        dishService.updateDish(dishName, dishDTO);
        return ResponseEntity.ok("Dish was successfully updated");
    }

    @GetMapping
    @ApiOperation("Get all dishes")
    public ResponseEntity<List<DishDTO>> getAllDishes() {
        return ResponseEntity.ok(dishService.getAllDishes());
    }
}
