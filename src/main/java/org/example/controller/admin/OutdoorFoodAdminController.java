package org.example.controller.admin;

import java.util.List;
import org.example.dto.response.OutdoorFoodOptionResponse;
import org.example.dto.response.UploadResponse;
import org.example.entity.outdoor.OutdoorFood;
import org.example.mapper.OutdoorFoodMapper;
import org.example.repository.OutdoorFoodRepository;
import org.example.repository.OutdoorThemeRepository;
import org.example.service.upload.FileUpLoadService;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin/outdoor-foods")
public class OutdoorFoodAdminController {

    private final OutdoorFoodRepository foods;
    private final OutdoorThemeRepository themes;
    private final FileUpLoadService files;

    public OutdoorFoodAdminController(
            OutdoorFoodRepository foods,
            OutdoorThemeRepository themes,
            FileUpLoadService files
    ) {
        this.foods = foods;
        this.themes = themes;
        this.files = files;
    }

    public record FoodRequest(
            String name,
            Integer themeId,
            String imageUrl
    ) {}

    @GetMapping
    public List<OutdoorFoodOptionResponse> list() {
        return foods.findAll()
                .stream()
                .map(OutdoorFoodMapper::toOptionResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public OutdoorFoodOptionResponse get(@PathVariable Integer id) {
        return OutdoorFoodMapper.toOptionResponse(find(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public OutdoorFoodOptionResponse create(
            @RequestBody FoodRequest request
    ) {
        return save(new OutdoorFood(), request);
    }

    @PutMapping("/{id}")
    @Transactional
    public OutdoorFoodOptionResponse update(
            @PathVariable Integer id,
            @RequestBody FoodRequest request
    ) {
        return save(find(id), request);
    }

    @PostMapping("/{id}/image")
    @Transactional
    public UploadResponse uploadImage(
            @PathVariable Integer id,
            @RequestParam("file") MultipartFile file
    ) {
        OutdoorFood food = find(id);

        String url = files.saveImage(file, "outdoor-food");

        food.setImageUrl(url);
        foods.saveAndFlush(food);

        return new UploadResponse(url);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void delete(@PathVariable Integer id) {
        foods.delete(find(id));
        foods.flush();
    }

    private OutdoorFood find(Integer id) {
        return foods.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Outdoor food not found"
                ));
    }

    private OutdoorFoodOptionResponse save(
            OutdoorFood food,
            FoodRequest request
    ) {
        if (request == null
                || request.name() == null
                || request.name().isBlank()
                || request.name().trim().length() > 255) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Food name must contain 1 to 255 characters"
            );
        }

        if (request.themeId() != null
                && !themes.existsById(request.themeId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Theme does not exist"
            );
        }

        food.setName(request.name().trim());
        food.setThemeId(request.themeId());

        // Giữ ảnh hiện tại nếu request không truyền imageUrl.
        if (request.imageUrl() != null) {
            food.setImageUrl(
                    request.imageUrl().isBlank()
                            ? null
                            : request.imageUrl().trim()
            );
        }

        return OutdoorFoodMapper.toOptionResponse(
                foods.saveAndFlush(food)
        );
    }
}
