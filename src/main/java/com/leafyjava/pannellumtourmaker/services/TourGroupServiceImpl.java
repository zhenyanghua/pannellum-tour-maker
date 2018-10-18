package com.leafyjava.pannellumtourmaker.services;

import com.leafyjava.pannellumtourmaker.domains.TourGroup;
import com.leafyjava.pannellumtourmaker.repositories.TourGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TourGroupServiceImpl implements TourGroupService {
    private TourGroupRepository tourGroupRepository;

    @Autowired
    public TourGroupServiceImpl(final TourGroupRepository tourGroupRepository) {
        this.tourGroupRepository = tourGroupRepository;
    }

    @Override
    public TourGroup findOne(final String groupName) {
        return tourGroupRepository.findOne(groupName);
    }

    @Override
    public Page<TourGroup> findAll(final Pageable pageable) {
        return tourGroupRepository.findAll(pageable);
    }

    @Override
    public List<TourGroup> findAll() {
        return tourGroupRepository.findAll(
            new Sort(Sort.Direction.ASC, "alias"));
    }
}
