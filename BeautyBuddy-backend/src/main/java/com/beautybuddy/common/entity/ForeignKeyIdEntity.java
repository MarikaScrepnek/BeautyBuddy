package com.beautybuddy.common.entity;

import java.util.Objects;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ForeignKeyIdEntity {
    protected abstract Long getForeignKeyId();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ForeignKeyIdEntity that = (ForeignKeyIdEntity) o;
        return Objects.equals(getForeignKeyId(), that.getForeignKeyId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getForeignKeyId());
    }
}
