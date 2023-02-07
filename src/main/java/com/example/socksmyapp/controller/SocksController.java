package com.example.socksmyapp.controller;
import com.example.socksmyapp.exception.MyException;
import com.example.socksmyapp.model.Color;
import com.example.socksmyapp.model.Size;
import com.example.socksmyapp.model.Socks;
import com.example.socksmyapp.service.SocksService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Collection;


@RestController
@RequestMapping("/socks")
@Tag(name = "Носки", description = "CRUD - операции с носками")
public class SocksController {

    private final SocksService socksService;

    public SocksController(SocksService socksService) {
        this.socksService = socksService;
    }

    @Operation(summary = "Создание носков", description = "Выберите подходящий цвет:RED, BLUE, BLACK, YELLOW и выберите размер от S до XXL")
    @PostMapping()
    public ResponseEntity<Long> addSocks(@Valid @RequestBody Socks socks) {
        long id = socksService.createSocks(socks);
        return ResponseEntity.ok(id);
    }

    @Operation(summary = "Забрать со склада", description = "Выберите подходящий цвет:RED, BLUE, BLACK, YELLOW и выберите размер от S до XXL")
    @PutMapping()
    public ResponseEntity<Void> takeSocks(@Valid @RequestBody Socks socks) throws MyException {
        socksService.getFromTheWarehouse(socks);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Узнать сколько носков на складе", description =
            "Выберите подходящий цвет:RED, BLUE, BLACK, YELLOW и выберите размер от S до XXL"
                    + "и введите min и max процент содержания хлопка (от 0 до 100)")
    @GetMapping()
    public ResponseEntity<Integer> quanitySocks(@RequestParam(name = "color") Color color,
                                                @RequestParam(name = "size") Size size,
                                                @RequestParam(name = "minCotton") Integer minCotton,
                                                @RequestParam(name = "maxCotton") Integer maxCotton) {
        Integer quantity = socksService.getSocksQuantity(color, size, minCotton, maxCotton);
        return ResponseEntity.ok().body(quantity);
    }

    @Operation(summary = "Списать бракованное количество носков на складе", description =
            "Выберите подходящий цвет:RED, BLUE, BLACK, YELLOW и выберите размер от S до XXL"
                    + "и введите процент содержания хлопка (от 0 до 100)")
    @DeleteMapping
    public ResponseEntity<Void> deletedDefectiveSocks(@RequestParam(name = "color") Color color,
                                                      @RequestParam(name = "size") Size size,
                                                      @RequestParam(name = "cottonPercent") Integer cottonPercent,
                                                      @RequestParam(name = "quantity") Integer quantity) throws MyException {
        socksService.removeSocks(color, size, cottonPercent, quantity);
        return ResponseEntity.notFound().build();
    }



    @Operation(summary = "Получение списка носков")
    @GetMapping("/all")
    public ResponseEntity<Collection<Socks>> getAllSocks() {
        Collection<Socks> listSocks = socksService.getListSocks();
        if (listSocks != null) {
            return ResponseEntity.ok(listSocks);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
