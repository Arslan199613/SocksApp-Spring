package com.example.socksmyapp.service;

import com.example.socksmyapp.exception.NumberOfSocksException;
import com.example.socksmyapp.model.Color;
import com.example.socksmyapp.model.Size;
import com.example.socksmyapp.model.Socks;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public interface SocksService {


    long createSocks(Socks socks);

    void getFromTheWarehouse(Socks socks) throws NumberOfSocksException;

    void removeSocks(Color color, Size size, int cottonPercent, int quantity) throws NumberOfSocksException;

    int getSocksQuantity(Color color, Size size, Integer minCottonPercent,
                         Integer maxCottonPercent);

    Map<Long,Socks> getListSocks();


    Path createReport() throws IOException;
}
