package com.example.lesson_89.service;

import com.example.lesson_89.dto.CourseDTO;
import com.example.lesson_89.dto.CourseFilterDTO;
import com.example.lesson_89.dto.StudentDTO;
import com.example.lesson_89.dto.StudentFilterDTO;
import com.example.lesson_89.entity.CourseEntity;
import com.example.lesson_89.entity.StudentEntity;
import com.example.lesson_89.exp.ItemNotFoundException;
import com.example.lesson_89.repository.CourseRepository;
import com.example.lesson_89.repository.CustomCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CustomCourseRepository customCourseRepository;
    public CourseDTO add(CourseDTO dto) {
        CourseEntity entity = toEntity(dto);

        courseRepository.save(entity);
        dto.setId(entity.getId());

        return dto;
    }
    public List<CourseDTO> getAll(){
        Iterable<CourseEntity> iterable = courseRepository.findAll();
        List<CourseDTO> dtoList = new LinkedList<>();
        iterable.forEach(entity -> {
            dtoList.add(toDTO(entity));
        });
        return dtoList;
    }
    public CourseDTO getById(Integer id) {
        Optional<CourseEntity> optional = courseRepository.findById(id);
        if (optional.isEmpty()) {
            throw  new ItemNotFoundException("Course not found");
        }
        CourseEntity entity = optional.get();
        return toDTO(entity);
    }
    public Boolean update(Integer id, CourseDTO course) {
        int effectedRows = courseRepository.update(id, course.getName(), course.getPrice(), course.getDuration());
        return effectedRows != 0;
    }
    /*public Boolean update2(Integer id, CourseDTO course) {
        Optional<CourseEntity> optional = courseRepository.findById(id);
        if (optional.isPresent()) {
            CourseEntity entity = optional.get();
            entity.setName(course.getName());
            entity.setDuration(course.getDuration());
            entity.setPrice(course.getPrice());
            entity.setCreatedDate(LocalDateTime.now());

            courseRepository.save(entity);
            return true;
        }
        return false;
    }*/
    public Boolean delete(Integer id) {
        Optional<CourseEntity> optional = courseRepository.findById(id);
        if (optional.isEmpty()) {
            throw  new ItemNotFoundException("Course not found");
        }
        courseRepository.deleteById(id);
        return true;
    }
    public List<CourseDTO> GetCourseByPrice(Integer from, Integer to){
        Iterable<CourseEntity> iterable = courseRepository.findByPriceBetween(from, to);
        List<CourseDTO> dtoList = new LinkedList<>();

        iterable.forEach(entity -> {
            dtoList.add(toDTO(entity));
        });

        return dtoList;
    }
    public List<CourseDTO> getByDateBetween(LocalDate from, LocalDate to) {
        LocalDateTime from1 = LocalDateTime.of(from, LocalTime.MIN);
        LocalDateTime to1 = LocalDateTime.of(to, LocalTime.MAX);

        Iterable<CourseEntity> iterable = courseRepository.findByCreatedDateBetween(from1, to1);
        List<CourseDTO> dtoList = new LinkedList<>();

        iterable.forEach(entity -> {
            dtoList.add(toDTO(entity));
        });

        return dtoList;
    }
    public PageImpl<CourseDTO> coursePagination(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "createdDate");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CourseEntity> pageObj = courseRepository.findAll(pageable);
        List<CourseDTO> courseDTOList = pageObj.stream().map(this::toDTO).collect(Collectors.toList());
        return new PageImpl<>(courseDTOList, pageable, pageObj.getTotalElements());
    }
    public PageImpl<CourseDTO> studentPaginationByPrice(Integer price, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdDate"));
        Page<CourseEntity> pageObj = courseRepository.findByPrice(price, pageable);
        List<CourseDTO> courseDTOList = pageObj.stream().map(this::toDTO).collect(Collectors.toList());
        return new PageImpl<>(courseDTOList, pageable, pageObj.getTotalElements());
    }
    public PageImpl<CourseDTO> studentPaginationByPriceBetween(Integer from, Integer to, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdDate"));
        Page<CourseEntity> pageObj = courseRepository.findByPriceBetween(from, to, pageable);
        List<CourseDTO> courseDTOList = pageObj.stream().map(this::toDTO).collect(Collectors.toList());
        return new PageImpl<>(courseDTOList, pageable, pageObj.getTotalElements());
    }
    public PageImpl<CourseDTO> filter(CourseFilterDTO filterDTO, int page, int size) {
        customCourseRepository.filter(filterDTO, page, size);
        return null;
    }

    public CourseDTO toDTO(CourseEntity entity){
        CourseDTO dto = new CourseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setPrice(entity.getPrice());
        dto.setDuration(entity.getDuration());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }


    public CourseEntity toEntity(CourseDTO dto){
        CourseEntity entity = new CourseEntity();
        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        entity.setDuration(dto.getDuration());
        entity.setCreatedDate(LocalDateTime.now());

        return entity;
    }
}
