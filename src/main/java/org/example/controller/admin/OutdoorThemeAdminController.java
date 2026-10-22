package org.example.controller.admin;

import java.util.List;
import org.example.entity.outdoor.OutdoorTheme;
import org.example.repository.OutdoorThemeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin/outdoor-themes")
public class OutdoorThemeAdminController {
    private final OutdoorThemeRepository themes;
    public OutdoorThemeAdminController(OutdoorThemeRepository themes) { this.themes = themes; }
    public record ThemeRequest(String name) {}

    @GetMapping
    public List<OutdoorTheme> list() { return themes.findAll(); }
    @GetMapping("/{id}")
    public OutdoorTheme get(@PathVariable Integer id) {
        return themes.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Theme not found"));
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public OutdoorTheme create(@RequestBody ThemeRequest request) { return save(new OutdoorTheme(), request); }
    @PutMapping("/{id}")
    @Transactional
    public OutdoorTheme update(@PathVariable Integer id, @RequestBody ThemeRequest request) { return save(get(id), request); }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void delete(@PathVariable Integer id) { themes.delete(get(id)); themes.flush(); }

    private OutdoorTheme save(OutdoorTheme theme, ThemeRequest request) {
        if (request == null || request.name() == null || request.name().isBlank() || request.name().trim().length() > 100)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Theme name must contain 1 to 100 characters");
        String name = request.name().trim();
        if (themes.findAll().stream().anyMatch(t -> name.equalsIgnoreCase(t.getName()) && !t.getId().equals(theme.getId())))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Theme name already exists");
        theme.setName(name);
        return themes.saveAndFlush(theme);
    }
}
