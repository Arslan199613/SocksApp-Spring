package com.example.socksmyapp.service;

import com.example.socksmyapp.exception.MyException;
import com.example.socksmyapp.model.Color;
import com.example.socksmyapp.model.Size;
import com.example.socksmyapp.model.Socks;

import java.util.Collection;
import java.util.Map;

public interface SocksService {


    long createSocks(Socks socks);

    void getFromTheWarehouse(Socks socks) throws  MyException;

    void removeSocks(Color color, Size size, int cottonPercent, int quantity) throws MyException;

    int getSocksQuantity(Color color, Size size, Integer minCottonPercent,
                         Integer maxCottonPercent);


    Map<Long,Socks> getListSocks();
}
