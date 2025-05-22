package com.netease.nemo.entlive.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageDto <T> {
    private Long total;
    private List<T> list;
}
