package com.leafyjava.pannellumtourmaker.services;

import java.io.Serializable;
import java.util.List;

public interface RawService<T extends Serializable> {
    List<T> findAllPaginatedAndSorted(int page, int size, String sortBy, String sortOrder);
}
