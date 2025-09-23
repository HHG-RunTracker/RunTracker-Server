package com.runtracker.global.util.message;

import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Locale;

@RequiredArgsConstructor
@Component
public class Messages {

    private final MessageSource messageSource;

    private MessageSourceAccessor accessor;

    @PostConstruct
    private void init() {
        accessor = new MessageSourceAccessor(messageSource, Locale.getDefault());
    }

    public String get(String code) {
        return accessor.getMessage(code);
    }

    public String get(@NonNull String code, @NonNull Object... messages) {
        return accessor.getMessage(code, Arrays.stream(messages).toArray());
    }
}