package ru.yandex.practicum.ewmmainservice.main.service.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewmmainservice.main.dto.compilation.CompilationDto;
import ru.yandex.practicum.ewmmainservice.main.dto.compilation.NewCompilationDto;
import ru.yandex.practicum.ewmmainservice.main.dto.compilation.UpdateCompilationRequest;
import ru.yandex.practicum.ewmmainservice.main.exception.NotFoundException;
import ru.yandex.practicum.ewmmainservice.main.mapper.CompilationMapper;
import ru.yandex.practicum.ewmmainservice.main.model.Compilation;
import ru.yandex.practicum.ewmmainservice.main.model.Event;
import ru.yandex.practicum.ewmmainservice.main.repository.CompilationRepository;
import ru.yandex.practicum.ewmmainservice.main.repository.EventRepository;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminCompilationServiceImpl implements AdminCompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        if (compilationRepository.existsByTitle(newCompilationDto.getTitle())) {
            throw new IllegalArgumentException("Compilation with this title already exists");
        }

        Compilation compilation = CompilationMapper.toEntity(newCompilationDto);

        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            Set<Event> events = new HashSet<>(eventRepository.findAllById(newCompilationDto.getEvents()));
            compilation.setEvents(events);
        }

        Compilation savedCompilation = compilationRepository.save(compilation);
        return CompilationMapper.toDto(savedCompilation);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Compilation with id=" + compId + " was not found");
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateRequest) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));

        if (updateRequest.getTitle() != null) {
            if (!updateRequest.getTitle().equals(compilation.getTitle()) &&
                    compilationRepository.existsByTitle(updateRequest.getTitle())) {
                throw new IllegalArgumentException("Compilation with title '" + updateRequest.getTitle() + "' already exists");
            }
            compilation.setTitle(updateRequest.getTitle());
        }

        if (updateRequest.getPinned() != null) {
            compilation.setPinned(updateRequest.getPinned());
        }

        if (updateRequest.getEvents() != null) {
            Set<Event> events = updateRequest.getEvents().isEmpty() ?
                    new HashSet<>() :
                    new HashSet<>(eventRepository.findAllById(updateRequest.getEvents()));
            compilation.setEvents(events);
        }

        Compilation updatedCompilation = compilationRepository.save(compilation);
        return CompilationMapper.toDto(updatedCompilation);
    }
}