package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SequenceIdGenerator {
    private long nextId = 1;

    public long getNextId() {
        return nextId++;
    }
}
