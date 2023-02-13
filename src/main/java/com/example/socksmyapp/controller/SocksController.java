package com.example.socksmyapp.controller;
import com.example.socksmyapp.exception.NumberOfSocksException;
import com.example.socksmyapp.exception.SocksNotFoundException;
import com.example.socksmyapp.model.Color;
import com.example.socksmyapp.model.Size;
import com.example.socksmyapp.model.Socks;
import com.example.socksmyapp.service.FilesService;
import com.example.socksmyapp.service.SocksService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;


@RestController
@RequestMapping("/socks")
@Tag(name = "Носки", description = "CRUD - операции с носками")
public class SocksController {

    private final SocksService socksService;

    private FilesService filesService;

    public SocksController(SocksService socksService, FilesService filesService) {
        this.socksService = socksService;
        this.filesService = filesService;
    }


    @Operation(summary = "Создание носков", description = "Выберите подходящий цвет:RED, BLUE, BLACK, YELLOW и выберите размер от S до XXL")
    @PostMapping()
    public ResponseEntity<Long> addSocks(@Valid @RequestBody Socks socks) {
        long id = socksService.createSocks(socks);
        return ResponseEntity.ok(id);
    }

    @Operation(summary = "Забрать со склада", description = "Выберите подходящий цвет:RED, BLUE, BLACK, YELLOW и выберите размер от S до XXL")
    @PutMapping()
    public ResponseEntity<Void> takeSocks(@Valid @RequestBody Socks socks) throws NumberOfSocksException {
        socksService.getFromTheWarehouse(socks);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Узнать сколько носков на складе", description =
            "Выберите подходящий цвет:RED, BLUE, BLACK, YELLOW и выберите размер от S до XXL"
                    + "и введите min и max процент содержания хлопка (от 0 до 100)")
    @GetMapping()
    public ResponseEntity<Integer> quantitySocks(@RequestParam(name = "color") Color color,
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
                                                      @RequestParam(name = "quantity") Integer quantity) throws NumberOfSocksException {
        socksService.removeSocks(color, size, cottonPercent, quantity);
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Получение списка носков")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllSocks() {
        try{
        Map<Long, Socks> listSocks = socksService.getListSocks();
            return ResponseEntity.ok(listSocks);
        }catch (SocksNotFoundException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/socksReport")
    @Operation(
            summary = "Получение отчёта о товаре в формате txt",
            description = "Все носки "
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Данные о товаре получены"
            )
    })

    public ResponseEntity<InputStreamResource> getSocksReport() {
        try {
            Path path = socksService.createReport();
            if (Files.size(path) == 0) {
                return ResponseEntity.noContent().build();
            }
            InputStreamResource resource = new InputStreamResource(new FileInputStream(path.toFile()));
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .contentLength(Files.size(path))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"SocksLog.txt\"")
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.noContent().build();
        }
    }
}


