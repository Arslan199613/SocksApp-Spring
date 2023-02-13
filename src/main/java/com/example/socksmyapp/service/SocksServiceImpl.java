package com.example.socksmyapp.service;
import com.example.socksmyapp.exception.NumberOfSocksException;
import com.example.socksmyapp.model.Color;
import com.example.socksmyapp.model.Size;
import com.example.socksmyapp.model.Socks;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.TreeMap;

@Service
public class SocksServiceImpl implements SocksService {

    private Map<Long, Socks> socksMap = new TreeMap<>();
    private static long id = 0;

    private FilesService filesService;

    public SocksServiceImpl( FilesService filesService) {
        this.filesService = filesService;
    }

    @PostConstruct
    private void init() {
        try {
            readFromFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public long createSocks(Socks socks)  {
        if (socksMap.containsValue(socks)) {
            for (Map.Entry<Long, Socks> entry : socksMap.entrySet()) {
                if (entry.getValue().equals(socks)) {
                    long id = entry.getKey();
                    int oldQuantity = entry.getValue().getQuantity();
                    int newQuantity = oldQuantity + socks.getQuantity();
                    Socks newSocks = new Socks(socks.getColor(), socks.getSize(), socks.getCottonPercent(),
                            newQuantity);
                    socksMap.put(id, newSocks);
                    return id;
                }
            }
        } else {
            socksMap.put(id, socks);
            saveToFile();
        }
        return id++;
    }

    @Override
    public void getFromTheWarehouse(Socks socks) throws NumberOfSocksException {
        if (socksMap.containsValue(socks)) {
            for (Map.Entry<Long, Socks> entry : socksMap.entrySet()) {
                if (entry.getValue().equals(socks)) {
                    long id = entry.getKey();
                    int oldQuanity = entry.getValue().getQuantity();
                    int newQuanity = socks.getQuantity();
                    if (oldQuanity >= newQuanity) {
                        int quantity = oldQuanity - newQuanity;
                        Socks socksNew = new Socks(socks.getColor(), socks.getSize(), socks.getCottonPercent(),
                                quantity);
                        socksMap.put(id, socksNew);
                        saveToFile();
                    } else {
                        throw new NumberOfSocksException("В запросе получилось больше носков,чем есть на складе");
                    }
                }
            }
        }
    }

    @Override
    public int getSocksQuantity(Color color, Size size, Integer minCotton,
                                Integer maxCotton) {
        int count = 0;
        for (Map.Entry<Long, Socks> entry : socksMap.entrySet()) {
            if (color != null && !entry.getValue().getColor().equals(color)) {
                continue;
            }
            if (size != null && !entry.getValue().getSize().equals(size)) {
                continue;
            }
            if (minCotton != null && entry.getValue().getCottonPercent() < minCotton) {
                continue;
            }
            if (maxCotton != null && entry.getValue().getCottonPercent() > maxCotton) {
                continue;
            }
            count += entry.getValue().getQuantity();
            saveToFile();
        }
        return count;
    }

    @Override
    public void removeSocks(Color color, Size size, int cottonPercent, int quantity) throws NumberOfSocksException {
        Socks socks = new Socks(color, size, cottonPercent, quantity);
        if (socksMap.containsValue(socks)) {
            for (Map.Entry<Long, Socks> entry : socksMap.entrySet()) {
                if (entry.getValue().equals(socks)) {
                    long id = entry.getKey();
                    int oldQuantity = entry.getValue().getQuantity();
                    int defectiveQuantitySocks = socks.getQuantity();
                    if (oldQuantity >= defectiveQuantitySocks) {
                        int newQuantity = oldQuantity - defectiveQuantitySocks;
                        Socks newSocks = new Socks(socks.getColor(), socks.getSize(), socks.getCottonPercent(),
                                newQuantity);
                     socksMap.put(id, newSocks);
                     saveToFile();
                    } else {
                        throw new NumberOfSocksException("На складе нет носков");
                    }
                }
            }
        }
    }

    @Override
    public Map<Long,Socks> getListSocks() {
        return Map.copyOf(socksMap);
    }

    private void saveToFile() {
        try {
            DataFile dataFile = new DataFile(id+1,socksMap);
            String json = new ObjectMapper().writeValueAsString(dataFile);
            filesService.saveToFile(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void readFromFile() {
        try {
            String json = filesService.readFromFile();
            DataFile dataFile = new ObjectMapper().readValue(json, new TypeReference<DataFile>() {
            });
            id = dataFile.socksId;
            socksMap = dataFile.getSocksMap();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Path createReport() throws IOException {
        Path socksText = filesService.createTempFile("Socks_text");
        for (Socks socks : socksMap.values()) {
            try (Writer writer = Files.newBufferedWriter(socksText, StandardOpenOption.APPEND)) {
                writer.append(" Цвет носков : ");
                writer.append(String.valueOf(socks.getColor()));
                writer.append("\n Размер : ");
                writer.append(String.valueOf(socks.getSize()));
                writer.append("\n Процент хлопка: ");
                writer.append(String.valueOf(socks.getCottonPercent()));
                writer.append("\n Количество: ");
                writer.append(String.valueOf(socks.getQuantity()));
                writer.append("\n\n");
            }
        }
        return socksText;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    private static class DataFile {

        private long socksId;
        private Map<Long,Socks> socksMap;

    }
}


