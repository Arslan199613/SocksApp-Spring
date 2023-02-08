package com.example.socksmyapp.service;

import com.example.socksmyapp.exception.MyException;
import com.example.socksmyapp.model.Color;
import com.example.socksmyapp.model.Size;
import com.example.socksmyapp.model.Socks;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.TreeMap;

@Service
public class SocksServiceImpl implements SocksService {

    private final Map<Long, Socks> socksMap = new TreeMap<>();
    private static long id = 1;

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
        }
        return id++;
    }

    @Override
    public void getFromTheWarehouse(Socks socks) throws MyException {
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
                    } else {
                        throw new MyException("в запросе получилось больше носков,чем есть на складе");
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
        }
        return count;
    }

    @Override
    public void removeSocks(Color color, Size size, int cottonPercent, int quantity) throws MyException {
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
                    } else {
                        throw new MyException("Пусто");
                    }
                }
            }
        }
    }

    @Override
    public Map<Long,Socks> getListSocks() {
        return Map.copyOf(socksMap);
    }
}

