package com.yho.urlshrinker.domain;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record LongUrl (@NotNull @NotEmpty @URL String url) {

}
