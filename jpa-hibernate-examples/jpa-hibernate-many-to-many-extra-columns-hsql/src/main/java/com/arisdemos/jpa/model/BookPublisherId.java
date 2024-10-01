package com.arisdemos.jpa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data @AllArgsConstructor @NoArgsConstructor
@Embeddable
public class BookPublisherId implements Serializable {
    @Column(name = "book_id")
    private Integer bookId;

    @Column(name = "publisher_id")
    private Integer publisherId;
}
