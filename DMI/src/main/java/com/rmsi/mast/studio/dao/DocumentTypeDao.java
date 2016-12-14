package com.rmsi.mast.studio.dao;

import com.rmsi.mast.studio.domain.DocumentType;

public interface DocumentTypeDao extends GenericDAO<DocumentType, String> {
    DocumentType getTypeByAttributeOptionId(int optId);
}
