package com.zurich.demo.controller;

import com.zurich.demo.dto.GoogleBookDTO;
import com.zurich.demo.dto.SaleInfo;
import com.zurich.demo.service.GoogleBooksService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/external/books")
public class GoogleBooksController {

    private final GoogleBooksService googleBooksService;

    public GoogleBooksController(GoogleBooksService googleBooksService) {
        this.googleBooksService = googleBooksService;
    }

    /**
     * Endpoint de busca flexível.
     * Ex: GET /api/external/books/search?author=Stephen+King
     */
    @GetMapping("/search")
    public List<GoogleBookDTO> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String subject) {
        return googleBooksService.searchBooks(title, author, subject);
    }

    /**
     * ✨ NOVO: Endpoint para buscar o preço de um livro pelo ISBN.
     * Retorna 200 OK com os dados de venda ou 404 Not Found se não encontrar.
     * Ex: GET /api/external/books/price?isbn=9788532530837
     */
    @GetMapping("/price")
    public ResponseEntity<SaleInfo> getBookPriceByIsbn(@RequestParam String isbn) {
        Optional<SaleInfo> saleInfoOpt = googleBooksService.findBookPriceByIsbn(isbn);

        // Retorna o objeto SaleInfo com status 200 se presente, ou status 404 se ausente.
        return saleInfoOpt
                .map(ResponseEntity::ok) // Se o Optional contém valor, faz .ok(valor)
                .orElseGet(() -> ResponseEntity.notFound().build()); // Se vazio, faz .notFound()
    }

    /**
     * ✨ NOVO: Endpoint para obter recomendações com base no título de um livro.
     * Ex: GET /api/external/books/recommendations?title=O+Poder+do+Hábito
     */
    @GetMapping("/recommendations")
    public List<GoogleBookDTO> getRecommendations(@RequestParam String title) {
        return googleBooksService.findRecommendationsByTitle(title);
    }
}