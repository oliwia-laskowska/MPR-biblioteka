package pl.s32832.library.mapper;

import pl.s32832.library.dto.response.BookResponse;
import pl.s32832.library.entity.Book;

import java.util.stream.Collectors;

public class BookMapper {
    private BookMapper() {}

    public static BookResponse toResponse(Book b) {
        return new BookResponse(
                b.getId(),
                b.getTitle(),
                b.getIsbn(),
                b.getTotalCopies(),
                b.getAvailableCopies(),
                b.getAuthors().stream().map(a -> a.getId()).collect(Collectors.toSet())
        );
    }
}
