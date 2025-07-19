package com.zurich.demo.books.googlebooks.service;

import com.zurich.demo.books.googlebooks.bookDTOs.GoogleBookDTO;
import com.zurich.demo.books.googlebooks.bookDTOs.Volume;
import com.zurich.demo.books.googlebooks.bookDTOs.VolumeInfo;
import java.util.Collections;

public class GoogleBooksMapper {

    public static GoogleBookDTO toDto(Volume volume) {
        VolumeInfo info = volume.volumeInfo();
        GoogleBookDTO dto = new GoogleBookDTO();

        dto.setId(volume.id());
        dto.setTitle(info.title());
        dto.setAuthors(info.authors() != null ? info.authors() : Collections.emptyList());
        dto.setCategories(info.categories() != null ? info.categories() : Collections.emptyList());
        dto.setPublisher(info.publisher());
        dto.setPublishedDate(info.publishedDate());
        dto.setDescription(info.description());
        dto.setPageCount(info.pageCount());
        dto.setPrintType(info.printType());
        dto.setAverageRating(info.averageRating());
        dto.setRatingsCount(info.ratingsCount());
        dto.setLanguage(info.language());
        dto.setPreviewLink(info.previewLink());
        dto.setInfoLink(info.infoLink());
        dto.setCanonicalVolumeLink(info.canonicalVolumeLink());

        if (info.imageLinks() != null) {
            dto.setThumbnailUrl(info.imageLinks().thumbnail());
        }

        if (info.industryIdentifiers() != null) {
            info.industryIdentifiers().stream()
                    .filter(id -> "ISBN_13".equals(id.type()))
                    .findFirst()
                    .ifPresent(id -> dto.setIsbn13(id.identifier()));
        }

        return dto;
    }
}
