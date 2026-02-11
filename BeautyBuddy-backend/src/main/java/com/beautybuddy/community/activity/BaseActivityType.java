package com.beautybuddy.community.activity;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public abstract class BaseActivityType {
    @Id
    @ManyToOne
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;
}
