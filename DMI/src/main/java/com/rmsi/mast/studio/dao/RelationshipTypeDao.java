package com.rmsi.mast.studio.dao;

import com.rmsi.mast.studio.domain.RelationshipType;

public interface RelationshipTypeDao extends GenericDAO<RelationshipType, String> {
    RelationshipType getTypeByAttributeOptionId(int optId);
}
