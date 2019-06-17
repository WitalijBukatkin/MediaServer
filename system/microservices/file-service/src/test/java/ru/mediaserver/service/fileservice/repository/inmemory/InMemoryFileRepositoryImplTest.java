package ru.mediaserver.service.fileservice.repository.inmemory;

import org.springframework.boot.test.context.SpringBootTest;
import ru.mediaserver.service.fileservice.repository.AbstractFileRepositoryTest;

@SpringBootTest(classes = InMemoryFileRepositoryImpl.class)
public class InMemoryFileRepositoryImplTest extends AbstractFileRepositoryTest {
}
