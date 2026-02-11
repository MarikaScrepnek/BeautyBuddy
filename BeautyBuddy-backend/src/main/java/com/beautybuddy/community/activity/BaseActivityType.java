package com.beautybuddy.community.activity;

import com.beautybuddy.common.entity.ForeignKeyIdEntity;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;

@MappedSuperclass
public abstract class BaseActivityType extends ForeignKeyIdEntity {
    @Id
    @OneToOne
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    public Activity getActivity() {
        return activity;
    }
    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected Long getForeignKeyId() {
        return activity.getId();
    }
}
