package org.example.controller.admin;

import java.util.List;
import org.example.dto.request.CategoryAdminRequest;
import org.example.dto.response.CategoryResponse;
import org.example.entity.indoor.Category;
import org.example.mapper.CategoryMapper;
import org.example.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin/categories")
public class CategoryAdminController {
    private final CategoryRepository categories;

    public CategoryAdminController(CategoryRepository categories) {
        this.categories = categories;
    }

    @GetMapping
    public List<CategoryResponse> list() {
        return categories.findAll().stream().map(CategoryMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public CategoryResponse get(@PathVariable Integer id) {
        return CategoryMapper.toResponse(find(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(@RequestBody CategoryAdminRequest request) {
        String name = name(request);
        if (categories.existsByNameIgnoreCase(name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category already exists");
        }
        return CategoryMapper.toResponse(categories.save(new Category(name)));
    }

    @PutMapping("/{id}")
    public CategoryResponse update(@PathVariable Integer id, @RequestBody CategoryAdminRequest request) {
        var category = find(id);
        String name = name(request);
        if (!category.getName().equalsIgnoreCase(name) && categories.existsByNameIgnoreCase(name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category already exists");
        }
        category.setName(name);
        return CategoryMapper.toResponse(categories.save(category));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        categories.delete(find(id));
    }

    private Category find(Integer id) {
        return categories.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
    }

    private String name(CategoryAdminRequest request) {
        if (request == null || request.name() == null || request.name().isBlank()
                || request.name().trim().length() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category name is required (max 100)");
        }
        return request.name().trim();
    }
}
