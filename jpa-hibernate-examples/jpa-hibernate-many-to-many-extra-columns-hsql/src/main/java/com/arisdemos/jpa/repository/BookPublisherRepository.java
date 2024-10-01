package com.arisdemos.jpa.repository;

import com.arisdemos.jpa.model.BookPublisher;
import com.arisdemos.jpa.model.BookPublisherId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookPublisherRepository extends JpaRepository<BookPublisher, BookPublisherId> {
}
