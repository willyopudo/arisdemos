package com.arisdemos.jpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookPublisherDto {
    private Integer id;
    private boolean isDefault;
}
