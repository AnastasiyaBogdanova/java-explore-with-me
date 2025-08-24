package ru.yandex.practicum.ewmmainservice.main.controller.compilation;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewmmainservice.main.dto.compilation.CompilationDto;
import ru.yandex.practicum.ewmmainservice.main.dto.compilation.NewCompilationDto;
import ru.yandex.practicum.ewmmainservice.main.dto.compilation.UpdateCompilationRequest;
import ru.yandex.practicum.ewmmainservice.main.service.compilation.AdminCompilationServiceImpl;


@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminCompilationController {
    private final AdminCompilationServiceImpl compilationServiceImpl;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("Creating new compilation: {}", newCompilationDto);
        return compilationServiceImpl.createCompilation(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable @Positive Long compId) {
        log.info("Deleting compilation with id: {}", compId);
        compilationServiceImpl.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable @Positive Long compId,
                                            @Valid @RequestBody(required = false) UpdateCompilationRequest updateRequest) {
        log.info("Updating compilation with id: {}, data: {}", compId, updateRequest);

        return compilationServiceImpl.updateCompilation(compId, updateRequest);
    }
}