package com.yho.urlshrinker.domain;

import org.hibernate.validator.constraints.URL;
import org.springframework.lang.NonNull;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record LongUrl (@NonNull @NotNull @NotEmpty @URL String url) {

}
