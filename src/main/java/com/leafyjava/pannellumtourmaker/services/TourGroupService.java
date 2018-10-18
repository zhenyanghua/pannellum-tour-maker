package com.leafyjava.pannellumtourmaker.services;

import com.leafyjava.pannellumtourmaker.domains.TourGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TourGroupService {
    TourGroup findOne(String groupName);
    Page<TourGroup> findAll(Pageable pageable);
    List<TourGroup> findAll();
}
