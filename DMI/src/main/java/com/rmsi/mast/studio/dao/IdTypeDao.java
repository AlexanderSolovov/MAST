package com.rmsi.mast.studio.dao;

import com.rmsi.mast.studio.domain.IdType;

public interface IdTypeDao extends GenericDAO<IdType, String> {
    IdType getTypeByAttributeOptionId(int optId);
}
